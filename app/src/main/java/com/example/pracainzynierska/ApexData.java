package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ApexData extends AppCompatActivity {

    EditText ApexNick,ApexBio;
    Spinner ApexUseMic,ApexPrefHours,ApexRanks;
    Button ApexAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apex_data);

        ApexNick = findViewById(R.id.editTextApexNick);
        ApexBio = findViewById(R.id.editTextApexBio);
        ApexUseMic = findViewById(R.id.spinnerApexUseMic);
        ApexPrefHours = findViewById(R.id.spinnerApexPrefHours);
        ApexRanks = findViewById(R.id.spinnerApexRank);
        ApexAddGame = findViewById(R.id.buttonAddApex);


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayApexRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexRanks.setAdapter(adapterRanks);

    }
}