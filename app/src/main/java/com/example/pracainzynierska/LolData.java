package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LolData extends AppCompatActivity {

    EditText LolNick,LolBio;
    Spinner LolUseMic,LolPrefHours,LolRanks;
    Button LolAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lol_data);

        LolNick = findViewById(R.id.editTextLolNick);
        LolBio = findViewById(R.id.editTextLolBio);
        LolUseMic = findViewById(R.id.spinnerLolUseMic);
        LolPrefHours = findViewById(R.id.spinnerLolPrefHours);
        LolRanks = findViewById(R.id.spinnerLolRank);
        LolAddGame = findViewById(R.id.buttonAddLol);


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayLolRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolRanks.setAdapter(adapterRanks);


    }
}