package com.example.nfcproject.Seeker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.nfcproject.MainActivity;
import com.example.nfcproject.R;


//rando push
public class SeekerJourneyMenuFragment extends Fragment {

    public SeekerJourneyMenuFragment() {
        //required empty public constructor
    }

    static SeekerJourneyMenuFragment newInstance() {
        return new SeekerJourneyMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission();
        final View JourneyMenu = inflater.inflate(R.layout.journey_menu, container, false);

        Button toShrineMenu = JourneyMenu.findViewById(R.id.test_journey_button); // to shrine menu
        toShrineMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekerShrineMenuFragment smf = SeekerShrineMenuFragment.newInstance();
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
                SeekerSagaMenuFragment smf = SeekerSagaMenuFragment.newInstance();
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
