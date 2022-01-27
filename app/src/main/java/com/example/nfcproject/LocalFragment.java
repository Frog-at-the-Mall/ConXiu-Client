package com.example.nfcproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Objects;


public class LocalFragment extends Fragment {

    private String localPassed; // the local game's state, how it will decide what specific information to populate
    private String localRememberNode; //will store our node when we travel to node screen, so we can refresh if we popped the local fragment from the stack from node screen

    private String sharedPreferencesStringBR;
    private String sharedPreferencesStringBL;
    private String sharedPreferencesStringTR;
    private String sharedPreferencesStringTL;
    private String sharedPreferencesStringMR;
    private String sharedPreferencesStringML;
    private String sharedPreferencesStringCT;
    private String sharedPreferencesStringCM;
    private String sharedPreferencesStringCB;

    public LocalFragment() {
        // Required empty public constructor
    }

    static LocalFragment newInstance() {
        return new LocalFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (localRememberNode != null) {
            updateUI(); //problem code, this is always called
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("local_frag", "onCreate");
        final View fragLocal = inflater.inflate(R.layout.fragment_local, container, false);
        localRememberNode = null;
        Button toNodeBR = fragLocal.findViewById(R.id.toNodeBR);
        toNodeBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("local_frag", "I heard you pressed BR");
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"BR");
                localRememberNode = "BR";
                Log.i("local_frag", "end of button");
            }
        });
        Button toNodeBL = fragLocal.findViewById(R.id.toNodeBL);
        toNodeBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"BL");
                localRememberNode = "BL";
            }
        });
        Button toNodeTR = fragLocal.findViewById(R.id.toNodeTR);
        toNodeTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"TR");
                localRememberNode = "TR";
            }
        });
        Button toNodeTL = fragLocal.findViewById(R.id.toNodeTL);
        toNodeTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"TL");
                localRememberNode = "TL";
            }
        });
        Button toNodeMR = fragLocal.findViewById(R.id.toNodeMR);
        toNodeMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"MR");
                localRememberNode = "MR";
            }
        });
        Button toNodeML = fragLocal.findViewById(R.id.toNodeML);
        toNodeML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"ML");
                localRememberNode = "ML";
            }
        });
        Button toNodeCT = fragLocal.findViewById(R.id.toNodeCT);
        toNodeCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"CT");
                localRememberNode = "CT";
            }
        });
        Button toNodeCM = fragLocal.findViewById(R.id.toNodeCM);
        toNodeCM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"CM");
                localRememberNode = "CM";
            }
        });
        Button toNodeCB = fragLocal.findViewById(R.id.toNodeCB);
        toNodeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodeFragment nf = NodeFragment.newInstance();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.replaceFrame, nf, "NodeFragment")
                        .addToBackStack(null)
                        .commit();
                nf.setNodeState(localPassed,"CB");
                localRememberNode = "CB";

            }
        });
        Button backToGlobal = fragLocal.findViewById(R.id.backToGlobal);
        backToGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        Log.i("local_frag", "shared prefs start");
        //Logic for buttons showing or not
        //basically give Shared Prefs two strings concatenated together to represent the unique local/node button on this fragment
        //if shared prefs value is returned TRUE, then reveal the NEXT button in the ARG (a correct answer in the node fragment sets the current local/node value to true to unlock the following node.
        //false: set button to visibility: gone to remove it entirely
        //be careful with T and B buttons, since they are anchored to CT and CB, i.e. always bring up the CT and CB buttons before the B_/T_ buttons.
        //the logic dictating the consequent node to unlock will be different for each shape since a different path is drawn for each local ARG (update later)

        createPrefsKeys();

        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE); //write unlock node to shared preferences
        boolean resultBR = sharedPref.getBoolean(sharedPreferencesStringBR, true); Log.i("local_frag", String.valueOf(resultBR));
        boolean resultBL = sharedPref.getBoolean(sharedPreferencesStringBL, true); Log.i("local_frag", String.valueOf(resultBL));
        boolean resultTR = sharedPref.getBoolean(sharedPreferencesStringTR, true); Log.i("local_frag", String.valueOf(resultTR));
        boolean resultTL = sharedPref.getBoolean(sharedPreferencesStringTL, true); Log.i("local_frag", String.valueOf(resultTL));
        boolean resultMR = sharedPref.getBoolean(sharedPreferencesStringMR, true); Log.i("local_frag", String.valueOf(resultMR));
        boolean resultML = sharedPref.getBoolean(sharedPreferencesStringML, true); Log.i("local_frag", String.valueOf(resultML));
        boolean resultCT = sharedPref.getBoolean(sharedPreferencesStringCT, true); Log.i("local_frag", String.valueOf(resultCT));
        boolean resultCM = sharedPref.getBoolean(sharedPreferencesStringCM, true); Log.i("local_frag", String.valueOf(resultCM));
        boolean resultCB = sharedPref.getBoolean(sharedPreferencesStringCB, true); Log.i("local_frag", String.valueOf(resultCB));

        //here is where we will designate the buttons reveal order.
        //simply change the button being hidden.
        //the only permutation not allowed is if a button is being set to gone from its own value in shared prefs
        Log.i("local_frag", "set visibility");
        if (!resultBR) {
            toNodeBL.setVisibility(View.GONE);
        }
        else {
            toNodeBL.setVisibility(View.VISIBLE);
        }
        if (!resultBL) {
            toNodeTR.setVisibility(View.GONE);
        }
        else {
            toNodeTR.setVisibility(View.VISIBLE);
        }
        if (!resultTR) {
            toNodeTL.setVisibility(View.GONE);
        }
        else {
            toNodeTL.setVisibility(View.VISIBLE);
        }
        if (!resultTL) {
            toNodeMR.setVisibility(View.GONE);
        }
        else {
            toNodeMR.setVisibility(View.VISIBLE);
        }
        if (!resultMR) {
            toNodeML.setVisibility(View.GONE);
        }
        else {
            toNodeML.setVisibility(View.VISIBLE);
        }
        if (!resultML) {
            toNodeCT.setVisibility(View.GONE);
        }
        else {
            toNodeCT.setVisibility(View.VISIBLE);
        }
        if (!resultCT) {
            toNodeCM.setVisibility(View.GONE);
        }
        else {
            toNodeCM.setVisibility(View.VISIBLE);
        }
        if (!resultCM) {
            toNodeCB.setVisibility(View.GONE);
        }
        else {
            toNodeCB.setVisibility(View.VISIBLE);
        }
        if (!resultCB) {
            toNodeBR.setVisibility(View.GONE);
        }
        else {
            toNodeBR.setVisibility(View.VISIBLE);
        }

        //TO IMPLEMENT LATER
