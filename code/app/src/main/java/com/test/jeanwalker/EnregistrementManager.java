package com.test.jeanwalker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.SphericalUtil;
import com.test.jeanwalker.dao.TrajetDAO;
import com.test.jeanwalker.modeles.Trajet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EnregistrementManager {
    private GoogleMap map;
    private Marker userPosition;

    private FirebaseFirestore firestore;
    private TrajetDAO trajetDAO;

    private TextView textViewDistanceParcourue;
    private TextView textViewVitesseActuelle;
    private Chronometer chronoDureeEnregistrement;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<LatLng> listePositions;
    private Polyline polylineTrajet;
    private Location lastLocation;
    private double distanceParcourue;
    private float vitesseActuelle;
    private int tempsEnregistrement;
    private String titre;
    private Activity activity;


    public EnregistrementManager(GoogleMap map, Marker userPosition, TextView textViewDistanceParcourue, TextView textViewVitesseActuelle, Chronometer chronoDureeEnregistrement, FusedLocationProviderClient fusedLocationProviderClient, Activity activity) {
        this.map = map;
        this.userPosition = userPosition;
        this.activity = activity;
        this.textViewDistanceParcourue = textViewDistanceParcourue;
        this.textViewVitesseActuelle = textViewVitesseActuelle;
        this.chronoDureeEnregistrement = chronoDureeEnregistrement;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.firestore = FirebaseFirestore.getInstance();
        this.trajetDAO = new TrajetDAO(firestore, FirebaseAuth.getInstance().getCurrentUser());
        this.listePositions = new ArrayList<>();

        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(20));
        polylineTrajet = map.addPolyline(new PolylineOptions().pattern(pattern));

    }

    public void demarrerEnregistrement(){
        chronoDureeEnregistrement.start();
        chronoDureeEnregistrement.setOnChronometerTickListener(chronometer -> {
            @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(task -> {
               if (task.isSuccessful()){
                   lastLocation = task.getResult();
                   listePositions.add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                   if (listePositions.size() > 1){
                       distanceParcourue = distanceParcourue + SphericalUtil.computeDistanceBetween(listePositions.get(listePositions.size() - 1),
                                                                                                    listePositions.get(listePositions.size() - 2));
                   }
                   vitesseActuelle = (lastLocation.getSpeed() * 3600) / 1000;
                   updateUi();
                   Log.d("EnregistrementEnCours", "demarrerEnregistrement: latlng - " + lastLocation.getLatitude() +"-"+lastLocation.getLongitude());
               }
            });
            tempsEnregistrement++;
        });
    }

    public void arreterEnregistrement(){
        chronoDureeEnregistrement.stop();
        showStopDialog();
        polylineTrajet.setPoints(new ArrayList<>());
    }

    @SuppressLint("SetTextI18n")
    public void updateUi(){
        if (distanceParcourue >= 1000f){

            textViewDistanceParcourue.setText(String.format("%.2f km", distanceParcourue/1000));
        }else{

            textViewDistanceParcourue.setText(String.format("%.2f m", distanceParcourue));
        }

        textViewVitesseActuelle.setText(String.format("%3.1f km/h", vitesseActuelle));

        polylineTrajet.setPoints(listePositions);
    }

    public void showStopDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(chronoDureeEnregistrement.getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_fin_enregistrement, null);
        TextView textViewTitre = dialogLayout.findViewById(R.id.editTextTitreTrajet);
        builder.setView(dialogLayout)
                .setPositiveButton(R.string.finEnregistrementConfirmer, (dialogInterface, i) -> {
                    if (TextUtils.isEmpty(textViewTitre.getText().toString())){
                        showStopDialog();
                    }else {
                        Trajet trajet = new Trajet(
                                textViewTitre.getText().toString(),
                                tempsEnregistrement,
                                new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                                distanceParcourue,
                                transformerLatLngToGeoPointsRoute(listePositions)
                                );
                        trajetDAO.ajouterTrajet(trajet);
                    }
                })
                .setNegativeButton(R.string.finEnregistrementAnnuler, (dialogInterface, i) -> {
                    polylineTrajet.setPoints(new ArrayList<>());
                })
                .show();
    }

    public List<GeoPoint> transformerLatLngToGeoPointsRoute(List<LatLng> liste){
        List<GeoPoint> route = new ArrayList<>();
        for(LatLng point : liste){
            route.add(new GeoPoint(point.latitude, point.longitude));
        }

        return route;
    }
}
