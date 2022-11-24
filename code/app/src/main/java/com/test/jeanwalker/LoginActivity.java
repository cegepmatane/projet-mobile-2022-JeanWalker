package com.test.jeanwalker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.test.jeanwalker.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    //view binding
    private ActivityMainBinding binding;

    private static final int REQ_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_login);

        //Configuration google signin
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // FirebaseAuth init
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.googleAuthSigninButton).setOnClickListener(view -> {
            //Début google signin
            Log.d(TAG, "Begin google signin");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, REQ_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_IN){
            Log.d(TAG, "Google signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount compte = accountTask.getResult(ApiException.class);
                firebaseAuthAvecCompteGoogle(compte);
            }catch (Exception e){
                Log.d(TAG, "onActivityResult" + e.getMessage());
            }
        }
    }

    private void firebaseAuthAvecCompteGoogle(GoogleSignInAccount compte) {
        Log.d(TAG, "firebaseAuthAvecCompteGoogle : début firebase auth avec compte google");
        AuthCredential credential = GoogleAuthProvider.getCredential(compte.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess : Logged in");

                        //Récup le user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String userId = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        // check si nouveau user
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            //Nouveau -> création de compte
                            Log.d(TAG, "onSuccess: Création compte : "+email);
                            Toast.makeText(LoginActivity.this, "Compte créé pour : "+email, Toast.LENGTH_SHORT).show();
                        }else {
                            //Existant -> logged in
                            Log.d(TAG, "onSuccess: User existe : "+email);
                        }

                        //lance l'app
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure : Loggin failed"+e.getMessage());
                    }
                });
    }
}