package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class Dashboard extends AppCompatActivity {

    final String TAG = "Dashboard";

    Animation topAnim, bottomAnim;

    TextView appName;

    ImageButton FindPeople;
    ImageButton GoProfile;
    ImageButton Settings;
    ImageButton Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
       // bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        FindPeople = findViewById(R.id.buttonFindPeople);
        GoProfile = findViewById(R.id.buttonGoProfile);
        Settings = findViewById(R.id.buttonSettings);
        Logout = findViewById(R.id.buttonLogout);
        appName = findViewById(R.id.editTextAppName);

       // appName.setAnimation(topAnim);
        //FindPeople.setAnimation(bottomAnim);
        //GoProfile.setAnimation(bottomAnim);
       // Settings.setAnimation(bottomAnim);
        //Logout.setAnimation(bottomAnim);


        FindPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FindPeople.class));
            }
        });

        GoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyProfile.class));
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),GameChoice.class));
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName); // nazwa preferencji / pliku gdzie skladowane beda klucz-wartosc
                SharedPreferences preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);
                preferences.edit().clear().apply(); //usuwa autologowanie po wylogowaniu sie
                Log.i(TAG, "Usunieto autologowanie");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    boolean singleBack = false;

    @Override
    public void onBackPressed() {
        if (singleBack) {
            finishAffinity();
            return;
        }

        this.singleBack = true;
        Toast.makeText(this, "Naciśnij ponownie WSTECZ, aby wyjść", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                singleBack=false;
            }
        }, 2000);
    }
}