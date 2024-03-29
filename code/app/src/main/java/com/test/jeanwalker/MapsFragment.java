package com.test.jeanwalker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class MapsFragment extends Fragment {

    public static final int BASE_LOCATION_UPDATE_INTERVAL = 1000;
    public static final int PERMISSION_FINE_LOCATION = 100;
    public static final String TAG = "MAPS_FRAGMENT_TAG";

    Location lastLocation;
    private GoogleMap map;
    private Marker userPosition;
    private boolean locationPermissionGranted;

    private TextView textViewDistanceParcourue, textViewVitesseActuelle;
    private Chronometer chronoDureeEnregistrement;

    private EnregistrementManager enregistrementManager;

    private boolean isRecording = false;

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;

            updateMapLocation();

            getLastLocation();

        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null){
                    //enregistrementManager.updateUi();
                    Location location = locationResult.getLastLocation();
                    updateUI(location);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        textViewDistanceParcourue = view.findViewById(R.id.textViewDistanceParcourue);
        textViewVitesseActuelle = view.findViewById(R.id.textViewVitesseActuelle);
        chronoDureeEnregistrement = view.findViewById(R.id.chronoDureeEnregistrement);

        Button buttonDemarrerEnregistrement = (Button) view.findViewById(R.id.buttonDemarrerEnregistrement);
        buttonDemarrerEnregistrement.setOnClickListener(view1 -> {
            if (isRecording){
                isRecording = false;
                buttonDemarrerEnregistrement.setText("Démarrer");
                enregistrementManager.arreterEnregistrement();
            }else{
                isRecording = true;
                buttonDemarrerEnregistrement.setText("Arrêter");
                chronoDureeEnregistrement.setBase(SystemClock.elapsedRealtime());
                enregistrementManager.demarrerEnregistrement();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getContext(), "This app requires permission to work properly", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else{
                    locationPermissionGranted = true;
                }
        }
        updateMapLocation();
    }

    /**
     * Permet d'obtenir la dernière localisation connue de l'appareil
     */
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        try {
            if (locationPermissionGranted){
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                   if (task.isSuccessful()){
                       lastLocation = task.getResult();
                       if (lastLocation != null){
                           userPosition = map.addMarker(new MarkerOptions()
                                   .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                                   .flat(true)
                                   .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_arrow_orientation))
                                   .rotation(lastLocation.getBearing()));
                           map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 19));
                           enregistrementManager = new EnregistrementManager(map, userPosition, textViewDistanceParcourue, textViewVitesseActuelle, chronoDureeEnregistrement, fusedLocationProviderClient, requireActivity());
                       }
                   }else {
                       Log.d(TAG, "getLastLocation: Localisation actuelle nulle. Using Clairegoutte.");
                       Log.e(TAG, "getLastLocation: Exception : %s", task.getException());
                       map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.670453518417354, 6.636553152266528), 13));
                       map.getUiSettings().setMyLocationButtonEnabled(false);
                   }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getLastLocation: Exception : %s", e );
        }
    }

    @SuppressLint("MissingPermission")
    private void updateMapLocation(){
        if(map == null){
            return;
        }
        try{
        if (locationPermissionGranted) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            map.setMyLocationEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            lastLocation = null;
            getLocationPermission();
        }

    }catch (SecurityException e){
            Log.e(TAG, "updateMapLocation: Exception : " + e.getMessage());
        }
    }

    private void getLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(BASE_LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    private void updateUI(Location location){
        userPosition.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        userPosition.setRotation(location.getBearing());
        map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(userPosition.getPosition())
                .bearing(userPosition.getRotation())
                .zoom(19)
                .build()));
        //map.moveCamera(CameraUpdateFactory.newLatLng(userPosition.getPosition()));
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}