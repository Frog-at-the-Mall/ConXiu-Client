package com.example.nfcproject;

import static android.content.ContentValues.TAG;

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
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;


///will be commenting more about this stuff soon once we get ui figured out so we know when to invoke fragments/activities.
///very similar to my "test media class" link to my github on discord

//adding comment here to check if privileges on github are sound

public class Guide extends AppCompatActivity implements SensorEventListener {

    int PERMISSION_ID = 44;
    private static final String TAG = "tag";
    private SensorManager mSensorManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeomagneticField mGeomagneticField;
    MediaPlayer mp;
    Location myLocation =  getDestination();
    Location myDestination;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //sensor init
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //google map
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //guide prep


        guidePrepare();

    }

    //add constructors for parameters like ==> Guide guide(getDestination(), getOtherStuff());
    public Guide() {

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.d(TAG, "onSensorChanged: confirmed " + event);
        //4 state radial audio tracker
        SensorEvent eventX = event;

        //main state (though im not sure how healthy it is to have guide always in a "listening state" through its SensorEventListener implementation
        // probably doesn't matter)
        guideGuiding(event, myLocation,myDestination);

        //i added this to put updates in but not sure this that's the safest way.
        getLastLocation();



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

        // code for system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last location from FusedLocationClient object
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            requestNewLocationData();


                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location on Android 10.0 and higher, use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    //useless method for testing
    public int getPermID(){
        return PERMISSION_ID;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //GUIDE STATESSTATESSTATESSTATESSTATESSTATESSTATES///
    //preparing state i.e. gathering mediafiles
    public void guidePrepare(){
        mp = new MediaPlayer();
        try {
            mp.prepare();
            mp.getMetrics();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //guiding state from which all things will probably grow.
    public void guideGuiding (SensorEvent event, Location myLocation, Location myDestination){

        float degree = Math.round(event.values[0]);
        float leftVol = degree/1000;
        float rightVol= degree /1000;

        ///still has bugs facing forward
        //facing forward
        if(degree > 315 && degree < 45){
            leftVol = 1;
            rightVol = 1;
        }
        //facing left
        if(degree < 315 && degree > 225){
            leftVol = 0;
            rightVol = 1;
        }
        //facing backwards
        if(degree < 225 && degree > 135){
            leftVol = 0;
            rightVol = 0;
        }
        //facing right
        if(degree < 135 && degree > 45){
            leftVol = 1;
            rightVol =0;
        }

        mp.setVolume(leftVol,rightVol);
        Log.d(TAG, "onSensorChanged: volume");

        ///use heading and bearing to find path
        float bearing = myLocation.bearingTo(myDestination);


    }

    //state in which guide is completing its task
    public void guideFinalizing(){

        mp.release();

    }
    ///state in which guide is on the boundary between to proximity zones.
    public void guideBoundary(){

    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Use the magnetic field to compute true (geographic) north from the specified heading
     * relative to magnetic north.
     *
     * @param heading the heading (in degrees) relative to magnetic north
     * @return the heading (in degrees) relative to true north
     */
    private float computeTrueNorth(float heading) {
        if (mGeomagneticField != null) {
            return heading + mGeomagneticField.getDeclination();
        } else {
            return heading;
        }
    }

    ////helper function subject to change once we can receive destination info
    private Location getDestination(){
        Location destination = new Location("");
        //west of me => 44.484869171461725, -73.23584437166608  expected 1770m, read 1761
        //east of me => 44.487011, -73.130027 expected 6.95 , read 6662
        destination.setLatitude(44.487011);
        destination.setLongitude(-73.130027);
        return destination;
    }


}

