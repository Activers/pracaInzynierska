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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class PubgData extends AppCompatActivity {

    final String TAG = "PubgData";

    EditText PubgNick, PubgDesc;
    Spinner PubgUseMic,PubgPrefHours,PubgRanks;
    Button PubgAddGame;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubg_data);

        PubgNick = findViewById(R.id.editTextPubgNick);
        PubgDesc = findViewById(R.id.editTextPubgDesc);
        PubgUseMic = findViewById(R.id.spinnerPubgUseMic);
        PubgPrefHours = findViewById(R.id.spinnerPubgPrefHours);
        PubgRanks = findViewById(R.id.spinnerPubgRank);
        PubgAddGame = findViewById(R.id.buttonAddPubg);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayPubgRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PubgRanks.setAdapter(adapterRanks);

        PubgAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(PubgNick.getText().toString())) { PubgNick.setError("To pole jest wymagane"); return; }

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
        pubgData.put("hours", PubgPrefHours.getSelectedItem().toString());
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