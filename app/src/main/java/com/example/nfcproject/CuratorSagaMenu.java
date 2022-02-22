package com.example.nfcproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class CuratorSagaMenu extends Fragment {
    public CuratorSagaMenu() {
        //required empty public constructor
    }

    static CuratorSagaMenu newInstance() {
        return new CuratorSagaMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View curatorSagaMenu = inflater.inflate(R.layout.curator_saga_menu, container, false);

        Button back = curatorSagaMenu.findViewById(R.id.curator_saga_menu_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SagaMenuFragment smf = SagaMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, smf, "SagaMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //smf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button testSaga = curatorSagaMenu.findViewById(R.id.curator_test_saga_button);
        testSaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuratorJourneyMenu cjmf = CuratorJourneyMenu.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, cjmf, "CuratorJourneyMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //cjmf.setLocalState("example state"); //carry over state as needed
            }
        });

        Button newSaga = curatorSagaMenu.findViewById(R.id.curator_new_saga_button);
        newSaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here will be logic to open a sandwich and gather a name for a saga, then creating a new button which
                //routes to the curator journey screen
            }
        });

        return curatorSagaMenu;
    }
}
