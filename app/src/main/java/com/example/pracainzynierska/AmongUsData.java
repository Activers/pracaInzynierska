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

public class AmongUsData extends AppCompatActivity {

    final String TAG = "AmongUsData";

    EditText AmongUsNick, AmongUsDesc;
    Spinner AmongUseMic, AmongUsPrefHours, AmongUsRanks;
    Button AmongUsAddGame;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_among_us_data);

        AmongUsNick = findViewById(R.id.editTextAmongUsNick);
        AmongUsDesc = findViewById(R.id.editTextAmongUsDesc);
        AmongUseMic = findViewById(R.id.spinnerAmongUseMic);
        AmongUsPrefHours = findViewById(R.id.spinnerAmongUsPrefHours);
        AmongUsRanks = findViewById(R.id.spinnerAmongUsRank);
        AmongUsAddGame = findViewById(R.id.buttonAddAmongUs);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongUsPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayFortniteRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AmongUsRanks.setAdapter(adapterRanks);

        AmongUsAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(AmongUsNick.getText().toString())) { AmongUsNick.setError("To pole jest wymagane"); return; }
                InsertIntoDatabase();
            }
        });

    }

    private void InsertIntoDatabase() {
        DocumentReference amongusDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("amongus");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        WriteBatch batch = fStore.batch();

        Map<String, Object> amongusData = new HashMap<>();
        amongusData.put("nick", AmongUsNick.getText().toString());
        amongusData.put("mic", AmongUseMic.getSelectedItem().toString());
        amongusData.put("hours", AmongUsPrefHours.getSelectedItem().toString());
        amongusData.put("rank", AmongUsRanks.getSelectedItem().toString());
        amongusData.put("desc", AmongUsDesc.getText().toString());

        batch.set(amongusDocRef,amongusData);
        batch.update(usersDocRef,"usernames.amongus",AmongUsNick.getText().toString());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Batch command commited");
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });
    }

}