package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            // SPRAWDZANIE AUTOLOGOWANIA
            String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName); // nazwa preferencji / pliku gdzie skladowane beda klucz-wartosc
            preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);
            if (preferences.contains("pref_automaticLogin")) {


                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();

                String email = preferences.getString("pref_email", "no data found");
                String password = preferences.getString("pref_password", "no data found");

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Zostałeś pomyślnie zalogowany!", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(),AfterRegister.class));
                            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.i(TAG, "Document Snapshot data: " + document.getData());
                                            String name = document.getString("name");
                                            if (name == null) {
                                                startActivity(new Intent(getApplicationContext(), AfterRegister.class));
                                            } else {
                                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                            }
                                        } else {
                                            Log.i(TAG, "Nie znaleziono dokumentu");
                                        }
                                    } else {
                                        Log.i(TAG, "niepowodzenie spowodowane: ", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.i(TAG, "Autologowanie zakończone niepowodzeniem");
                            Toast.makeText(MainActivity.this, "Autologowanie zakończone niepowodzeniem!", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            // KONIEC SPRAWDZANIA AUTOLOGOWANIA

        Button buttonRegisterEmail = (Button) findViewById(R.id.buttonRegisterEmail);
        TextView textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        buttonRegisterEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Register.class));
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });

    }

    boolean singleBack = false;

    @Override
    public void onBackPressed() {
        if (singleBack) {
            finishAffinity();
            return;
        }

        this.singleBack = true;
        Toast.makeText(this, "Naciśnij ponownie WSTECZ, aby wyjść", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                singleBack=false;
            }
        }, 2000);
    }
    
}