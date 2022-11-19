package com.test.jeanwalker.modeles;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trajet {
    protected String titre;
    protected List<GeoPoint> route;
    protected int duree;

    public Trajet() {
    }

    public Trajet(List<GeoPoint> route) {
        this.route = route;
    }

    public Trajet(List<GeoPoint> route, int duree) {
        this.route = route;
        this.duree = duree;
    }

    public Trajet(String titre){
        this.titre = titre;
    }

    public Trajet(String titre, int duree) {
        this.titre = titre;
        this.duree = duree;
    }

    public Trajet(String titre, List<GeoPoint> route, int duree) {
        this.titre = titre;
        this.route = route;
        this.duree = duree;
    }

    public List<GeoPoint> getRoute() {
        return route;
    }

    public void setRoute(List<GeoPoint> route) {
        this.route = route;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public HashMap<String, String> obtenirTrajetPourAfficher(){
        HashMap<String, String> trajetPourAfficher = new HashMap<>();
        trajetPourAfficher.put("titre", this.titre);
        trajetPourAfficher.put("duree", dureeEnString(this.duree) );

        return trajetPourAfficher;
    }

    private String dureeEnString(int duree){
        int hr = duree / 3600;
        int min = (duree % 3600) / 60;
        int sec = (duree % 3600) % 60;

        return String.format("%02d:%02d:%02d", hr, min, sec);
    }
}
