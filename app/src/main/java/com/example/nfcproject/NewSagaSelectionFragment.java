package com.example.nfcproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.nfcproject.Curator.CuratorSagaMenu;
import com.example.nfcproject.Seeker.SeekerSagaMenuFragment;

public class NewSagaSelectionFragment extends Fragment {

    public NewSagaSelectionFragment() {
        //required empty public constructor
    }

    public static NewSagaSelectionFragment newInstance() {
        return new NewSagaSelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment
        final View NewSagaSelection = inflater.inflate(R.layout.new_saga_selection, container, false);

        Button back = NewSagaSelection.findViewById(R.id.new_saga_select_back); //back button
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

        Button seek = NewSagaSelection.findViewById(R.id.new_saga_select_seek_button);
        seek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toast for scanning a new tag
            }
        });

        Button curate = NewSagaSelection.findViewById(R.id.new_saga_select_curate_button);
        curate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuratorSagaMenu csmf = CuratorSagaMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, csmf, "CuratorSagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //csmf.setLocalState("example state"); // carry over state as needed
            }
        });

        return NewSagaSelection;
    }
}
