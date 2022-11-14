package com.test.jeanwalker.dao;


import static android.service.controls.ControlsProviderService.TAG;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.test.jeanwalker.modeles.Trajet;

import java.util.ArrayList;
import java.util.List;

public class TrajetDAO {
    private final FirebaseFirestore firestore;
    private Trajet trajet;

    public TrajetDAO(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.trajet = null;
    }

    public List<Trajet> listerTrajetsPourUser(String user, FirestoreCallbacks callback){
        Log.d(TAG, "inside listerTrajets");
        List<Trajet> listeTrajets = new ArrayList<>();
        DocumentReference documentRef = firestore.collection("trajets").document("init");
        documentRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String titre = documentSnapshot.getId();
                List<GeoPoint> route = (List<GeoPoint>) documentSnapshot.get("route");
                int duree = Math.toIntExact(Math.round(documentSnapshot.getDouble("duree")));
                listeTrajets.add(new Trajet(titre));

                callback.onCallback(listeTrajets);
                Log.d(TAG, "Inside onComplete");
                Log.d(TAG, listeTrajets.toString());
            }else{
                Log.d(TAG, "Error getting document", task.getException());
            }
        });

        Log.d(TAG, "outside completeListener");
        Log.d(TAG, listeTrajets.toString());

        return listeTrajets;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }
}
