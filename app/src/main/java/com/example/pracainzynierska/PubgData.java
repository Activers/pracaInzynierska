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

public class PubgData extends AppCompatActivity {

    final String TAG = "PubgData";

    EditText PubgNick, PubgDesc;
    Spinner PubgUseMic,PubgRanks;
    Button PubgAddGame, PubgPrefHoursMorning, PubgPrefHoursAfternoon, PubgPrefHoursEvening, PubgPrefHoursNight;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    boolean prefMorning = false;
    boolean prefAfternoon = false;
    boolean prefEvening = false;
    boolean prefNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubg_data);

        PubgNick = findViewById(R.id.editTextPubgNick);
        PubgDesc = findViewById(R.id.editTextPubgDesc);
        PubgUseMic = findViewById(R.id.spinnerPubgUseMic);
        PubgRanks = findViewById(R.id.spinnerPubgRank);
        PubgAddGame = findViewById(R.id.buttonAddPubg);

        PubgPrefHoursMorning = findViewById(R.id.buttonPubgPrefHoursMorning);
        PubgPrefHoursAfternoon = findViewById(R.id.buttonPubgPrefHoursAfternoon);
        PubgPrefHoursEvening = findViewById(R.id.buttonPubgPrefHoursEvening);
        PubgPrefHoursNight = findViewById(R.id.buttonPubgPrefHoursNight);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayPubgRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgRanks.setAdapter(adapterRanks);

        //Pref Hours
        PubgPrefHoursMorning.setOnClickListener(new View.OnClickListener() {
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

        PubgPrefHoursAfternoon.setOnClickListener(new View.OnClickListener() {
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

        PubgPrefHoursEvening.setOnClickListener(new View.OnClickListener() {
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

        PubgPrefHoursNight.setOnClickListener(new View.OnClickListener() {
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

        PubgAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(PubgNick.getText().toString())) { PubgNick.setError("To pole jest wymagane"); return; }
                if (!prefMorning && !prefAfternoon && !prefEvening && !prefNight) { Toast.makeText(PubgData.this, "Zaznacz przynajmniej jedną preferowaną godzinę", Toast.LENGTH_SHORT).show(); return;}

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
        DocumentReference pubgDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("pubg");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        Map<String, Object> pubgData = new HashMap<>();
        pubgData.put("nick", PubgNick.getText().toString());
        pubgData.put("mic", PubgUseMic.getSelectedItem().toString());
        ArrayList<String> prefHoursArrayList = new ArrayList<>();
        if (prefMorning) prefHoursArrayList.add("Rano");
        if (prefAfternoon) prefHoursArrayList.add("Po południu");
        if (prefEvening) prefHoursArrayList.add("Wieczorem");
        if (prefNight) prefHoursArrayList.add("W nocy");
        pubgData.put("hours", prefHoursArrayList);
        pubgData.put("rank", PubgRanks.getSelectedItem().toString());
        pubgData.put("desc", PubgDesc.getText().toString());

        batch.set(pubgDocRef,pubgData);
        batch.update(usersDocRef,"usernames.pubg",PubgNick.getText().toString());
        // End MyProfile

        // Players
        DocumentReference gamesDocRef = fStore.collection("games").document("pubg");
        DocumentReference gamesPlayersDocRef = fStore.collection("games").document("pubg").collection("players").document(MyProfile.globalUsername);

        Map<String, Object> pubgPlayersData = new HashMap<>();
        pubgPlayersData.putAll(pubgData);
        pubgPlayersData.putAll(profileData);

        batch.set(gamesPlayersDocRef, pubgPlayersData);
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".nick", PubgNick.getText().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".mic", PubgUseMic.getSelectedItem().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".rank", PubgRanks.getSelectedItem().toString());
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