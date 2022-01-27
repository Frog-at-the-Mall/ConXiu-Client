package com.example.nfcproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;

//shrine fragment
public class NodeFragment extends Fragment {

    private String localPassed; //the node's state, how it will decide what specific information to populate
    private String nodePassed; //the node depends not only on which node in the local game we select, but also which local game we're playing
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String coords;

    public NodeFragment() {
        // Required empty public constructor
    }

    static NodeFragment newInstance() {
        return new NodeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("node_fragment", "skeleton complete");
        final View fragNode = inflater.inflate(R.layout.fragment_node, container, false);
        final TextView riddleBlock = fragNode.findViewById(R.id.riddleBlock);
        Button backToLocal = fragNode.findViewById(R.id.backToLocal);
        backToLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the desired node value to the localFragment so the right local game can be loaded.
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });
        locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                coords = location.getLatitude() + ", " + location.getLongitude();
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

        Button contentNode = fragNode.findViewById(R.id.contentNode);
        contentNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //button to tell user that NFC can now be scanned/verify input
                EditText answerBlock = fragNode.findViewById(R.id.answerBlock);
                TextView riddleBlock = fragNode.findViewById(R.id.riddleBlock);

                String answerBlockString = answerBlock.getText().toString();
                String riddleBlockString = riddleBlock.getText().toString();

                if (riddleBlockString.equals("")) {
                    Toast.makeText(getActivity(), "Find node and press phone to it", Toast.LENGTH_SHORT).show();
                }
                Log.i("node_answer", answerBlockString);
                if (answerBlockString.equals("orange")){ //correct answer
                    SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE); //write unlock node to shared preferences
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(localPassed+nodePassed, true); //shared prefs value is updated to reveal next node in ARG
                    editor.apply();
                    Toast.makeText(getActivity(), "The next node's location has been unlocked.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (riddleBlockString.equals("")) {
                        Toast.makeText(getActivity(), "Your input was not accepted. Please scan a node to obtain a clue, or try submitting again.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Your input was not accepted. Please try submitting again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        TextView locator = fragNode.findViewById(R.id.locatorFieldNode);
        locator.setText("Local: "+localPassed+"  Node: "+nodePassed+"\nCoordinates: 44.460050, -73.157703"); //to be updated when permanent node locations are determined
        return fragNode;
    }

    void setNodeState(String localState, String nodeState) {
        localPassed = localState;
        nodePassed = nodeState;
    }
}
