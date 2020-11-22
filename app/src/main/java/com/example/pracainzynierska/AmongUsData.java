package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AmongUsData extends AppCompatActivity {

    EditText AmongNick,AmongBio;
    Spinner AmongUseMic,AmongPrefHours,AmongRanks;
    Button AmongAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_among_us_data);

        AmongNick = findViewById(R.id.editTextAmongNick);
        AmongBio = findViewById(R.id.editTextAmongBio);
        AmongUseMic = findViewById(R.id.spinnerAmongUseMic);
        AmongPrefHours = findViewById(R.id.spinnerAmongPrefHours);
        AmongRanks = findViewById(R.id.spinnerAmongRank);
        AmongAddGame = findViewById(R.id.buttonAddAmong);


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayFortniteRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongRanks.setAdapter(adapterRanks);

    }
}