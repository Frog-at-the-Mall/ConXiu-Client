package com.example.nfcproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class ShrineSpecificsFragment extends Fragment {

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

        //send volley request for shrine data (lat long).
        // will be using erics capsule to decrypt
        //start guide



        return ShrineSpecifics;
    }
}
