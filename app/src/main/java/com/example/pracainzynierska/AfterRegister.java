package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AfterRegister extends AppCompatActivity {

    final String TAG = "AfterRegister";

    Button EndReg;
    EditText Age;
    Spinner Countries;
    FirebaseAuth fAuth;
    StorageReference fStorage;
    FirebaseFirestore fStore;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_register);

            Countries = findViewById(R.id.spinnerCoutries);
            EndReg = findViewById(R.id.buttonEndReg);
            Age = findViewById(R.id.editTextAge);
            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();


            ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.ArrayCountries, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            Countries.setAdapter(adapter);

            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        username = task.getResult().getString("username");
                    }
                }
            });


            EndReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String age = Age.getText().toString();
                    String country = Countries.getSelectedItem().toString();


                    if (TextUtils.isEmpty(age)) {
                        Age.setError("To pole jest wymagane!");
                        return;
                    }

                    if (Integer.parseInt(age) > 100 || Integer.parseInt(age) <= 0) {
                        Age.setError("Podaj poprawną wartość!");
                        return;
                    }

                    DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                    Map<String, Object> user = new HashMap<>();
                    user.put("age", age);
                    user.put("country", country);
                    usersDocRef.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Dane(name,age,country) dodane do bazy");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Dane(name,age,country) nie dodane do bazy");
                        }
                    });

                    startActivity(new Intent(getApplicationContext(), Dashboard.class));

                }
            });
    }

}