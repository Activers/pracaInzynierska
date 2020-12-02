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

public class LolData extends AppCompatActivity {

    final String TAG = "LolData";

    EditText LolNick, LolDesc;
    Spinner LolUseMic,LolPrefHours,LolRanks;
    Button LolAddGame;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lol_data);

        LolNick = findViewById(R.id.editTextLolNick);
        LolDesc = findViewById(R.id.editTextLolDesc);
        LolUseMic = findViewById(R.id.spinnerLolUseMic);
        LolPrefHours = findViewById(R.id.spinnerLolPrefHours);
        LolRanks = findViewById(R.id.spinnerLolRank);
        LolAddGame = findViewById(R.id.buttonAddLol);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayLolRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        LolRanks.setAdapter(adapterRanks);

        Log.d(TAG, "PRZED LISTENEREM");

        LolAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(LolNick.getText().toString())) { LolNick.setError("To pole jest wymagane"); return; }

                InsertIntoDatabase();
            }
        });

    }

    private void InsertIntoDatabase() {

        DocumentReference lolDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("lol");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        WriteBatch batch = fStore.batch();

        Map<String, Object> lolData = new HashMap<>();
        lolData.put("nick", LolNick.getText().toString());
        lolData.put("mic", LolUseMic.getSelectedItem().toString());
        lolData.put("hours", LolPrefHours.getSelectedItem().toString());
        lolData.put("rank", LolRanks.getSelectedItem().toString());
        lolData.put("desc", LolDesc.getText().toString());

        batch.set(lolDocRef,lolData);
        batch.update(usersDocRef,"usernames.lol",LolNick.getText().toString());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Batch command commited");
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });


    }
}