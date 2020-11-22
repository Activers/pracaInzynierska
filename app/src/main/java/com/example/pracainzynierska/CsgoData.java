package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CsgoData extends AppCompatActivity {

    EditText CsgoNick,CsgoBio;
    Spinner CsgoUseMic,CsgoPrefHours,CsgoRanks;
    Button CsgoAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csgo_data);

       CsgoNick = findViewById(R.id.editTextCsgoNick);
       CsgoBio = findViewById(R.id.editTextCsgoBio);
       CsgoUseMic = findViewById(R.id.spinnerCsgoUseMic);
       CsgoPrefHours = findViewById(R.id.spinnerCsgoPrefHours);
       CsgoRanks = findViewById(R.id.spinnerCsgoRank);
       CsgoAddGame = findViewById(R.id.buttonAddCsgo);

       ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
       adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
       CsgoUseMic.setAdapter(adapterUseMic);

       ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
       adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
       CsgoPrefHours.setAdapter(adapterPrefHours);

       ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayCsgoRanks,R.layout.spinner_item);
       adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
       CsgoRanks.setAdapter(adapterRanks);



    }

}