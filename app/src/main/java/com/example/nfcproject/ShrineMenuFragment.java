package com.example.nfcproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ShrineMenuFragment extends Fragment {

    public ShrineMenuFragment() {
        //required empty public constructor
    }
    static ShrineMenuFragment newInstance() {
        return new ShrineMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the global fragment
        final View shrineMenu = inflater.inflate(R.layout.shrine_menu, container, false);
        Button toShrineSpecifics = shrineMenu.findViewById(R.id.test_shrine_button);
        toShrineSpecifics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShrineSpecificsFragment ssf = ShrineSpecificsFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, ssf, "ShrineSpecificsFragment")
                        .addToBackStack(null)
                        .commit();
                //ssf.setLocalState("example state");         //just to carry over state as needed
            }
        });
        return shrineMenu;
    }
}
