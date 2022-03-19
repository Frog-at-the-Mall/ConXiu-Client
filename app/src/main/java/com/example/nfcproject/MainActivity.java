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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfcproject.Curator.CuratorSagaMenu;
import com.example.nfcproject.DataRep.Encryption;
import com.example.nfcproject.DataRep.Saga;
import com.example.nfcproject.LoginAndSplash.InitialFragment;
import com.example.nfcproject.Seeker.SeekerSagaMenuFragment;
import com.example.nfcproject.Seeker.SeekerShrineSpecificsFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

//imports necessary for volley transactions

public class MainActivity extends AppCompatActivity {

    byte[][] journeyData;
    double[] nextShrineLocation;
    String meditation_message;

    //NFC
    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    //NFC rep
    private String rawNFCData;

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
        SeekerSagaMenuFragment smf = SeekerSagaMenuFragment.newInstance();
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
        wait(2000); //wait 2 seconds for user to see toast, maybe implement button and redirect on user click in the future
        startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
            if (checkPermission()) { //here is where we check for permissions... before invoking the method resolveIntent which begins our NFC blocks and GPS comparison ping
                try {
                    resolveIntent(intent);
                } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please allow permissions for use in the app.", Toast.LENGTH_SHORT).show();
            }
//        } else {
//            Toast.makeText(this, "Connect on the NODE screen", Toast.LENGTH_SHORT).show();
//        }
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

                //dumptagdata method is called!
                // NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                //NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                //msgs = new NdefMessage[]{msg};
            }

            //here's where we code in the functionality for NFC
            //1st option is scanning a new saga in seeker new saga screen
            NewSagaSelectionFragment nssfFragment = (NewSagaSelectionFragment)getSupportFragmentManager().findFragmentByTag("NewSagaSelectionFragment");
            //2nd option is scanning a shrine in the shrinespecificsfragment screen
            SeekerShrineSpecificsFragment sssfFragment = (SeekerShrineSpecificsFragment)getSupportFragmentManager().findFragmentByTag("SeekerShrineSpecificsFragment");
            //3rd option is writing to a tag in the curator new saga creation screen, in sequence.
            CuratorSagaMenu csmFragment = (CuratorSagaMenu)getSupportFragmentManager().findFragmentByTag("CuratorSagaMenuFragment");
            //4th option is everywhere else, where we're met with a toast saying "you're not in the right screen"

            //scan new saga into client
            if (nssfFragment != null && nssfFragment.isVisible()) { //TODO: program functionality for adding a new saga in this case
                //displayMsgs(msgs);
                Toast.makeText(this,"You're on the new saga screen.", Toast.LENGTH_SHORT).show(); //test
                //handle the read tag to get hash
                //query server with that hash/id
                //receive saga and format it into objects properly
                //NewSagaSelectionFragment.addSagaToSharedPrefs(formattedSaga);
                //toast success
            }

            //scanning a shrine in nature
            else if (sssfFragment != null && sssfFragment.isVisible()) { //TODO: program functionality for handling a scanned shrine
                //displayMsgs(msgs);
                Toast.makeText(this,"You're on the shrine specifics fragment screen.", Toast.LENGTH_SHORT).show(); //test
            }

            //curator writing to shrines
            else if (csmFragment != null && csmFragment.isVisible()) { //TODO: program functionality for writing to shrines after a game is finalized by a curator
                //NFC write
                Toast.makeText(this,"You're on the curator new saga screen.", Toast.LENGTH_SHORT).show(); //test
            }

            else { //you're not on the right screen for NFC read or write
                Toast.makeText(this,"Scan a shrine on a valid screen.", Toast.LENGTH_SHORT).show();
            }


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
        SeekerSagaMenuFragment smf = SeekerSagaMenuFragment.newInstance();
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

    public boolean checkPermission() {
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

    //simple getter for the raw NFC data read from tag.
    public String getRawNFCData() {
        if (rawNFCData == null) {
            return "";
        }
        return rawNFCData;
    }

    //simple setter for the raw NFC data saved after reading a tag
    public void setRawNFCData(String data) {
        rawNFCData = data;
    }

//<<<<<<< HEAD
//=======
//>>>>>>> b91188f169aeb7a7bd8da9107709ed9087580fdc
}
