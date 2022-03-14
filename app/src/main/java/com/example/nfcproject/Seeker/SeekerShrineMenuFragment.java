package com.example.nfcproject.Seeker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.nfcproject.MainActivity;
import com.example.nfcproject.R;

public class SeekerShrineMenuFragment extends Fragment {

    public SeekerShrineMenuFragment() {
        //required empty public constructor
    }
    static SeekerShrineMenuFragment newInstance() {
        return new SeekerShrineMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment
        final View ShrineMenu = inflater.inflate(R.layout.shrine_menu, container, false);

        Button toShrineSpecifics = ShrineMenu.findViewById(R.id.test_shrine_button); //to main game screen
        toShrineSpecifics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekerShrineSpecificsFragment ssf = SeekerShrineSpecificsFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, ssf, "ShrineSpecificsFragment")
                        .addToBackStack(null)
                        .commit();
                //ssf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button back = ShrineMenu.findViewById(R.id.shrine_menu_back); //back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekerJourneyMenuFragment jmf = SeekerJourneyMenuFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, jmf, "JourneyMenuFragment")
                        .addToBackStack(null)
                        .commit();
                //jmf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        return ShrineMenu;
    }
}
