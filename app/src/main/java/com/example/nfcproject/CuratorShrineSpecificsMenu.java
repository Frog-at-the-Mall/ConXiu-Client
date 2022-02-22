package com.example.nfcproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class CuratorShrineSpecificsMenu extends Fragment {

    public CuratorShrineSpecificsMenu() {
        //required empty public constructor
    }

    static CuratorShrineSpecificsMenu newInstance() {
        return new CuratorShrineSpecificsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorShrineSpecificsMenu = inflater.inflate(R.layout.curator_shrine_specifics_menu, container, false);

        Button back = curatorShrineSpecificsMenu.findViewById(R.id.curator_shrine_specifics_menu_back_button); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuratorShrineMenu csmf = CuratorShrineMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, csmf, "CuratorShrineMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //csmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button submit = curatorShrineSpecificsMenu.findViewById(R.id.curator_shrine_specifics_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save shrine info to file, to implement.
            }
        });

        return curatorShrineSpecificsMenu;
    }
}