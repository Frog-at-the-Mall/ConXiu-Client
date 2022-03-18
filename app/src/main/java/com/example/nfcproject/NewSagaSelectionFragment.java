package com.example.nfcproject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nfcproject.Curator.CuratorSagaMenu;
import com.example.nfcproject.DataRep.Saga;
import com.example.nfcproject.Seeker.SeekerSagaMenuFragment;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class NewSagaSelectionFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity){
            a=(Activity) context;
        }
    }

    public static final String SHARED_PREF = "sharedPref";
    SharedPreferences sp;

    public NewSagaSelectionFragment() {
        //required empty public constructor
    }

    public static NewSagaSelectionFragment newInstance() {
        return new NewSagaSelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context mcontext = getContext();
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
                //TODO implement scanning new tag in NewSagaSelectionFragment
                Toast.makeText(getActivity(), "Scan a saga tag now.", Toast.LENGTH_SHORT).show();

                //start the NFC listener, handle the read tag to get hash
                //query server with that hash/id
                //receive saga and format it into objects properly
                //addSagaToSharedPrefs(returnedSaga);
                //toast success
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
        sp = this.getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return NewSagaSelection;
    }



    //accepts a saga and stores it in the shared prefs of main activity
    private void addSagaToSharedPrefs(String key, Saga value) {
        //retrieve sagas
        Gson gson = new Gson();
        String retrievedJson = sp.getString("Sagas", "");
        ArrayList<Saga> sagas = gson.fromJson(retrievedJson, new TypeToken<ArrayList<Saga>>() {
        }.getType());
        //add and store new saga
        sagas.add(value);
        SharedPreferences.Editor editor = sp.edit();
        String submittedJson = gson.toJson(sagas);
        editor.putString("Sagas", submittedJson);
        editor.apply(); //background process instead of instant commit()
    }
}