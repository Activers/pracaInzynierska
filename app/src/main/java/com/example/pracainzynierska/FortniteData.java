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

public class FortniteData extends AppCompatActivity {

    final String TAG = "FortniteData";

    EditText FortniteNick,FortniteDesc;
    Spinner FortniteUseMic,FortniteRanks;
    Button FortniteAddGame, FortnitePrefHoursMorning, FortnitePrefHoursAfternoon, FortnitePrefHoursEvening, FortnitePrefHoursNight;;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    boolean prefMorning = false;
    boolean prefAfternoon = false;
    boolean prefEvening = false;
    boolean prefNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortnite_data);

        FortniteNick = findViewById(R.id.editTextFortniteNick);
        FortniteDesc = findViewById(R.id.editTextFortniteDesc);
        FortniteUseMic = findViewById(R.id.spinnerFortniteUseMic);
        FortniteRanks = findViewById(R.id.spinnerFortniteRank);
        FortniteAddGame = findViewById(R.id.buttonAddFortnite);

        FortnitePrefHoursMorning = findViewById(R.id.buttonFortnitePrefHoursMorning);
        FortnitePrefHoursAfternoon = findViewById(R.id.buttonFortnitePrefHoursAfternoon);
        FortnitePrefHoursEvening = findViewById(R.id.buttonFortnitePrefHoursEvening);
        FortnitePrefHoursNight = findViewById(R.id.buttonFortnitePrefHoursNight);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortniteUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayFortniteRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortniteRanks.setAdapter(adapterRanks);

        //Pref Hours
        FortnitePrefHoursMorning.setOnClickListener(new View.OnClickListener() {
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

        FortnitePrefHoursAfternoon.setOnClickListener(new View.OnClickListener() {
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

        FortnitePrefHoursEvening.setOnClickListener(new View.OnClickListener() {
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

        FortnitePrefHoursNight.setOnClickListener(new View.OnClickListener() {
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

        FortniteAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(FortniteNick.getText().toString())) { FortniteNick.setError("To pole jest wymagane"); return; }
                if (!prefMorning && !prefAfternoon && !prefEvening && !prefNight) { Toast.makeText(FortniteData.this, "Zaznacz przynajmniej jedną preferowaną godzinę", Toast.LENGTH_SHORT).show(); return;}

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
        DocumentReference usersGamesDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("fortnite");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        Map<String, Object> fortniteData = new HashMap<>();
        fortniteData.put("nick", FortniteNick.getText().toString());
        fortniteData.put("mic", FortniteUseMic.getSelectedItem().toString());
        ArrayList<String> prefHoursArrayList = new ArrayList<>();
        if (prefMorning) prefHoursArrayList.add("Rano");
        if (prefAfternoon) prefHoursArrayList.add("Po południu");
        if (prefEvening) prefHoursArrayList.add("Wieczorem");
        if (prefNight) prefHoursArrayList.add("W nocy");
        fortniteData.put("hours", prefHoursArrayList);
        fortniteData.put("rank", FortniteRanks.getSelectedItem().toString());
        fortniteData.put("desc", FortniteDesc.getText().toString());

        batch.set(usersGamesDocRef,fortniteData);
        batch.update(usersDocRef,"usernames.fortnite",FortniteNick.getText().toString());
        // End MyProfile

        // Players
        DocumentReference gamesDocRef = fStore.collection("games").document("fortnite");
        DocumentReference gamesPlayersDocRef = fStore.collection("games").document("fortnite").collection("players").document(MyProfile.globalUsername);

        Map<String, Object> fortnitePlayersData = new HashMap<>();
        fortnitePlayersData.putAll(fortniteData);
        fortnitePlayersData.putAll(profileData);

        batch.set(gamesPlayersDocRef, fortnitePlayersData);
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".nick", FortniteNick.getText().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".mic", FortniteUseMic.getSelectedItem().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".rank", FortniteRanks.getSelectedItem().toString());
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