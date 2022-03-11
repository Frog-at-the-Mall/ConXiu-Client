package com.example.nfcproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfcproject.Capsule.Byte_Array;
import com.example.nfcproject.Capsule.Capsule;
import com.example.nfcproject.Capsule.Encryption;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

//imports necessary for volley transactions

public class MainActivity extends AppCompatActivity {

    byte[][] journeyData;
    double[] nextShrineLocation;
    String meditation_message;

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    //maybe change to public so shrineSpecificFrag can use it.
    private LocationManager locationManager;
    private LocationListener locationListener;
     Location deviceLocation;

    private Button welcome_login_btn;

    public static final String URL = "http://ec2-18-190-157-121.us-east-2.compute.amazonaws.com:3000/";


    // Volley Request queue
    RequestQueue mQueue;

    //***   Shared Prefs    ***//
    public static final String SHARED_PREF = "sharedPref";
    public static final String JWT = "jwt";
    public static final String USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar

        mQueue = Volley.newRequestQueue(this);

        //check to see if device has NFC hardware, and if it's enabled
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        //if no NFC hardware at all, user can't use the app.
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC hardware on this device", Toast.LENGTH_SHORT).show();
            try {
                wait(2000); // wait 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
            return;
        }

        //relay nfc intent to app as we receive input... kinda?
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //end of NFC setup for onCreate

        if (checkPermission()) {
            locationManager = (LocationManager) Objects.requireNonNull(getSystemService(LOCATION_SERVICE));

            Location LKL = locationManager.getLastKnownLocation("gps");

            if (LKL != null) {
                deviceLocation = new Location(LKL);
            }
            else {
                deviceLocation = new Location("LocationManager#GPS_PROVIDER");
            }
        }
        else {
            Toast.makeText(this, "You did not enable location, NFC and Internet permissions. App will not function properly.", Toast.LENGTH_LONG).show();
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                deviceLocation.setLatitude(location.getLatitude());
                deviceLocation.setLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Gets the JWT from shared prefs, if none it will be set to ""
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String jwt = prefs.getString(JWT, "");

        // If JWT is not "" check if the token is valid.
        // If not valid open initalfrag where user can login.
        if(jwt != "") {
            tokenCheck(jwt);
        } else {
            openInitialFrag();
        }

    } //End OnCreate

    public void onClick(View v) {
        SagaMenuFragment smf = SagaMenuFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                .commit();
    }

