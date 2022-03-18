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

import java.nio.Buffer;

public class CuratorJourneyMenu extends Fragment {

    public static final String URL = "http://ec2-18-190-157-121.us-east-2.compute.amazonaws.com:3000/curator-new";
    //***   Volley Utils    ***//
    // Volley Request queue
    RequestQueue mQueue;

    //***   Shared Prefs    ***//
    public static final String SHARED_PREF = "sharedPref";
    public static final String JWT = "jwt";
    SharedPreferences prefs;

    public CuratorJourneyMenu() {
        //required empty public constructor
    }

    static CuratorJourneyMenu newInstance() {
        return new CuratorJourneyMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorJourneyMenu = inflater.inflate(R.layout.curator_journey_menu, container, false);

        prefs = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        mQueue = Volley.newRequestQueue(getActivity());

        Button back = curatorJourneyMenu.findViewById(R.id.curator_journey_menu_back_button); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorSagaMenu csmf = CuratorSagaMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, csmf, "CuratorSagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //smf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button testJourney = curatorJourneyMenu.findViewById(R.id.curator_test_journey_button);
        testJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorShrineMenu csmf = CuratorShrineMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, csmf, "CuratorShrineMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //smf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button newJourney = curatorJourneyMenu.findViewById(R.id.curator_new_journey_button);
        newJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open sandwich with prompts, bind to new button and add to vertical linear layout
                showCreateJourneyDialog();
            }
        });

        return curatorJourneyMenu;
    } // End onCreate

    //Function to display the new_saga_dialog
    public void showCreateJourneyDialog() {
        final Dialog dialog = new Dialog(getActivity());
        // Disable default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Allow user to cancel by pressing outside the box
        dialog.setCancelable(true);
        // Set the context
        dialog.setContentView(R.layout.new_journey_dialog);

        // Initializing views from dialog
        final EditText journeyNameInput = dialog.findViewById(R.id.newJourneyNameInput);
        final EditText journeyDescInput = dialog.findViewById(R.id.newJourneyDescInput);
        Button submitBtn = dialog.findViewById(R.id.newJourneySubmitBtn);

        submitBtn.setOnClickListener((v) -> {
            String journeyName = journeyNameInput.getText().toString().trim();
            String journeyDesc = journeyDescInput.getText().toString().trim();
            if(!journeyName.isEmpty() && !journeyDesc.isEmpty()) {
                // Do stuff to add Journey
                Toast.makeText(getActivity(), "A Journey would be created!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), "All fields must be filled out", Toast.LENGTH_SHORT).show();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     *
     * @param journeyName name of the Journey being created
     * @param journeyDesc description of the Journey
     * @param parentSagaID ID for the saga the Journey belongs to
     * @param byteData byte data stored in a buffer array
     */
    public void createJourneyReq(String journeyName, String journeyDesc, String parentSagaID, Buffer[] byteData) {
        JSONObject jsonJourney = new JSONObject();
        JSONObject jsonSendRequest = new JSONObject();
        try {
            jsonJourney.put("name", journeyName);
            jsonJourney.put("description", journeyDesc);
            jsonJourney.put("sagaID", parentSagaID);
            jsonJourney.put("byteDataBuf", byteData);
            jsonSendRequest.put("token", prefs.getString(JWT,""));
            jsonSendRequest.put("journey", jsonJourney);
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
                // Journey was created
            } else {

            }
        }, error -> Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show());

    }
}