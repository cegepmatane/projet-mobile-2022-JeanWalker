package com.test.jeanwalker.modeles;

import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trajet {
    protected String titre;
    protected int duree;
    protected String date;
    protected int distance;
    protected List<GeoPoint> route;

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

    public Trajet(String titre, int duree, List<GeoPoint> route) {
        this.titre = titre;
        this.duree = duree;
        this.route = route;
    }

    public Trajet(String titre, int duree, String date, int distance, List<GeoPoint> route) {
        this.titre = titre;
        this.duree = duree;
        this.date = date;
        this.distance = distance;
        this.route = route;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public HashMap<String, String> obtenirTrajetPourAfficher(){
        HashMap<String, String> trajetPourAfficher = new HashMap<>();
        trajetPourAfficher.put("titre", this.titre);
        trajetPourAfficher.put("duree", dureeEnString(this.duree) );

        return trajetPourAfficher;
    }

    public List<LatLng> transformeGeoPointsToLatLng(){
        List<LatLng> latLngRoute = new ArrayList<>();

        for (GeoPoint point : route){
            double lat = point.getLatitude();
            double lng = point.getLongitude();
            LatLng latLngPoint = new LatLng(lat, lng);
            latLngRoute.add(latLngPoint);
        }
        return latLngRoute;
    }

    private String dureeEnString(int duree){
        int hr = duree / 3600;
        int min = (duree % 3600) / 60;
        int sec = (duree % 3600) % 60;

        return String.format("%02d:%02d:%02d", hr, min, sec);
    }
}