//        if our node was activated it will create a line to the next node in the ARG
//        Log.i("local_frag", "button line");
//        if (localRememberNode != null && localRememberNode.equals("BR") && resultBR) {
//            final LinearLayout localFrame = getActivity().findViewById(R.id.localFrame);
//
//            //using addOnGlobalLayoutListener to make sure layout is happened.
//            localFrame.getViewTreeObserver().addOnGlobalLayoutListener(
//                    new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            //Remove the listener before proceeding
//                            localFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                            Button node1 = Objects.requireNonNull(getActivity()).findViewById(R.id.toNodeBR);
//                            int[] loc1 = new int[2];
//                            node1.getLocationInWindow(loc1);//loc1[0] is x and loc1[1] is y
//                            //for more information about this method, in Android Studio, just right-click -> Go To -> Declaration
//                            Button node2 = Objects.requireNonNull(getActivity()).findViewById(R.id.toNodeBL);
//                            int[] loc2 = new int[2];
//                            node2.getLocationInWindow(loc2);
//
//                            View v = new View(getActivity());
//                            v.setLayoutParams(new ViewGroup.LayoutParams(loc2[0]-loc1[0]-node1.getWidth(),dpToPx(2)));//dpToPx(20 + 20), dpToPx(2)));
//                            v.setBackgroundColor(Color.WHITE);
//                            localFrame.addView(v);
//                            v.setTranslationY(-dpToPx(30+20+20)-dpToPx(20+30/2));
//                            v.setTranslationX(dpToPx(20+30));
//                        }
//                    }
//            );
//        }
        Log.i("local_frag", "about to return");
        return fragLocal;
    }

    private void createPrefsKeys() {
        sharedPreferencesStringBR = localPassed+"BR";
        sharedPreferencesStringBL = localPassed+"BL";
        sharedPreferencesStringTR = localPassed+"TR";
        sharedPreferencesStringTL = localPassed+"TL";
        sharedPreferencesStringMR = localPassed+"MR";
        sharedPreferencesStringML = localPassed+"ML";
        sharedPreferencesStringCT = localPassed+"CT";
        sharedPreferencesStringCM = localPassed+"CM";
        sharedPreferencesStringCB = localPassed+"CB";
    }

    private void updateUI() {
        LocalFragment lf = LocalFragment.newInstance();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.replaceFrame, lf, "LocalFragment")
                .commit();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    void setLocalState(String localState) {
        localPassed = localState;
    }
}
