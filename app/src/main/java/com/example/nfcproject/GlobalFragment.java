package com.example.nfcproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalFragment extends Fragment {

    public GlobalFragment() {
        // Required empty public constructor
    }

    static GlobalFragment newInstance() {
        return new GlobalFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) Objects.requireNonNull(getActivity())).checkPermission(); //check for permissions immediately upon entering the global fragment
        final View fragGlobal = inflater.inflate(R.layout.fragment_global, container, false);
        Button toLocalBR = fragGlobal.findViewById(R.id.toLocalBR);
        toLocalBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("global_frag", "I hear you clicked BR");
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("BR");
                Log.i("global_frag", "end of listener");
            }
        });
        Button toLocalBL = fragGlobal.findViewById(R.id.toLocalBL);
        toLocalBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("BL");
            }
        });
        Button toLocalTR = fragGlobal.findViewById(R.id.toLocalTR);
        toLocalTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("TR");
            }
        });
        Button toLocalTL = fragGlobal.findViewById(R.id.toLocalTL);
        toLocalTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("TL");
            }
        });
        Button toLocalMR = fragGlobal.findViewById(R.id.toLocalMR);
        toLocalMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("MR");
            }
        });
        Button toLocalML = fragGlobal.findViewById(R.id.toLocalML);
        toLocalML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("ML");
            }
        });
        Button toLocalCT = fragGlobal.findViewById(R.id.toLocalCT);
        toLocalCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("CT");
            }
        });
        Button toLocalCM = fragGlobal.findViewById(R.id.toLocalCM);
        toLocalCM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("CM");
            }
        });
        Button toLocalCB = fragGlobal.findViewById(R.id.toLocalCB);
        toLocalCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalFragment lf = LocalFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, lf, "LocalFragment")
                        .addToBackStack(null)
                        .commit();
                lf.setLocalState("CB");
            }
        });
        Log.i("global_frag", "about to return");
        return fragGlobal;
    }
}
