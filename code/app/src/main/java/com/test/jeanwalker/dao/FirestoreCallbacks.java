package com.test.jeanwalker.dao;

import com.test.jeanwalker.modeles.Trajet;

import java.util.List;

public interface FirestoreCallbacks {
    void onCallback(List<Trajet> trajetList);
}
