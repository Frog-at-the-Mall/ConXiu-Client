package com.example.nfcproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ShrineSpecificsFragment extends Fragment {

    public ShrineSpecificsFragment() {
        //required empty public constructor
    }
    static ShrineSpecificsFragment newInstance() {
        return new ShrineSpecificsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the global fragment
        return inflater.inflate(R.layout.shrine_specifics, container, false);
    }
}
