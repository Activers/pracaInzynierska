package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    final String TAG = "SplashScreen";

    private static int SPLASH_SCREEN_DELAY = 4000;

    Animation topAnim, bottomAnim;
    ImageView imageLogo;
    TextView appName, appSlogan;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    SharedPreferences preferences;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Animacje
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);


        imageLogo = findViewById(R.id.imageViewLogo);
        appName = findViewById(R.id.textViewAppName);
        appSlogan = findViewById(R.id.textViewAppSlogan);

        imageLogo.setAnimation(topAnim);

        appName.setAnimation(bottomAnim);
        appSlogan.setAnimation(bottomAnim);

        // Verifying autologing
        String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName); // preference name - file with key-value
        preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);
        if (preferences.contains("pref_automaticLogin")) {


            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();

            String email = preferences.getString("pref_email", "no data found");
            String password = preferences.getString("pref_password", "no data found");

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                        usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.i(TAG, "Document Snapshot data: " + document.getData());
                                        String age = document.getString("age");
                                        if (age == null) {
                                            intent = new Intent(SplashScreen.this,  AfterRegister.class);
                                        } else {
                                            intent = new Intent(SplashScreen.this,  Dashboard.class);
                                        }
                                    } else {
                                        Log.i(TAG, "Nie znaleziono dokumentu");
                                        intent = new Intent(SplashScreen.this,  MainActivity.class);
                                    }
                                } else {
                                    Log.i(TAG, "niepowodzenie spowodowane: ", task.getException());
                                    intent = new Intent(SplashScreen.this,  MainActivity.class);
                                }
                            }
                        });
                    } else {
                        Log.i(TAG, "Autologowanie zakończone niepowodzeniem");
                        Toast.makeText(SplashScreen.this, "Autologowanie zakończone niepowodzeniem!", Toast.LENGTH_SHORT).show();
                        intent = new Intent(SplashScreen.this,  MainActivity.class);
                    }
                }
            });
        } else {
            Log.i(TAG, "Wylaczone autologowanie - przejscie do MainActivity");
            intent = new Intent(SplashScreen.this,  MainActivity.class);
        }
        // Verifying autologing END


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN_DELAY);
    }
}