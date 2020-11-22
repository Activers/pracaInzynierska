package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FortniteData extends AppCompatActivity {

    EditText FortniteNick,FortniteBio;
    Spinner FortniteUseMic,FortnitePrefHours,FortniteRanks;
    Button FortniteAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortnite_data);

        FortniteNick = findViewById(R.id.editTextFortniteNick);
        FortniteBio = findViewById(R.id.editTextFortniteBio);
        FortniteUseMic = findViewById(R.id.spinnerFortniteUseMic);
        FortnitePrefHours = findViewById(R.id.spinnerFortnitePrefHours);
        FortniteRanks = findViewById(R.id.spinnerFortniteRank);
        FortniteAddGame = findViewById(R.id.buttonAddFortnite);

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortniteUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortnitePrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayFortniteRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortniteRanks.setAdapter(adapterRanks);

    }
}