    //this will give priority to the foreground activity (MainActivity) when NFC info is obtained on the hardware, allowing this logic to parse it
    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                try {
                    showWirelessSettings();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    //toast to let user know to enable their NFC settings, redirect to their settings
    private void showWirelessSettings() throws InterruptedException {
        Toast.makeText(this, "Enable NFC on your device", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        //wait(2000); //wait 2 seconds for user to see toast, maybe implement button and redirect on user click in the future
        startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        NodeFragment testVisibility = (NodeFragment) getSupportFragmentManager().findFragmentByTag("NodeFragment");
        if (testVisibility != null && testVisibility.isAdded() && testVisibility.isVisible()) {
            if (checkPermission()) { //here is where we check for permissions... before invoking the method resolveIntent which begins our NFC blocks and GPS comparison ping
                try {
                    resolveIntent(intent);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Permissions for NFC and/or Fine Location Access have been denied. Please allow permissions for use in the app.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Connect on the NODE screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void resolveIntent(Intent intent) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                assert tag != null;

                //dumptagdata method is called!
                byte[] tagPayload = dumpTagData(tag).getBytes(); //dumptagdata method is called!
                SecretKey key = Encryption.secretKey_from_bytes(tagPayload);

                //read from journeyData using key from NFC tag
                journeyData = Capsule.peelLayer(journeyData[1], key);
                byte[] coordinateBytes = Arrays.copyOfRange(journeyData[0], 0, 2*Double.BYTES);
                nextShrineLocation = Byte_Array.byteArrayToCoords(coordinateBytes);
                byte[] meditationBytes = Arrays.copyOfRange(journeyData[0], 2*Double.BYTES, 256);
                meditation_message = Byte_Array.byteArrayToCharSequence(meditationBytes).toString();

                //dumptagdata method is called!
                // NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                //NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                //msgs = new NdefMessage[]{msg};
            }
            //displayMsgs(msgs);
        }
    }
    //write tag. not confirmed working, but at least it isn't throwing errors.
    //based on code from https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc
    public void writeTag(Tag tag, byte[] capsule) throws IOException {
    	NfcA tag1 = NfcA.get(tag);
    	try {
    	    tag1.connect();
            tag1.transceive(capsule);
            tag1.close();
    	} catch (IOException e) {
            e.printStackTrace();
        }
    	finally {
    	    tag1.close();
        }
    }

    public void openInitialFrag(){
        InitialFragment iff = InitialFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.replaceFrame, iff, "InitialFragment")
                .commit();
    }

    public void openSagaMenuFrag(){
        SagaMenuFragment smf = SagaMenuFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                .commit();
    }

    // Will send a request to check if the passed JWT is valid. If it is Sagamenu will be opened.
    public void tokenCheck(String jwt) {
        JSONObject json = new JSONObject();
        try {
            json.put("token", jwt);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,URL+"login/userVerify",json, response -> {
            try {
                if((boolean) response.get("success")) {
                    openSagaMenuFrag();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> openInitialFrag());

        mQueue.add(jsonRequest);
    }

//    **--old message parsing code--**
//    private void displayMsgs(NdefMessage[] msgs) {
//        if (msgs == null || msgs.length == 0)
//            return;
//
//        StringBuilder builder = new StringBuilder();
//        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
//        final int size = records.size();
//
//        for (int i = 0; i < size; i++) {
//            ParsedNdefRecord record = records.get(i);
//            String str = record.str();  //where str() is called and the classes are invoked to parse and create a string
//            builder.append(str).append("\n");
//        }
//
//        nfcHash = builder.toString();
//
//        nfcHash = nfcHash.trim(); //trim whitespace from gathered tag hash
//
//        //IMPORTANT FUTURE DESIGN NOTE
//        //call to web server with NfcHash as argument, get answer (riddle)
//        //I'll explain how it should work.
//        //user gets location, and sends it to server along with hash. if both values match, we get an answer from the server
//        //to implement in the future, for now we'll have placeholders stored in the app itself for comparison
//
//        TextView riddleBlock = findViewById(R.id.riddleBlock);
//
//        //Check user's location [ok since we can only access this block if both NFC and access fine location permissions have been given]
//
//        getLocation();
//        Log.i("coords", String.valueOf(deviceLocation));
//        if (isUserInCorrectLocation()) {
//            if (nfcHash.equals("secrethash")) {
//                //call to web server, get answer (riddle)
//                riddleBlock.setText("What color is an orange?"); //normally we would query the web server for this hard-coded string, but the web server is not yet implemented
//            } else {
//                riddleBlock.setText(R.string.bad_tag);
//            }
//        }
//        else {
//            riddleBlock.setText(R.string.bad_location);
//        }
//    }
//
    //gets raw data from the NFC tag
    //@param tag the tag we're scanning
    //@return the raw string data from the tag
    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize()).append(" bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: ").append(e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    // conversion utilities
    //converts the tag's raw data into hexadecimal
    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    //converts the tag's raw data into reverse hexadecimal
    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    //converts the tag's raw data into decimal
    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (byte aByte : bytes) {
            long value = aByte & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    //converts the tag's raw data into reverse decimal
    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    boolean checkPermission() {
        boolean islocationPermissionGiven = false;
        boolean isNfcPermissionGiven = false;
        boolean isInternetPermissionGiven = false;

        //location permission
        if (ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "This app will only read passive tags, and your location is never broadcast.",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        123);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        123);
                islocationPermissionGiven = true;
            }
        } else {
            islocationPermissionGiven = true;
        }

        //NFC permission
        if (ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.NFC)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "This app will only read passive tags to read stored static identifiers.",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .NFC},
                                        231);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .NFC},
                        231);
                isNfcPermissionGiven = true;
            }
        } else {
            isNfcPermissionGiven = true;
        }

        //internet permission
        if (ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.INTERNET)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Enabling Internet will permit communication with various supporting APIs.",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.INTERNET},
                                        312);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.INTERNET},
                        312);
                isInternetPermissionGiven = true;
            }
        } else {
            isInternetPermissionGiven = true;
        }

        return isNfcPermissionGiven && islocationPermissionGiven && isInternetPermissionGiven; //did user give all permissions?
    }

    //do not call this if user has not given ACCESS FINE LOCATION permission
    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    //is user within 25 metres of tag's known geolocation?
    private boolean isUserInCorrectLocation() {

        //
        //temporary hard-coded coordinates, will replace with server query later
        Location tagLocation = new Location("");
        //tagLocation.setLatitude(44.460050);
        //tagLocation.setLongitude(-73.157703);
        //temporary hard-coded coordinates, will replace with server query later
        //

        tagLocation.setLongitude(nextShrineLocation[0]);
        tagLocation.setLatitude(nextShrineLocation[1]);
        float distanceBetweenDeviceAndTag = deviceLocation.distanceTo(tagLocation);

        return distanceBetweenDeviceAndTag < 20;
    }

