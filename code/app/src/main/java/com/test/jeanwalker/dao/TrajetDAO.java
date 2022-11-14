package com.test.jeanwalker.dao;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    public List<Trajet> loadData(String user){
        List<Trajet> listeTrajets = new ArrayList<>();
        DocumentReference documentRef = firestore.collection("trajets").document("init");
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String titre = documentSnapshot.getId();
                    List<GeoPoint> route = (List<GeoPoint>) documentSnapshot.get("route");
                    int duree = Math.toIntExact(Math.round(documentSnapshot.getDouble("duree")));
                    listeTrajets.add(new Trajet(titre));
                }
            }
        });

        return listeTrajets;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }
}
