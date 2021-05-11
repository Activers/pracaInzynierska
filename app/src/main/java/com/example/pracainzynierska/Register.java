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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    EditText Username,Email,Password;
    Button Register;
    TextView Login;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
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
        fStore = FirebaseFirestore.getInstance();
        checkBoxTerms = findViewById(R.id.checkBoxTerms);
        textViewTerms = findViewById(R.id.textViewAllTerms);

        textViewTerms.setText(Html.fromHtml("<u>Kliknij tutaj</u> by wyświetlić regulamin"));
        textViewTerms.setTextSize(16);

        textViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Register.this,Terms.class));
            }
        });

        //if(fAuth.getCurrentUser() != null){
         //   startActivity(new Intent(getApplicationContext(),MainActivity.class));
        //    finish();
        //}

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String username = Username.getText().toString();

                if (TextUtils.isEmpty(username)){ // sprawdzenie username
                    Username.setError("To pole jest wymagane!");
                    return;
                }
                if (TextUtils.isEmpty(password)){ // sprawdzenie hasla
                    Password.setError("To pole jest wymagane!");
                    return;
                }
                if (password.length() < 6){ // sprawdzenie hasla
                    Password.setError("Hasło musi zawierać minimum 6 znaków");
                    return;
                }
                if (TextUtils.isEmpty(email)) { // sprawdzenie maila
                    Email.setError("To pole jest wymagane!");
                    return;
                }
                if (checkBoxTerms.isChecked()){ // sprawdzenie akceptacji regulaminu
                }else{ // po co dawac if z pustym argumentem i else? nie lepiej zrobic if (!checkBoxTerms.isChecked()) ?
                    Toast.makeText(Register.this, "Musisz zaakceptować regulamin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                fStore.collection("users").whereEqualTo("username",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Username.setError("Wybrany nick jest zajęty");
                        } else {
                            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Map<String,Object> user = new HashMap<>();
                                        user.put("username",username);
                                        user.put("e-mail",email);
                                        String uid = fAuth.getCurrentUser().getUid();
                                        fStore.collection("users").document(uid).set(user);
                                        Toast.makeText(Register.this, "Zostałeś pomyślnie zarejestrowany!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    }
                                    //else {
                                    //   Toast.makeText(Register.this, "Błąd rejestracji! Spróbuj ponownie później.", Toast.LENGTH_SHORT).show();
                                    //}
                                    //if(fAuth.getCurrentUser() != null){ // czemu to mialo sluzyc?
                                    //    Toast.makeText(Register.this, "Takie konto już istnieje!", Toast.LENGTH_SHORT).show();
                                    //}
                                }
                            });
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