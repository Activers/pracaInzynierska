package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LolData extends AppCompatActivity {

    final String TAG = "LolData";

    EditText LolNick, LolDesc;
    Spinner LolUseMic,LolRanks;
    Button LolAddGame, LolPrefHoursMorning, LolPrefHoursAfternoon, LolPrefHoursEvening, LolPrefHoursNight;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    boolean prefMorning = false;
    boolean prefAfternoon = false;
    boolean prefEvening = false;
    boolean prefNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lol_data);

        LolNick = findViewById(R.id.editTextLolNick);
        LolDesc = findViewById(R.id.editTextLolDesc);
        LolUseMic = findViewById(R.id.spinnerLolUseMic);
        LolRanks = findViewById(R.id.spinnerLolRank);
        LolAddGame = findViewById(R.id.buttonAddLol);

        LolPrefHoursMorning = findViewById(R.id.buttonLolPrefHoursMorning);
        LolPrefHoursAfternoon = findViewById(R.id.buttonLolPrefHoursAfternoon);
        LolPrefHoursEvening = findViewById(R.id.buttonLolPrefHoursEvening);
        LolPrefHoursNight = findViewById(R.id.buttonLolPrefHoursNight);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayLolRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolRanks.setAdapter(adapterRanks);

        //Pref Hours
        LolPrefHoursMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefMorning) {
                    prefMorning = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefMorning = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });

        LolPrefHoursAfternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefAfternoon) {
                    prefAfternoon = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefAfternoon = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });

        LolPrefHoursEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefEvening) {
                    prefEvening = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefEvening = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });

        LolPrefHoursNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefNight) {
                    prefNight = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefNight = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });
        // End of Pref Hours

        LolAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(LolNick.getText().toString())) { LolNick.setError("To pole jest wymagane"); return; }
                if (!prefMorning && !prefAfternoon && !prefEvening && !prefNight) { Toast.makeText(LolData.this, "Zaznacz przynajmniej jedną preferowaną godzinę", Toast.LENGTH_SHORT).show(); return;}

                InsertIntoDatabase();
            }
        });

    }

    private void InsertIntoDatabase() {

        Map<String,Object> profileData = new HashMap<>();
        profileData.put("username", MyProfile.globalUsername);
        profileData.put("country", MyProfile.globalCountry);
        profileData.put("age", MyProfile.globalAge);

        WriteBatch batch = fStore.batch();

        // MyProfile
        DocumentReference lolDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("lol");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        Map<String, Object> lolData = new HashMap<>();
        lolData.put("nick", LolNick.getText().toString());
        lolData.put("mic", LolUseMic.getSelectedItem().toString());
        ArrayList<String> prefHoursArrayList = new ArrayList<>();
        if (prefMorning) prefHoursArrayList.add("Rano");
        if (prefAfternoon) prefHoursArrayList.add("Po południu");
        if (prefEvening) prefHoursArrayList.add("Wieczorem");
        if (prefNight) prefHoursArrayList.add("W nocy");
        lolData.put("hours", prefHoursArrayList);
        lolData.put("rank", LolRanks.getSelectedItem().toString());
        lolData.put("desc", LolDesc.getText().toString());

        batch.set(lolDocRef,lolData);
        batch.update(usersDocRef,"usernames.lol",LolNick.getText().toString());
        // End MyProfile

        // Players
        DocumentReference gamesDocRef = fStore.collection("games").document("lol");
        DocumentReference gamesPlayersDocRef = fStore.collection("games").document("lol").collection("players").document(MyProfile.globalUsername);

        Map<String, Object> lolPlayersData = new HashMap<>();
        lolPlayersData.putAll(lolData);
        lolPlayersData.putAll(profileData);

        batch.set(gamesPlayersDocRef, lolPlayersData);
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".nick", LolNick.getText().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".mic", LolUseMic.getSelectedItem().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".rank", LolRanks.getSelectedItem().toString());
        // End Players

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Batch command commited");
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });


    }
}