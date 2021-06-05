package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

    CheckBox rememberMe;
    SharedPreferences preferences;

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

        rememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName);
        preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String TAG = "Login";

                boolean wrongData = false;

                if (TextUtils.isEmpty(email)) { Email.setError("To pole jest wymagane!"); wrongData = true; }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(email)) { // checking if email is valid
                    Email.setError("Podany format jest nieprawidłowy!"); wrongData = true;
                }
                if (TextUtils.isEmpty(password)) { Password.setError("To pole jest wymagane!"); wrongData = true; }
                if (password.length() < 6 && !TextUtils.isEmpty(password)) { Password.setError("Hasło musi zawierać minimum 6 znaków"); wrongData = true; }

                if (wrongData) { return; }

                progressBar.setVisibility(View.VISIBLE);



                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Zostałeś pomyślnie zalogowany!", Toast.LENGTH_SHORT).show();
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.i(TAG, "Document Snapshot data: " + document.getData());
                                            String age = document.getString("age");

                                            if (rememberMe.isChecked()) {

                                                Boolean isChecked = rememberMe.isChecked();
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("pref_email", email);
                                                editor.putString("pref_password", password);
                                                editor.putBoolean("pref_automaticLogin", isChecked);
                                                editor.apply();
                                                Log.i(TAG, "Ustawiono autologowanie - zapisano email, password, check");
                                            } else {
                                                preferences.edit().clear().apply();
                                                Log.i(TAG, "Usunieto autologowanie");
                                            }

                                            if (age == null) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                startActivity(new Intent(getApplicationContext(),AfterRegister.class));
                                            } else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                MyProfile.globalUsername = document.getString("username");
                                                MyProfile.globalAge = document.getString("age");
                                                MyProfile.globalCountry = document.getString("country");
                                                startActivity(new Intent(getApplicationContext(),Dashboard.class));
                                            }
                                        } else {
                                            Log.i(TAG, "Nie znaleziono dokumentu");
                                        }
                                    } else {
                                        Log.i(TAG, "niepowodzenie spowodowane: ", task.getException());
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(Login.this, "Niepoprawny E-mail lub hasło!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Niezalogowano bo: ", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
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