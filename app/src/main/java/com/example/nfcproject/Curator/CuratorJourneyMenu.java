package com.example.nfcproject.Curator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.nfcproject.R;

public class CuratorJourneyMenu extends Fragment {

    public CuratorJourneyMenu() {
        //required empty public constructor
    }

    static CuratorJourneyMenu newInstance() {
        return new CuratorJourneyMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorJourneyMenu = inflater.inflate(R.layout.curator_journey_menu, container, false);

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
            }
        });

        return curatorJourneyMenu;
    }
}