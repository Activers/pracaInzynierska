package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PubgData extends AppCompatActivity {

    EditText PubgNick,PubgBio;
    Spinner PubgUseMic,PubgPrefHours,PubgRanks;
    Button PubgAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubg_data);

        PubgNick = findViewById(R.id.editTextPubgNick);
        PubgBio = findViewById(R.id.editTextPubgBio);
        PubgUseMic = findViewById(R.id.spinnerPubgUseMic);
        PubgPrefHours = findViewById(R.id.spinnerPubgPrefHours);
        PubgRanks = findViewById(R.id.spinnerPubgRank);
        PubgAddGame = findViewById(R.id.buttonAddPubg);

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayPubgRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgRanks.setAdapter(adapterRanks);

    }
}