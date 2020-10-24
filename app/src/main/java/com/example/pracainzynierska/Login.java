package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText Email,Password;
    Button Login;
    TextView Forgot;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);
        Login = findViewById(R.id.buttonLogin);
        Forgot = findViewById(R.id.textViewForgot);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                final String TAG = "Login";

                if (TextUtils.isEmpty(email)) {
                    Email.setError("To pole jest wymagane!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Password.setError("To pole jest wymagane!");
                    return;
                }
                if (password.length() <= 6){
                    Password.setError("Hasło musi zawierać minimum 6 znaków");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Zostałeś pomyślnie zalogowany!", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(),AfterRegister.class));
                            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "Document Snapshot data: " + document.getData());
                                            String name = document.getString("name");
                                            if (name == null) {
                                                startActivity(new Intent(getApplicationContext(),AfterRegister.class));
                                            } else {
                                                startActivity(new Intent(getApplicationContext(),Dashboard.class));
                                            }
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with", task.getException());
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(Login.this, "Niepoprawny E-mail lub hasło!", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                //Czytanie z bazy danych dokumentu i przejscie do Dashboard lub AfterRegister

            }
        });

        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });



    }
}