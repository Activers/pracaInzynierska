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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApexData extends AppCompatActivity {

    final String TAG = "ApexData";

    EditText ApexNick, ApexDesc;
    Spinner ApexUseMic,ApexRanks;
    Button ApexAddGame, ApexPrefHoursMorning, ApexPrefHoursAfternoon, ApexPrefHoursEvening, ApexPrefHoursNight;;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    boolean prefMorning = false;
    boolean prefAfternoon = false;
    boolean prefEvening = false;
    boolean prefNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apex_data);

        ApexNick = findViewById(R.id.editTextApexNick);
        ApexDesc = findViewById(R.id.editTextApexDesc);
        ApexUseMic = findViewById(R.id.spinnerApexUseMic);
        ApexRanks = findViewById(R.id.spinnerApexRank);
        ApexAddGame = findViewById(R.id.buttonAddApex);

        ApexPrefHoursMorning = findViewById(R.id.buttonApexPrefHoursMorning);
        ApexPrefHoursAfternoon = findViewById(R.id.buttonApexPrefHoursAfternoon);
        ApexPrefHoursEvening = findViewById(R.id.buttonApexPrefHoursEvening);
        ApexPrefHoursNight = findViewById(R.id.buttonApexPrefHoursNight);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayApexRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexRanks.setAdapter(adapterRanks);

        //Pref Hours
        ApexPrefHoursMorning.setOnClickListener(new View.OnClickListener() {
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

        ApexPrefHoursAfternoon.setOnClickListener(new View.OnClickListener() {
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

        ApexPrefHoursEvening.setOnClickListener(new View.OnClickListener() {
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

        ApexPrefHoursNight.setOnClickListener(new View.OnClickListener() {
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

        ApexAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(ApexNick.getText().toString())) { ApexNick.setError("To pole jest wymagane"); return; }
                if (!prefMorning && !prefAfternoon && !prefEvening && !prefNight) { Toast.makeText(ApexData.this, "Zaznacz przynajmniej jedną preferowaną godzinę", Toast.LENGTH_SHORT).show(); return;}

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
        DocumentReference usersGamesDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("apex");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        Map<String, Object> apexData = new HashMap<>();
        apexData.put("nick", ApexNick.getText().toString());
        apexData.put("mic", ApexUseMic.getSelectedItem().toString());
        ArrayList<String> prefHoursArrayList = new ArrayList<>();
        if (prefMorning) prefHoursArrayList.add("Rano");
        if (prefAfternoon) prefHoursArrayList.add("Po południu");
        if (prefEvening) prefHoursArrayList.add("Wieczorem");
        if (prefNight) prefHoursArrayList.add("W nocy");
        apexData.put("hours", prefHoursArrayList);
        apexData.put("rank", ApexRanks.getSelectedItem().toString());
        apexData.put("desc", ApexDesc.getText().toString());

        batch.set(usersGamesDocRef,apexData);
        batch.update(usersDocRef,"usernames.apex",ApexNick.getText().toString());
        // End MyProfile

        // Players
        DocumentReference gamesDocRef = fStore.collection("games").document("apex");
        DocumentReference gamesPlayersDocRef = fStore.collection("games").document("apex").collection("players").document(MyProfile.globalUsername);

        Map<String, Object> apexPlayersData = new HashMap<>();
        apexPlayersData.putAll(apexData);
        apexPlayersData.putAll(profileData);

        batch.set(gamesPlayersDocRef, apexPlayersData);
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".nick", ApexNick.getText().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".mic", ApexUseMic.getSelectedItem().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".rank", ApexRanks.getSelectedItem().toString());
        // End Players

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Batch command commited");
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
                if (!task.isSuccessful()) { Log.d(TAG, "Batch Failure!!!: " + task.getException()); }
            }
        });
    }
}