package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.HashMap;
import java.util.Map;

public class CsgoData extends AppCompatActivity {

    final String TAG = "CsgoData";

    EditText CsgoNick, CsgoDesc;
    Spinner CsgoUseMic,CsgoPrefHours,CsgoRanks;
    Button CsgoAddGame;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csgo_data);

       CsgoNick = findViewById(R.id.editTextCsgoNick);
       CsgoDesc = findViewById(R.id.editTextCsgoDesc);
       CsgoUseMic = findViewById(R.id.spinnerCsgoUseMic);
       CsgoPrefHours = findViewById(R.id.spinnerCsgoPrefHours);
       CsgoRanks = findViewById(R.id.spinnerCsgoRank);
       CsgoAddGame = findViewById(R.id.buttonAddCsgo);

       fStore = FirebaseFirestore.getInstance();
       fAuth = FirebaseAuth.getInstance();

       ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
       adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
       CsgoUseMic.setAdapter(adapterUseMic);

       ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
       adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
       CsgoPrefHours.setAdapter(adapterPrefHours);

       ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayCsgoRanks,R.layout.spinner_item);
       adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
       CsgoRanks.setAdapter(adapterRanks);


       CsgoAddGame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if (TextUtils.isEmpty(CsgoNick.getText().toString())) { CsgoNick.setError("To pole jest wymagane"); return; }

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
        DocumentReference usersGamesDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("csgo");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

        Map<String, Object> csgoData = new HashMap<>();
        csgoData.put("nick", CsgoNick.getText().toString());
        csgoData.put("mic", CsgoUseMic.getSelectedItem().toString());
        csgoData.put("hours", CsgoPrefHours.getSelectedItem().toString());
        csgoData.put("rank", CsgoRanks.getSelectedItem().toString());
        csgoData.put("desc", CsgoDesc.getText().toString());

        batch.set(usersGamesDocRef,csgoData);
        batch.update(usersDocRef,"usernames.csgo",CsgoNick.getText().toString());
        // End MyProfile

        // Players
        DocumentReference gamesDocRef = fStore.collection("games").document("csgo");
        DocumentReference gamesPlayersDocRef = fStore.collection("games").document("csgo").collection("players").document(MyProfile.globalUsername);

        Map<String, Object> csgoPlayersData = new HashMap<>();
        csgoPlayersData.putAll(csgoData);
        csgoPlayersData.putAll(profileData);

        batch.set(gamesPlayersDocRef, csgoPlayersData);
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".nick", CsgoNick.getText().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".mic", CsgoUseMic.getSelectedItem().toString());
        batch.update(gamesDocRef,"players." + MyProfile.globalUsername + ".rank", CsgoRanks.getSelectedItem().toString());
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