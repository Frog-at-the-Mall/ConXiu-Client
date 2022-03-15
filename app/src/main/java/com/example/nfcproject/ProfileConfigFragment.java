package com.example.nfcproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfcproject.Seeker.SeekerSagaMenuFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileConfigFragment extends Fragment {

    public ProfileConfigFragment() {
        //required empty public constructor
    }

    public static ProfileConfigFragment newInstance() {
        return new ProfileConfigFragment();
    }

    public static final String URL = "http://ec2-18-190-157-121.us-east-2.compute.amazonaws.com:3000/";

    //***   Shared Prefs    ***//
    public static final String SHARED_PREF = "sharedPref";
    public static final String JWT = "jwt";
    public static final String USERNAME = "username";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment
        final View ProfileConfigMenu = inflater.inflate(R.layout.profile_config, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String username = prefs.getString(USERNAME,"");

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());

        EditText editT_Username = ProfileConfigMenu.findViewById(R.id.editTextNewUsername);
        EditText editT_Password = ProfileConfigMenu.findViewById(R.id.editTextCurPassword);
        EditText editT_newPassword = ProfileConfigMenu.findViewById(R.id.editTextNewPassword);
        EditText editT_rePassword = ProfileConfigMenu.findViewById(R.id.editTextNewConfPassword);
        TextView usernameHeader = ProfileConfigMenu.findViewById(R.id.usernameHeader);

        usernameHeader.setText(username);

        Button back = ProfileConfigMenu.findViewById(R.id.profile_config_back); //back button
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

        Button submit = ProfileConfigMenu.findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String newUsername = editT_Username.getText().toString();
                String newPassword = editT_newPassword.getText().toString();
                String reNewPassword = editT_rePassword.getText().toString();
                String curPassword = editT_Password.getText().toString();
                String jwt = prefs.getString(JWT, "");

                if(!newUsername.isEmpty() && !newPassword.isEmpty() && !reNewPassword.isEmpty() && !curPassword.isEmpty()) {
                    if(newPassword.equals(reNewPassword)) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("token", jwt);
                            json.put("newUsername", newUsername);
                            json.put("newPassword", newPassword);
                            json.put("curPassword", curPassword);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,URL+"login/editUser",json, response -> {
                            Boolean successCheck = false;
                            String returnMsg = "Error";
                            try {
                                successCheck = (Boolean) response.get("success");
                                returnMsg = (String) response.get("msg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (successCheck == true) {
                                // Credentials changed
                                try {
                                    editor.putString(JWT, (String) response.get("token"));
                                    editor.putString(USERNAME, newUsername);
                                    editor.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getActivity(), "Credentials updated", Toast.LENGTH_SHORT).show();
                                SeekerSagaMenuFragment smf = SeekerSagaMenuFragment.newInstance();
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(getActivity(), returnMsg, Toast.LENGTH_SHORT).show();
                            }
                        }, error -> Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show());

                        mQueue.add(jsonRequest);
                    } else {
                        Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return ProfileConfigMenu;
    }
}