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

public class ApexData extends AppCompatActivity {

    final String TAG = "ApexData";

    EditText ApexNick, ApexDesc;
    Spinner ApexUseMic,ApexPrefHours,ApexRanks;
    Button ApexAddGame;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apex_data);

        ApexNick = findViewById(R.id.editTextApexNick);
        ApexDesc = findViewById(R.id.editTextApexDesc);
        ApexUseMic = findViewById(R.id.spinnerApexUseMic);
        ApexPrefHours = findViewById(R.id.spinnerApexPrefHours);
        ApexRanks = findViewById(R.id.spinnerApexRank);
        ApexAddGame = findViewById(R.id.buttonAddApex);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexPrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayApexRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ApexRanks.setAdapter(adapterRanks);

        ApexAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(ApexNick.getText().toString())) { ApexNick.setError("To pole jest wymagane"); return; }

                InsertIntoDatabase();
            }
        });

    }

    private void InsertIntoDatabase() {
        DocumentReference apexDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("apex");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        WriteBatch batch = fStore.batch();

        Map<String, Object> apexData = new HashMap<>();
        apexData.put("nick", ApexNick.getText().toString());
        apexData.put("mic", ApexUseMic.getSelectedItem().toString());
        apexData.put("hours", ApexPrefHours.getSelectedItem().toString());
        apexData.put("rank", ApexRanks.getSelectedItem().toString());
        apexData.put("desc", ApexDesc.getText().toString());

        batch.set(apexDocRef,apexData);
        batch.update(usersDocRef,"usernames.apex",ApexNick.getText().toString());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Batch command commited");
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });
    }
}