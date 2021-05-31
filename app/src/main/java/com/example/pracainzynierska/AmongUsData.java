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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AmongUsData extends AppCompatActivity {

    final String TAG = "AmongUsData";

    EditText AmongUsNick, AmongUsDesc;
    Spinner AmongUseMic, AmongUsRanks;
    Button AmongUsAddGame, AmongUsPrefHoursMorning, AmongUsPrefHoursAfternoon, AmongUsPrefHoursEvening, AmongUsPrefHoursNight;;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    boolean prefMorning = false;
    boolean prefAfternoon = false;
    boolean prefEvening = false;
    boolean prefNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_among_us_data);

        AmongUsNick = findViewById(R.id.editTextAmongUsNick);
        AmongUsDesc = findViewById(R.id.editTextAmongUsDesc);
        AmongUseMic = findViewById(R.id.spinnerAmongUseMic);
        AmongUsRanks = findViewById(R.id.spinnerAmongUsRank);
        AmongUsAddGame = findViewById(R.id.buttonAddAmongUs);

        AmongUsPrefHoursMorning = findViewById(R.id.buttonAmongUsPrefHoursMorning);
        AmongUsPrefHoursAfternoon = findViewById(R.id.buttonAmongUsPrefHoursAfternoon);
        AmongUsPrefHoursEvening = findViewById(R.id.buttonAmongUsPrefHoursEvening);
        AmongUsPrefHoursNight = findViewById(R.id.buttonAmongUsPrefHoursNight);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayFortniteRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongUsRanks.setAdapter(adapterRanks);

        //Pref Hours
        AmongUsPrefHoursMorning.setOnClickListener(new View.OnClickListener() {
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

        AmongUsPrefHoursAfternoon.setOnClickListener(new View.OnClickListener() {
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

        AmongUsPrefHoursEvening.setOnClickListener(new View.OnClickListener() {
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

        AmongUsPrefHoursNight.setOnClickListener(new View.OnClickListener() {
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

        AmongUsAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(AmongUsNick.getText().toString())) { AmongUsNick.setError("To pole jest wymagane"); return; }
                if (!prefMorning && !prefAfternoon && !prefEvening && !prefNight) { Toast.makeText(AmongUsData.this, "Zaznacz przynajmniej jedną preferowaną godzinę", Toast.LENGTH_SHORT).show(); return;}

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
        DocumentReference usersGamesDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("amongus");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        Map<String, Object> amongusData = new HashMap<>();
        amongusData.put("nick", AmongUsNick.getText().toString());
        amongusData.put("mic", AmongUseMic.getSelectedItem().toString());
        ArrayList<String> prefHoursArrayList = new ArrayList<>();
        if (prefMorning) prefHoursArrayList.add("Rano");
        if (prefAfternoon) prefHoursArrayList.add("Po południu");
        if (prefEvening) prefHoursArrayList.add("Wieczorem");
        if (prefNight) prefHoursArrayList.add("W nocy");
        amongusData.put("hours", prefHoursArrayList);
        amongusData.put("rank", AmongUsRanks.getSelectedItem().toString());
        amongusData.put("desc", AmongUsDesc.getText().toString());

        batch.set(usersGamesDocRef,amongusData);
        batch.update(usersDocRef,"usernames.amongus",AmongUsNick.getText().toString());
        // End MyProfile

        // Players
        DocumentReference gamesDocRef = fStore.collection("games").document("amongus");
        DocumentReference gamesPlayersDocRef = fStore.collection("games").document("amongus").collection("players").document(MyProfile.globalUsername);

        Map<String, Object> amongusPlayersData = new HashMap<>();
        amongusPlayersData.putAll(amongusData);
        amongusPlayersData.putAll(profileData);

        batch.set(gamesPlayersDocRef, amongusPlayersData);
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".nick", AmongUsNick.getText().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".mic", AmongUseMic.getSelectedItem().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".rank", AmongUsRanks.getSelectedItem().toString());
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