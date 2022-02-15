package com.example.nfcproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


//rando push
public class JourneyMenuFragment extends Fragment {

    public JourneyMenuFragment() {
        //required empty public constructor
    }

    static JourneyMenuFragment newInstance() {
        return new JourneyMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission();
        final View JourneyMenu = inflater.inflate(R.layout.journey_menu, container, false);

        Button toShrineMenu = JourneyMenu.findViewById(R.id.test_journey_button); // to shrine menu
        toShrineMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShrineMenuFragment smf = ShrineMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "ShrineMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //smf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button back = JourneyMenu.findViewById(R.id.journey_menu_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SagaMenuFragment smf = SagaMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //jmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        return JourneyMenu;
    }
}
