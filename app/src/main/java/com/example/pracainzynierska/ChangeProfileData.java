package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

public class ChangeProfileData extends AppCompatActivity {

    final String TAG = "ChangeProfileData";

    Button Update;
    EditText Age;
    Spinner Countries;
    FirebaseAuth fAuth;
    StorageReference fStorage;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_data);

        Countries = findViewById(R.id.spinnerUpdateCoutries);
        Update = findViewById(R.id.buttonUpdate);
        Age = findViewById(R.id.editTextUpdateAge);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.ArrayCountries, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Countries.setAdapter(adapter);


        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                final CollectionReference playersGamesColRef = fStore.collection("games");

                boolean changedAge = false;
                boolean changedCountry = false;

                if (!Age.getText().toString().equals("") && !MyProfile.globalAge.equals(Age.getText().toString())) { changedAge = true; }
                if (Countries.getSelectedItemId() != 0 && !MyProfile.globalCountry.equals(Countries.getSelectedItem())) { changedCountry = true; }

                if (changedAge || changedCountry) {
                    final WriteBatch batch = fStore.batch();

                    // MyProfile
                    if (changedAge) { batch.update(usersDocRef, "age", Age.getText().toString()); }
                    if (changedCountry) { batch.update(usersDocRef, "country", Countries.getSelectedItem().toString()); }
                    Log.i(TAG, "batch.update for - MyProfile");
                    // End of MyProfile

                    // Players
                    final boolean finalChangedAge = changedAge;
                    final boolean finalChangedCountry = changedCountry;

                    usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                String[] gameArray = new String[] {"csgo", "lol", "fortnite", "amongus", "pubg", "apex"};
                                for (int j = 0; j < gameArray.length; j++) {
                                    if (document.contains("usernames." + gameArray[j])) {
                                        if (finalChangedAge) {
                                            batch.update(playersGamesColRef.document(gameArray[j]).collection("players").document(MyProfile.globalUsername), "age", Age.getText().toString());
                                        }
                                        if (finalChangedCountry) {
                                            batch.update(playersGamesColRef.document(gameArray[j]).collection("players").document(MyProfile.globalUsername), "country", Countries.getSelectedItem().toString());
                                        }
                                        Log.i(TAG, "batch.update for - " + gameArray[j]);
                                    }
                                }



                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getApplicationContext(), MyProfile.class));
                                            Toast.makeText(ChangeProfileData.this,"Zmieniono dane!", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "Batch Write - success");

                                        }
                                        else { Log.i(TAG, "Batch Write - failure: " + task.getException()); }

                                    }
                                });

                            }
                            else { Toast.makeText(ChangeProfileData.this,"Operacja nie powiodła się. Spróbuj później!", Toast.LENGTH_SHORT).show(); }
                        }
                    });
                    // End of Players


                } else { Toast.makeText(ChangeProfileData.this,"Zmień jakiekolwiek dane!", Toast.LENGTH_SHORT).show(); }

            }
        });

    }
}