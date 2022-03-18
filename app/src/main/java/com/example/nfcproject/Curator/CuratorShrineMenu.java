package com.example.nfcproject.Curator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfcproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CuratorShrineMenu extends Fragment {

    public static final String URL = "http://ec2-18-190-157-121.us-east-2.compute.amazonaws.com:3000/curator-new";
    //***   Volley Utils    ***//
    // Volley Request queue
    RequestQueue mQueue;

    //***   Shared Prefs    ***//
    public static final String SHARED_PREF = "sharedPref";
    public static final String JWT = "jwt";
    SharedPreferences prefs;

    //***   Location Services   ***//
    private FusedLocationProviderClient fusedLocationClient;
    private GeomagneticField geomagneticField;
    private static final int PERMISSION_ID = 44;
    private String latCord;
    private String lonCrod;

    public CuratorShrineMenu() {
        //required empty public constructor
    }

    static CuratorShrineMenu newInstance() {
        return new CuratorShrineMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorShrineMenu = inflater.inflate(R.layout.curator_shrine_menu, container, false);

        prefs = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        mQueue = Volley.newRequestQueue(getActivity());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Button back = curatorShrineMenu.findViewById(R.id.curator_shrine_menu_back_button); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorJourneyMenu cjmf = CuratorJourneyMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, cjmf, "CuratorJourneyMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //cjmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button testShrine = curatorShrineMenu.findViewById(R.id.curator_test_shrine);
        testShrine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorShrineSpecificsMenu cssmf = CuratorShrineSpecificsMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, cssmf, "CuratorShrineSpecificsMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //cssmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button newShrine = curatorShrineMenu.findViewById(R.id.curator_new_shrine_button);
        newShrine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open sandwich with prompts, bind to new button and add to vertical linear layout
                showCreateShrineDialog();
            }
        });

        return curatorShrineMenu;
    } // End onCreate

    // GPS Methods
    // Thanks Matt
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        latCord = String.valueOf(location.getLatitude());
                        lonCrod = String.valueOf(location.getLongitude());
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            geomagneticField = new GeomagneticField((float)mLastLocation.getLatitude(),(float)mLastLocation.getLongitude(),(float)mLastLocation.getAltitude(), System.currentTimeMillis());

            latCord = String.valueOf(mLastLocation.getLatitude());
            lonCrod = String.valueOf(mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location on Android 10.0 and higher, use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //Function to display the new_saga_dialog
    public void showCreateShrineDialog() {
        requestNewLocationData();
        final Dialog dialog = new Dialog(getActivity());
        // Disable default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Allow user to cancel by pressing outside the box
        dialog.setCancelable(true);
        // Set the context
        dialog.setContentView(R.layout.new_shrine_dialog);

        // Initializing views from dialog
        final EditText shrineNameInput = dialog.findViewById(R.id.newShrineNameInput);
        final EditText shrineDescInput = dialog.findViewById(R.id.newShrineDescInput);
        final EditText shrineMeditationInput = dialog.findViewById(R.id.newShineMeditationInput);
        final EditText shrineAcceptedInput = dialog.findViewById(R.id.newShrineAcceptedResp);
        Button getCordsBtn = dialog.findViewById(R.id.newShineGetCordBtn);
        Button submitBtn = dialog.findViewById(R.id.newShrineSubmitBtn);

        submitBtn.setOnClickListener((v) -> {
            String shrineName = shrineNameInput.getText().toString().trim();
            String shrineDesc = shrineDescInput.getText().toString().trim();
            String shrineMeditation = shrineMeditationInput.getText().toString().trim();
            String shrineAcceptedResp = shrineAcceptedInput.getText().toString().trim();
            if (!shrineName.isEmpty() && !shrineDesc.isEmpty() && !shrineMeditation.isEmpty() && !shrineAcceptedResp.isEmpty()) {
                // Do stuff to add Shrine
                Toast.makeText(getActivity(), "A shrine would be created!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), "All fields must be filled out", Toast.LENGTH_SHORT).show();
            }
        });

        getCordsBtn.setOnClickListener((v) -> {
            // Get the current phone location here
            requestNewLocationData();
            getLastLocation();
            if(lonCrod == null || latCord == null) {
                Toast toast = Toast.makeText(getActivity(), "Failed to get current location\nTry again", Toast.LENGTH_SHORT);
                TextView toastV = toast.getView().findViewById(android.R.id.message);
                if( toastV != null) toastV.setGravity(Gravity.CENTER);
                toast.show();
            } else {
                Toast.makeText(getActivity(), "Lon: " + lonCrod + " Lat: " + latCord, Toast.LENGTH_SHORT).show();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     *
     * @param hash hash data for shrine
     * @param journeyID journey ID this shrine belongs to
     */
    public void createShrineReq(String hash, String journeyID) {
        JSONObject jsonShrine = new JSONObject();
        JSONObject jsonSendRequest = new JSONObject();
        try {
            jsonShrine.put("hash", hash);
            jsonShrine.put("journeyID", journeyID);
            jsonSendRequest.put("token", prefs.getString(JWT,""));
            jsonSendRequest.put("journey", jsonShrine);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,URL+"/newJourney",jsonSendRequest, response -> {
            Boolean successCheck = false;
            String returnMsg = "Error";
            try {
                successCheck = (Boolean) response.get("success");
                returnMsg = (String) response.get("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (successCheck == true) {
                // Shrine was created
            } else {

            }
        }, error -> Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show());
    }
}