package com.test.jeanwalker;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

import android.app.MediaRouteButton;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.test.jeanwalker.dao.TrajetDAO;
import com.test.jeanwalker.modeles.Trajet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccueilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccueilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ACCUEIL_FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listeViewTrajets;
    private List<Trajet> listeTrajets;
    private FirebaseUser currentUser;
    private LinearLayout greyLayout;
    private TextView textAccueil;

    public AccueilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccueilFragment newInstance(String param1, String param2) {
        AccueilFragment fragment = new AccueilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accueil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        textAccueil = (TextView) view.findViewById(R.id.vueAccueilTextAccueil);

        listeViewTrajets = (ListView) view.findViewById(R.id.listeTrajets);
        greyLayout = (LinearLayout) view.findViewById(R.id.greyLayout);
        greyLayout.setVisibility(View.VISIBLE);;
        getActivity().getWindow().setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE);



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TrajetDAO dao = new TrajetDAO(db, currentUser);

        listeTrajets = new ArrayList<Trajet>();

        Log.d(TAG, "onViewCreated: called");
        
        /*dao.listerTrajetsPourUser(trajetList -> {
            listeTrajets = trajetList;
            //Trajet trajet = listeTrajets.get(0);
            //tv.setText(trajet.getTitre());

            for (int i = 0; i<15; i++){
                listeTrajets.add(new Trajet("Course à pieds dans la forêt", 6350));
                listeTrajets.add(new Trajet("Tour de vélo en famille", 3793));
                listeTrajets.add(new Trajet("Rando en montagne", 10982));
            }

            List<HashMap<String, String>> listeTrajetsPourAfficher = new ArrayList<>();

            for (Trajet trajet : listeTrajets){
                listeTrajetsPourAfficher.add(trajet.obtenirTrajetPourAfficher());
            }

            SimpleAdapter adapter = new SimpleAdapter(
                    view.getContext(),
                    listeTrajetsPourAfficher,
                    android.R.layout.two_line_list_item,
                    new String[]{"titre", "duree"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );
            listeViewTrajets.setAdapter(adapter);
            textAccueil.setVisibility(View.GONE);

            greyLayout.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(FLAG_NOT_TOUCHABLE);

        });*/
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onStart: called");

        this.currentUser = firebaseAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TrajetDAO dao = new TrajetDAO(db, currentUser);

        listeTrajets = new ArrayList<Trajet>();

        dao.listerTrajetsPourUser(trajetList -> {
            listeTrajets = trajetList;

            List<HashMap<String, String>> listeTrajetsPourAfficher = new ArrayList<>();

            for (Trajet trajet : listeTrajets){
                listeTrajetsPourAfficher.add(trajet.obtenirTrajetPourAfficher());
            }

            SimpleAdapter adapter = new SimpleAdapter(
                    getContext(),
                    listeTrajetsPourAfficher,
                    android.R.layout.two_line_list_item,
                    new String[]{"titre", "duree"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );

            listeViewTrajets.setAdapter(adapter);
            listeViewTrajets.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", listeTrajetsPourAfficher.get(position).get("titre"));
                startActivity(intent);
            });
            textAccueil.setVisibility(View.GONE);

            greyLayout.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(FLAG_NOT_TOUCHABLE);

        });
    }
}