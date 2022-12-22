package com.test.jeanwalker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.test.jeanwalker.dao.FirestoreParametreCallbacks;
import com.test.jeanwalker.dao.ParametreDAO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParametresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParametresFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ParametresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParametresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParametresFragment newInstance(String param1, String param2) {
        ParametresFragment fragment = new ParametresFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button signOutBtn = (Button) view.findViewById(R.id.vueParametresActionSignOut);
        Button confirmesBtn = (Button) view.findViewById(R.id.actionParametresConfirmer);
        Button annulerBtn = (Button) view.findViewById(R.id.actionParametresAnnuler);
        EditText ageTxt = (EditText) view.findViewById(R.id.textParametresAge);
        EditText tailleTxt = (EditText) view.findViewById(R.id.textParametresTaille);
        EditText masseTxt = (EditText) view.findViewById(R.id.textParametresMasse);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        ParametreDAO parametreDAO = new ParametreDAO(auth.getCurrentUser(),(age,taille,masse)->{
            ageTxt.setHint(String.valueOf(age));
            tailleTxt.setHint(String.valueOf(taille));
            masseTxt.setHint(String.valueOf(masse));
        });
        Log.i("TAMERE", "onViewCreated: " + parametreDAO.GetAge());

        signOutBtn.setOnClickListener(viewLambda -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });
        confirmesBtn.setOnClickListener(viewLambda -> {
            int age = ageTxt.getText().toString().isEmpty() ? 0 : Integer.parseInt(ageTxt.getText().toString());
            float taille = tailleTxt.getText().toString().isEmpty() ? 0 : Float.parseFloat(tailleTxt.getText().toString());
            float masse = masseTxt.getText().toString().isEmpty() ? 0 : Float.parseFloat(masseTxt.getText().toString());
            parametreDAO.updateParametre(auth.getCurrentUser().getUid(), age, taille, masse);
            ageTxt.setHint(ageTxt.getText().toString());
            tailleTxt.setHint(tailleTxt.getText().toString());
            masseTxt.setHint(masseTxt.getText().toString());

        });
        annulerBtn.setOnClickListener(viewLambda -> {
            int age = 0;
            float taille = 0;
            float masse = 0;
            Log.i("TAMERE", "onViewCreated: " + parametreDAO.GetAge());
            parametreDAO.updateParametre(auth.getCurrentUser().getUid(), age, taille, masse);
            ageTxt.setHint("");
            tailleTxt.setHint("");
            masseTxt.setHint("");
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parametres, container, false);
    }
}