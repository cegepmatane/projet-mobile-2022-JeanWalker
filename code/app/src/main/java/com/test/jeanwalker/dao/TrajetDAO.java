package com.test.jeanwalker.dao;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.test.jeanwalker.modeles.Trajet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrajetDAO {
    private static final String TAG = "TAG : TrajetDAO";
    private final FirebaseFirestore firestore;
    private Trajet trajet;
    private final FirebaseUser currentUser;

    public TrajetDAO(FirebaseFirestore firestore, FirebaseUser currentUser) {
        this.firestore = firestore;
        this.currentUser = currentUser;
        this.trajet = null;
    }

    public List<Trajet> listerTrajetsPourUser(FirestoreCallbacks callback){
        Log.d(TAG, "listerTrajetsPourUser: Début listing");
        // Init la liste des trajets
        List<Trajet> listeTrajets = new ArrayList<>();

        // Connexion à firebase et get tous les documents de la collection "trajet" pour currentUser
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("trajets")
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       //  Boucle pour passer, instancier et insérer tous les trajets dans listeTrajets
                       for (QueryDocumentSnapshot document : task.getResult()){
                           String titre = document.getId();
                           int duree = Math.toIntExact(Math.round(document.getDouble("duree")));
                           List<GeoPoint> route = (List<GeoPoint>) document.get("route");

                           Trajet trajet = new Trajet(titre, duree, route);
                           listeTrajets.add(trajet);

                           callback.onCallback(listeTrajets);
                           Log.d(TAG, "listerTrajetsPourUser: onComplete listeTrajet : " + listeTrajets.toString());
                       }
                   }else{
                       Log.e(TAG, "listerTrajetsPourUser: Erreur en récuprérant les docs", task.getException());
                   }
                });

        Log.d(TAG, "outside completeListener");
        Log.d(TAG, listeTrajets.toString());

        return listeTrajets;
    }

    /**
     * À appeler lorsqu'un trajet a fini de s'enregistrer.
     * @param trajet : Trajet terminé et complètement instancié
     */
    public void ajouterTrajet(Trajet trajet){
        // Crée le HashMap à entrer dans Firebase à partir du trajet en paramètre
        Map<String, Object> trajetFirebase = new HashMap<>();
        trajetFirebase.put("titre", trajet.getTitre());
        trajetFirebase.put("duree", trajet.getDuree());
        trajetFirebase.put("route", trajet.getRoute());

        //
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }
}
