package com.test.jeanwalker.dao;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import javax.security.auth.callback.Callback;

public class ParametreDAO {
    private int age;
    private float taille;
    private float masse;
    private final FirebaseFirestore firestore;
    private final FirebaseUser currentUser;

    public ParametreDAO(FirebaseUser currentUser, FirestoreParametreCallbacks callback) {
        this.currentUser = currentUser;
        this.firestore = FirebaseFirestore.getInstance();
        this.firestore.collection("users")
                .document(currentUser.getUid())
                .collection("userInfos")
                .document("infos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        task.getResult();
                        age = Math.toIntExact(Math.round((Double) task.getResult().getDouble("age")));
                        taille = Math.round((Double) task.getResult().getDouble("taille"));
                        masse = Math.round((Double) task.getResult().getDouble("masse"));
                        Log.i("test", "age : " + this.age + " taille : " + this.taille + " masse : " + this.masse);
                        //callbacks.onCallback(this.age, this.taille, this.masse);
                    }else{
                        Log.e("ParametreDAO","ParametreDAO: Erreur en récupérant les paramètres", task.getException());
                    }
                    callback.onCallback(this.age, this.taille, this.masse);
                });
    }

    public int GetAge(){
        return age;
    }
    public float GetTaille(){
        return taille;
    }
    public float GetMasse(){
        return masse;
    }
    public void updateParametre(String user,int age,float taille,float masse) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> userInfos = new HashMap<>();
        userInfos.put("age", age);
        userInfos.put("taille", taille);
        userInfos.put("masse", masse);

        db.collection("users").document(user).collection("userInfos").document("infos").update(userInfos)
                .addOnSuccessListener(aVoid -> {
            System.out.println("Parametre mis à jour");
        }).addOnFailureListener(e -> {
            System.out.println("Erreur lors de la mise à jour du parametre");
        });

    }
}

