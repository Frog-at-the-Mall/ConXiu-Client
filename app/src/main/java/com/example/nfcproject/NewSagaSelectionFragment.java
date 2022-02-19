package com.example.nfcproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class NewSagaSelectionFragment extends Fragment {

    public NewSagaSelectionFragment() {
        //required empty public constructor
    }

    static NewSagaSelectionFragment newInstance() {
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
                SagaMenuFragment smf = SagaMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //jmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        return NewSagaSelection;
    }
}
