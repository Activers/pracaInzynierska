package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddGame extends AppCompatActivity {

    Button AddCsgoGame;
    Button AddLolGame;
    Button AddFortniteGame;
    Button AddAmongUsGame;
    Button AddPubgGame;
    Button AddApexGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        AddCsgoGame = findViewById(R.id.buttonCsgo);
        AddLolGame = findViewById(R.id.buttonLol);
        AddFortniteGame = findViewById(R.id.buttonFortnite);
        AddAmongUsGame = findViewById(R.id.buttonAmongUs);
        AddPubgGame = findViewById(R.id.buttonPubg);
        AddApexGame = findViewById(R.id.buttonApex);

        AddCsgoGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CsgoData.class));
            }
        });

        AddLolGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LolData.class));
            }
        });

        AddFortniteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FortniteData.class));
            }
        });

        AddAmongUsGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AmongUsData.class));
            }
        });

        AddPubgGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PubgData.class));
            }
        });

        AddApexGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ApexData.class));
            }
        });





    }
}