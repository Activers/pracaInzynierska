package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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


public class Register extends AppCompatActivity {

    EditText Username,Email,Password;
    Button Register;
    TextView Login;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    CheckBox checkBoxTerms;
    TextView textViewTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Username = findViewById(R.id.editTextUsername);
        Email = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);
        Register = findViewById(R.id.buttonRegister);
        Login = findViewById(R.id.textViewLogin);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        checkBoxTerms = findViewById(R.id.checkBoxTerms);
        textViewTerms = findViewById(R.id.textViewTerm);

        textViewTerms.setText(Html.fromHtml("Akceptuje <u>Regulamin</u> oraz politykę prywatności"));
        textViewTerms.setTextSize(16);

        textViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Register.this,Terms.class));
            }
        });

        //if(fAuth.getCurrentUser() != null){
        //    startActivity(new Intent(getApplicationContext(),MainActivity.class));
        //    finish();
        //}

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (checkBoxTerms.isChecked()){
                }else{
                    Toast.makeText(Register.this, "Musisz zaakceptować regulamin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Password.setError("To pole jest wymagane!");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Email.setError("To pole jest wymagane!");
                    return;
                }
                if (password.length() <= 6){
                    Password.setError("Hasło musi zawierać minimum 6 znaków");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "Zostałeś pomyślnie zarejestrowany!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        //else {
                         //   Toast.makeText(Register.this, "Błąd rejestracji! Spróbuj ponownie później.", Toast.LENGTH_SHORT).show();
                        //}
                        if(fAuth.getCurrentUser() != null){
                            Toast.makeText(Register.this, "Takie konto już istnieje!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });

    }



}