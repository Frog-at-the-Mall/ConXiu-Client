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
import com.example.nfcproject.Seeker.SeekerSagaMenuFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class CuratorSagaMenu extends Fragment {

    public static final String URL = "http://ec2-18-190-157-121.us-east-2.compute.amazonaws.com:3000/curator-new";
    //***   Volley Utils    ***//
    // Volley Request queue
    RequestQueue mQueue;

    //***   Shared Prefs    ***//
    public static final String SHARED_PREF = "sharedPref";
    public static final String JWT = "jwt";
    SharedPreferences prefs;

    public CuratorSagaMenu() {
        //required empty public constructor
    }

    public static CuratorSagaMenu newInstance() {
        return new CuratorSagaMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorSagaMenu = inflater.inflate(R.layout.curator_saga_menu, container, false);

        mQueue = Volley.newRequestQueue(getActivity());
        prefs = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        Button back = curatorSagaMenu.findViewById(R.id.curator_saga_menu_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekerSagaMenuFragment smf = SeekerSagaMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //smf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button testSaga = curatorSagaMenu.findViewById(R.id.curator_test_saga_button);
        testSaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuratorJourneyMenu cjmf = CuratorJourneyMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, cjmf, "CuratorJourneyMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //cjmf.setLocalState("example state"); //carry over state as needed
            }
        });

        Button newSaga = curatorSagaMenu.findViewById(R.id.curator_new_saga_button);
        newSaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here will be logic to open a sandwich and gather a name for a saga, then creating a new button which

                //routes to the curator journey screen
                showCreateSagaDialog();
            }
        });

        return curatorSagaMenu;
    } // End onCreate

    //Function to display the new_saga_dialog
    public void showCreateSagaDialog() {
        final Dialog dialog = new Dialog(getActivity());
        // Disable default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Allow user to cancel by pressing outside the box
        dialog.setCancelable(true);
        // Set the context
        dialog.setContentView(R.layout.new_saga_dialog);

        // Initializing views from dialog
        final EditText sagaNameInput = dialog.findViewById(R.id.newSagaNameInput);
        final EditText sagaDescInput = dialog.findViewById(R.id.newSagaDescInput);
        Button submitBtn = dialog.findViewById(R.id.newSagaSubmitBtn);

        submitBtn.setOnClickListener((v) -> {
            String sagaName = sagaNameInput.getText().toString().trim();
            String sagaDesc = sagaDescInput.getText().toString().trim();
            Toast.makeText(getActivity(), "SagaName: {" + sagaName + "} "+ "sagaDes: {" + sagaDesc + "}", Toast.LENGTH_SHORT).show();
            if(!sagaName.isEmpty() && !sagaDesc.isEmpty()) {
                // Do stuff to add Saga
                Toast.makeText(getActivity(), "A Saga would be created!", Toast.LENGTH_SHORT).show();
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
     * @param sagaName name of the saga being created
     * @param sagaDesc short description of the saga
     */
    public void createSagaReq(String sagaName, String sagaDesc) {
        JSONObject jsonSaga = new JSONObject();
        JSONObject jsonSendRequest = new JSONObject();
        try {
            jsonSaga.put("name", sagaName);
            jsonSaga.put("description", sagaDesc);
            jsonSendRequest.put("token", prefs.getString(JWT,""));
            jsonSendRequest.put("saga", jsonSaga);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,URL+"/newSaga",jsonSendRequest, response -> {
            Boolean successCheck = false;
            String returnMsg = "Error";
            try {
                successCheck = (Boolean) response.get("success");
                returnMsg = (String) response.get("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (successCheck == true) {
                // Saga was created
            } else {

            }
        }, error -> Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show());

    }
}