//<<<<<<< HEAD
//=======
//
//
////could be a server call down the road
//    private void setSharedPreferencesDefaults() {
//        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        //get defaults of all 81 nodes
//        editor.putBoolean("BRCT", Boolean.parseBoolean(getString(R.string.BRCT)));
//        editor.putBoolean("BRTL", Boolean.parseBoolean(getString(R.string.BRTL)));
//        editor.putBoolean("BRML", Boolean.parseBoolean(getString(R.string.BRML)));
//        editor.putBoolean("BRBR", Boolean.parseBoolean(getString(R.string.BRBR)));
//        editor.putBoolean("BRTR", Boolean.parseBoolean(getString(R.string.BRTR)));
//        editor.putBoolean("BRMR", Boolean.parseBoolean(getString(R.string.BRMR)));
//        editor.putBoolean("BRCB", Boolean.parseBoolean(getString(R.string.BRCB)));
//        editor.putBoolean("BRBL", Boolean.parseBoolean(getString(R.string.BRBL)));
//        editor.putBoolean("BRCM", Boolean.parseBoolean(getString(R.string.BRCM))); Log.i("main activity defaults method", getString(R.string.BRCM));
//
//        editor.putBoolean("BLCT", Boolean.parseBoolean(getString(R.string.BLCT)));
//        editor.putBoolean("BLTL", Boolean.parseBoolean(getString(R.string.BLTL)));
//        editor.putBoolean("BLML", Boolean.parseBoolean(getString(R.string.BLML)));
//        editor.putBoolean("BLBR", Boolean.parseBoolean(getString(R.string.BLBR)));
//        editor.putBoolean("BLTR", Boolean.parseBoolean(getString(R.string.BLTR)));
//        editor.putBoolean("BLMR", Boolean.parseBoolean(getString(R.string.BLMR)));
//        editor.putBoolean("BLCB", Boolean.parseBoolean(getString(R.string.BLCB)));
//        editor.putBoolean("BLBL", Boolean.parseBoolean(getString(R.string.BLBL)));
//        editor.putBoolean("BLCM", Boolean.parseBoolean(getString(R.string.BLCM)));
//
//        editor.putBoolean("CBCT", Boolean.parseBoolean(getString(R.string.CBCT)));
//        editor.putBoolean("CBTL", Boolean.parseBoolean(getString(R.string.CBTL)));
//        editor.putBoolean("CBML", Boolean.parseBoolean(getString(R.string.CBML)));
//        editor.putBoolean("CBBR", Boolean.parseBoolean(getString(R.string.CBBR)));
//        editor.putBoolean("CBTR", Boolean.parseBoolean(getString(R.string.CBTR)));
//        editor.putBoolean("CBMR", Boolean.parseBoolean(getString(R.string.CBMR)));
//        editor.putBoolean("CBCB", Boolean.parseBoolean(getString(R.string.CBCB)));
//        editor.putBoolean("CBBL", Boolean.parseBoolean(getString(R.string.CBBL)));
//        editor.putBoolean("CBCM", Boolean.parseBoolean(getString(R.string.CBCM)));
//
//        editor.putBoolean("CMCT", Boolean.parseBoolean(getString(R.string.CMCT)));
//        editor.putBoolean("CMTL", Boolean.parseBoolean(getString(R.string.CMTL)));
//        editor.putBoolean("CMML", Boolean.parseBoolean(getString(R.string.CMML)));
//        editor.putBoolean("CMBR", Boolean.parseBoolean(getString(R.string.CMBR)));
//        editor.putBoolean("CMTR", Boolean.parseBoolean(getString(R.string.CMTR)));
//        editor.putBoolean("CMMR", Boolean.parseBoolean(getString(R.string.CMMR)));
//        editor.putBoolean("CMCB", Boolean.parseBoolean(getString(R.string.CMCB)));
//        editor.putBoolean("CMBL", Boolean.parseBoolean(getString(R.string.CMBL)));
//        editor.putBoolean("CMCM", Boolean.parseBoolean(getString(R.string.CMCM)));
//
//        editor.putBoolean("CTCT", Boolean.parseBoolean(getString(R.string.CTCT)));
//        editor.putBoolean("CTTL", Boolean.parseBoolean(getString(R.string.CTTL)));
//        editor.putBoolean("CTML", Boolean.parseBoolean(getString(R.string.CTML)));
//        editor.putBoolean("CTBR", Boolean.parseBoolean(getString(R.string.CTBR)));
//        editor.putBoolean("CTTR", Boolean.parseBoolean(getString(R.string.CTBR)));
//        editor.putBoolean("CTMR", Boolean.parseBoolean(getString(R.string.CTMR)));
//        editor.putBoolean("CTCB", Boolean.parseBoolean(getString(R.string.CTCB)));
//        editor.putBoolean("CTBL", Boolean.parseBoolean(getString(R.string.CTBL)));
//        editor.putBoolean("CTCM", Boolean.parseBoolean(getString(R.string.CTCM)));
//
//        editor.putBoolean("TRCT", Boolean.parseBoolean(getString(R.string.TRCT)));
//        editor.putBoolean("TRTL", Boolean.parseBoolean(getString(R.string.TRTL)));
//        editor.putBoolean("TRML", Boolean.parseBoolean(getString(R.string.TRML)));
//        editor.putBoolean("TRBR", Boolean.parseBoolean(getString(R.string.TRBR)));
//        editor.putBoolean("TRTR", Boolean.parseBoolean(getString(R.string.TRTR)));
//        editor.putBoolean("TRMR", Boolean.parseBoolean(getString(R.string.TRMR)));
//        editor.putBoolean("TRCB", Boolean.parseBoolean(getString(R.string.TRCB)));
//        editor.putBoolean("TRBL", Boolean.parseBoolean(getString(R.string.TRBL)));
//        editor.putBoolean("TRCM", Boolean.parseBoolean(getString(R.string.TRCM)));
//
//        editor.putBoolean("TLCT", Boolean.parseBoolean(getString(R.string.TLCT)));
//        editor.putBoolean("TLTL", Boolean.parseBoolean(getString(R.string.TLTL)));
//        editor.putBoolean("TLML", Boolean.parseBoolean(getString(R.string.TLML)));
//        editor.putBoolean("TLBR", Boolean.parseBoolean(getString(R.string.TLBR)));
//        editor.putBoolean("TLTR", Boolean.parseBoolean(getString(R.string.TLTR)));
//        editor.putBoolean("TLMR", Boolean.parseBoolean(getString(R.string.TLMR)));
//        editor.putBoolean("TLCB", Boolean.parseBoolean(getString(R.string.TLCB)));
//        editor.putBoolean("TLBL", Boolean.parseBoolean(getString(R.string.TLBL)));
//        editor.putBoolean("TLCM", Boolean.parseBoolean(getString(R.string.TLCM)));
//
//        editor.putBoolean("MRCT", Boolean.parseBoolean(getString(R.string.MRCT)));
//        editor.putBoolean("MRTL", Boolean.parseBoolean(getString(R.string.MRTL)));
//        editor.putBoolean("MRML", Boolean.parseBoolean(getString(R.string.MRML)));
//        editor.putBoolean("MRBR", Boolean.parseBoolean(getString(R.string.MRBR)));
//        editor.putBoolean("MRTR", Boolean.parseBoolean(getString(R.string.MRTR)));
//        editor.putBoolean("MRMR", Boolean.parseBoolean(getString(R.string.MRMR)));
//        editor.putBoolean("MRCB", Boolean.parseBoolean(getString(R.string.MRCB)));
//        editor.putBoolean("MRBL", Boolean.parseBoolean(getString(R.string.MRBL)));
//        editor.putBoolean("MRCM", Boolean.parseBoolean(getString(R.string.MRCM)));
//
//        editor.putBoolean("MLCT", Boolean.parseBoolean(getString(R.string.MLCT)));
//        editor.putBoolean("MLTL", Boolean.parseBoolean(getString(R.string.MLTL)));
//        editor.putBoolean("MLML", Boolean.parseBoolean(getString(R.string.MLML)));
//        editor.putBoolean("MLBR", Boolean.parseBoolean(getString(R.string.MLBR)));
//        editor.putBoolean("MLTR", Boolean.parseBoolean(getString(R.string.MLTR)));
//        editor.putBoolean("MLMR", Boolean.parseBoolean(getString(R.string.MLMR)));
//        editor.putBoolean("MLCB", Boolean.parseBoolean(getString(R.string.MLCB)));
//        editor.putBoolean("MLBL", Boolean.parseBoolean(getString(R.string.MLBL)));
//        editor.putBoolean("MLCM", Boolean.parseBoolean(getString(R.string.MLCM)));
//        editor.apply();
//    }
//
//>>>>>>> b91188f169aeb7a7bd8da9107709ed9087580fdc
}
