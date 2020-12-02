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

public class FortniteData extends AppCompatActivity {

    final String TAG = "FortniteData";

    EditText FortniteNick,FortniteDesc;
    Spinner FortniteUseMic,FortnitePrefHours,FortniteRanks;
    Button FortniteAddGame;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortnite_data);

        FortniteNick = findViewById(R.id.editTextFortniteNick);
        FortniteDesc = findViewById(R.id.editTextFortniteDesc);
        FortniteUseMic = findViewById(R.id.spinnerFortniteUseMic);
        FortnitePrefHours = findViewById(R.id.spinnerFortnitePrefHours);
        FortniteRanks = findViewById(R.id.spinnerFortniteRank);
        FortniteAddGame = findViewById(R.id.buttonAddFortnite);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayUseMic,R.layout.spinner_item);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortniteUseMic.setAdapter(adapterUseMic);

        ArrayAdapter adapterPrefHours =  ArrayAdapter.createFromResource(this,R.array.ArrayPrefHours,R.layout.spinner_item);
        adapterPrefHours.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortnitePrefHours.setAdapter(adapterPrefHours);

        ArrayAdapter adapterRanks =  ArrayAdapter.createFromResource(this,R.array.ArrayFortniteRanks,R.layout.spinner_item);
        adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FortniteRanks.setAdapter(adapterRanks);

        FortniteAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(FortniteNick.getText().toString())) { FortniteNick.setError("To pole jest wymagane"); return; }

                InsertIntoDatabase();
            }
        });
    }

    private void InsertIntoDatabase() {
        DocumentReference fortniteDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("fortnite");
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        WriteBatch batch = fStore.batch();

        Map<String, Object> fortniteData = new HashMap<>();
        fortniteData.put("nick", FortniteNick.getText().toString());
        fortniteData.put("mic", FortniteUseMic.getSelectedItem().toString());
        fortniteData.put("hours", FortnitePrefHours.getSelectedItem().toString());
        fortniteData.put("rank", FortniteRanks.getSelectedItem().toString());
        fortniteData.put("desc", FortniteDesc.getText().toString());

        batch.set(fortniteDocRef,fortniteData);
        batch.update(usersDocRef,"usernames.fortnite",FortniteNick.getText().toString());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Batch command commited");
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });
    }
}