package com.example.nfcproject;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

//import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class ShrineSpecificsFragment extends Fragment implements SensorEventListener {

    //gps services
    private SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    private FusedLocationProviderClient FusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


    GeomagneticField mGeomagneticField;
    Location myLocation = new Location("") ;
    Location myDestination = getDestination();
    MediaPlayer mp;
    MediaPlayer mp2;


    public ShrineSpecificsFragment() {
        //required empty public constructor
    }
    static ShrineSpecificsFragment newInstance() {
        return new ShrineSpecificsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment

        final View ShrineSpecifics = inflater.inflate(R.layout.shrine_specifics, container, false);
        Button back = ShrineSpecifics.findViewById(R.id.shrine_specifics_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShrineMenuFragment smf = ShrineMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "ShrineMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //jmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        ///guiding stuffs
        ///vvv not sure why this cant go above on create
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
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
