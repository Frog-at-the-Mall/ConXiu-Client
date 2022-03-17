package com.example.nfcproject.Curator;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfcproject.R;

import org.json.JSONException;
import org.json.JSONObject;

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

    //Function to display the new_saga_dialog
    public void showCreateShrineDialog() {
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
            if(!shrineName.isEmpty() && !shrineDesc.isEmpty() && !shrineMeditation.isEmpty() && !shrineAcceptedResp.isEmpty()) {
                // Do stuff to add Shrine
                Toast.makeText(getActivity(), "A shrine would be created!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), "All fields must be filled out", Toast.LENGTH_SHORT).show();
            }
        });

        getCordsBtn.setOnClickListener((v) -> {
            // Get the current phone location here
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