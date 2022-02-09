package com.example.nfcproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class SagaMenuFragment extends Fragment {

    public SagaMenuFragment() {
        //required empty public constructor
    }

    static SagaMenuFragment newInstance() {
        return new SagaMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the global fragment
        final View sagaMenu = inflater.inflate(R.layout.saga_menu, container, false);
        Button toJourneyMenu = sagaMenu.findViewById(R.id.test_saga_button);
        toJourneyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JourneyMenuFragment jmf = JourneyMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, jmf, "JourneyMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //jmf.setLocalState("example state");         //just to carry over state as needed
            }
        });
        return sagaMenu;
    }
}
