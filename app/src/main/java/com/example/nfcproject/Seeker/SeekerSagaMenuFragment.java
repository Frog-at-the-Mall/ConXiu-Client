package com.example.nfcproject.Seeker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.nfcproject.LoginAndSplash.InitialFragment;
import com.example.nfcproject.MainActivity;
import com.example.nfcproject.NewSagaSelectionFragment;
import com.example.nfcproject.ProfileConfigFragment;
import com.example.nfcproject.R;


public class SeekerSagaMenuFragment extends Fragment {

    public SeekerSagaMenuFragment() {
        //required empty public constructor
    }

    public static SeekerSagaMenuFragment newInstance() {
        return new SeekerSagaMenuFragment();
    }

    public static final String SHARED_PREF = "sharedPref";
    public static final String JWT = "jwt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).checkPermission(); //check for permissions immediately upon entering the fragment
        final View SagaMenu = inflater.inflate(R.layout.saga_menu, container, false);

        Button toJourneyMenu = SagaMenu.findViewById(R.id.test_saga_button); //to journey screen
        toJourneyMenu.setOnClickListener(new View.OnClickListener() {
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

        Button newSaga = SagaMenu.findViewById(R.id.new_saga_button); //to new saga selection screen (seek or curate)
        newSaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewSagaSelectionFragment nssf = NewSagaSelectionFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nssf, "NewSagaSelectionFragment")
                        .addToBackStack(null)
                        .commit();
                //nssf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button profileConfig = SagaMenu.findViewById(R.id.profile_config_button); //to profile config screen
        profileConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileConfigFragment pcf = ProfileConfigFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, pcf, "ProfileConfigFragment")
                        .addToBackStack(null)
                        .commit();
                //pcf.setLocalState("example state");         //just to carry over state as needed
            }
        });

        Button back = SagaMenu.findViewById(R.id.logout_button); //back to main menu
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                try {
                    editor.putString(JWT, "");
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                InitialFragment iff = InitialFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, iff, "InitialFragment")
                        .commit();
            }
        });

        return SagaMenu;
    }
}
