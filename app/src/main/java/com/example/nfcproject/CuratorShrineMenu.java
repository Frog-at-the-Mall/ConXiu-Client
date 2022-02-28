package com.example.nfcproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class CuratorShrineMenu extends Fragment {

    public CuratorShrineMenu() {
        //required empty public constructor
    }

    static CuratorShrineMenu newInstance() {
        return new CuratorShrineMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorShrineMenu = inflater.inflate(R.layout.curator_shrine_menu, container, false);

        Button back = curatorShrineMenu.findViewById(R.id.curator_shrine_menu_back_button); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorJourneyMenu cjmf = CuratorJourneyMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, cjmf, "CuratorJourneyMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //cjmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button testShrine = curatorShrineMenu.findViewById(R.id.curator_test_shrine);
        testShrine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorShrineSpecificsMenu cssmf = CuratorShrineSpecificsMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, cssmf, "CuratorShrineSpecificsMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //cssmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button newShrine = curatorShrineMenu.findViewById(R.id.curator_new_shrine_button);
        newShrine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open sandwich with prompts, bind to new button and add to vertical linear layout
            }
        });

        return curatorShrineMenu;
    }
}