package com.example.nfcproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ProfileConfigFragment extends Fragment {

    public ProfileConfigFragment() {
        //required empty public constructor
    }

    static ProfileConfigFragment newInstance() {
        return new ProfileConfigFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment
        final View ProfileConfigMenu = inflater.inflate(R.layout.profile_config, container, false);

        Button back = ProfileConfigMenu.findViewById(R.id.profile_config_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SagaMenuFragment smf = SagaMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //smf.setLocalState("example state");         //just to carry over state as needed
            }
        });
        return ProfileConfigMenu;
    }
}