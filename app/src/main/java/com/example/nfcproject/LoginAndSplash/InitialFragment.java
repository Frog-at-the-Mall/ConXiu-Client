package com.example.nfcproject.LoginAndSplash;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nfcproject.LoginActivity;
import com.example.nfcproject.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class InitialFragment extends Fragment {

    public InitialFragment() {
        // Required empty public constructor
    }

    public static InitialFragment newInstance() {
        return new InitialFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View initialFragment = inflater.inflate(R.layout.fragment_initial, container, false);

        Button login = initialFragment.findViewById(R.id.welcome_button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return initialFragment;
    }
}
