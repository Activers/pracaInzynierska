package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class Dashboard extends AppCompatActivity {

    Button FindPeople;
    Button GoProfile;
    Button ChooseGames;
    Button Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FindPeople = findViewById(R.id.buttonFindPeople);
        GoProfile = findViewById(R.id.buttonGoProfile);
        ChooseGames = findViewById(R.id.buttonChooseGames);
        Logout = findViewById(R.id.buttonLogout);

        FindPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),GameChoice.class));
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