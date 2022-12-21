package com.test.jeanwalker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.test.jeanwalker.dao.FirestoreCallbacks;
import com.test.jeanwalker.dao.TrajetDAO;
import com.test.jeanwalker.databinding.ActivityDetailBinding;
import com.test.jeanwalker.modeles.Trajet;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private List<Trajet> listeTrajets;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String id = intent.getExtras().getString("id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        View buttonClick = findViewById(R.id.returnButton);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TrajetDAO dao = new TrajetDAO(db, currentUser);

        listeTrajets = new ArrayList<Trajet>();

        dao.listerTrajetsPourUser(trajetList -> {
            listeTrajets = trajetList;
            for(Trajet trajet : listeTrajets){
                if(trajet.getTitre().equals(id)){
                    TextView tempsData = findViewById(R.id.tempsData);
                    tempsData.setText(String.valueOf(trajet.getDuree()/3600)+"h "+String.valueOf((trajet.getDuree()%3600)/60));

                    TextView distanceData = findViewById(R.id.distanceData);
                    distanceData.setText(String.valueOf(trajet.getDistance()/1000)+"km");

                    TextView dateData = findViewById(R.id.dateData);
                    dateData.setText(trajet.getDate());
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_detail);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return FirstFragment.newInstance("FirstFragment, Instance 1");
                case 1: return SecondFragment.newInstance("SecondFragment, Instance 1");
                case 2: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
                default: return ThirdFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }
}