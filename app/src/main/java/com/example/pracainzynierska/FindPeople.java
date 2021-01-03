package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class FindPeople extends AppCompatActivity {

    Button Csgo, Lol, Fortnite, Amongus, Pubg, Apex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        Csgo = findViewById(R.id.buttonCsgo);
        Lol = findViewById(R.id.buttonLol);
        Fortnite = findViewById(R.id.buttonFortnite);
        Amongus = findViewById(R.id.buttonAmongUs);
        Pubg = findViewById(R.id.buttonPubg);
        Apex = findViewById(R.id.buttonApex);

        final Intent intent = new Intent(getApplicationContext(),Players.class);

        Csgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("game","csgo");
                startActivity(intent);
            }
        });


        Lol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("game","lol");
                startActivity(intent);
            }
        });


        Fortnite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("game","fortnite");
                startActivity(intent);
            }
        });


        Amongus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("game","amongus");
                startActivity(intent);
            }
        });


        Pubg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("game","pubg");
                startActivity(intent);
            }
        });


        Apex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("game","apex");
                startActivity(intent);
            }
        });


    }
}