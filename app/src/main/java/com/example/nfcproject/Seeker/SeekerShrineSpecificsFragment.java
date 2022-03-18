package com.example.nfcproject.Seeker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

//import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.example.nfcproject.MainActivity;
import com.example.nfcproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class SeekerShrineSpecificsFragment extends Fragment implements SensorEventListener {

    private static final int PERMISSION_ID = 44;
    //gps services



    GeomagneticField mGeomagneticField;
    Location myLocation = new Location("") ;
    Location myDestination = getDestination();
    MediaPlayer mp;
    MediaPlayer mp2;
    private FusedLocationProviderClient mFusedLocationClient;


    public SeekerShrineSpecificsFragment() {
        //required empty public constructor
    }
    public static SeekerShrineSpecificsFragment newInstance() {
        return new SeekerShrineSpecificsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment

        final View ShrineSpecifics = inflater.inflate(R.layout.shrine_specifics, container, false);
        Button back = ShrineSpecifics.findViewById(R.id.shrine_specifics_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekerShrineMenuFragment smf = SeekerShrineMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "SeekerShrineMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //jmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        ///guiding stuffs
        ///vvv not sure why this cant go above on create
        SensorManager mSensorManager =(SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        ////

        mp = MediaPlayer.create(getContext(), R.raw.conxiuguide);
        mp2 = MediaPlayer.create(getContext(),R.raw.conxiuguidedrmz);
        mp.start();
        mp2.start();
        mp.setLooping(true);
        mp2.setLooping(true);

        //BlastVisualizer mVisualizer = ShrineSpecifics.findViewById(R.id.blast);
       // mVisualizer.setAudioSessionId(mp.getAudioSessionId());

        return ShrineSpecifics;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        float trueHeading = Math.round(computeTrueNorth(sensorEvent.values[0]));
        float relativeBearing = myLocation.bearingTo(myDestination) + 360 - trueHeading;
        Log.d("True Heading: " , String.valueOf(trueHeading));
        Log.d("Relative Bearing: " , String.valueOf(relativeBearing));

        float leftVol = trueHeading / 1000;
        float rightVol = trueHeading / 1000;

        ///still has bugs facing forward
        //facing forward
        if (trueHeading > 315 && trueHeading < 45) {
            leftVol = 1;
            rightVol = 1;
            mp.setVolume(leftVol,rightVol);
        }
        //facing left
        if (trueHeading < 315 && trueHeading > 225) {
            leftVol = 0;
            rightVol = 1;
            mp.setVolume(leftVol,rightVol);
        }
        //facing backwards
        if (trueHeading < 225 && trueHeading > 135) {
            leftVol = 0;
            rightVol = 0;
            mp.setVolume(leftVol,rightVol);
        }
        //facing right
        if (trueHeading < 135 && trueHeading > 45) {
            leftVol = 1;
            rightVol = 0;
            mp.setVolume(leftVol,rightVol);
        }

        Log.d("Left Volume : " , String.valueOf(leftVol));
        Log.d("Right Volume: " , String.valueOf(rightVol));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    ///gps methods :0
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //do something (check with tut)
                           requestNewLocationData();
                           
                        }
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
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mGeomagneticField = new GeomagneticField((float)mLastLocation.getLatitude(),(float)mLastLocation.getLongitude(),(float)mLastLocation.getAltitude(), System.currentTimeMillis());
            

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

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    //helper guys
    private float computeTrueNorth(float heading) {
        if (mGeomagneticField != null) {
            return heading + mGeomagneticField.getDeclination();
        } else {
            return heading;
        }
    }
    private Location getDestination() {
        Location destination = new Location("");
        //west of me => 44.484869171461725, -73.23584437166608  expected 1770m, read 1761
        //east of me => 44.487011, -73.130027 expected 6.95 , read 6662
        destination.setLatitude(44.484861);
        destination.setLongitude(-73.23584);
        return destination;
    }

}
