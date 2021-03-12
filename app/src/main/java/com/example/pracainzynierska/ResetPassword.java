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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity {

    EditText Password,RepeatPass;
    Button NewPassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Password = findViewById(R.id.editTextNewPassword);
        RepeatPass = findViewById(R.id.editTextRepeatNewPass);
        NewPassword = findViewById(R.id.buttonNewPassword);
        fAuth = FirebaseAuth.getInstance();


        NewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String TAG = "Password";

                String password = Password.getText().toString();
                String repeatPass = RepeatPass.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Password.setError("To pole jest wymagane!");
                    return;
                }
                if (!password.equals(repeatPass)){
                    RepeatPass.setError("Hasła nie są takie same!!");
                    return;
                }

                user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Haslo zaktualizowanie");
                            Toast.makeText(ResetPassword.this, "Hasło zostało zmienione!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(ResetPassword.this, "Błąd zmiany hasła!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error! Haslo nie zaktualizowane");
                        }
                    }
                });

            }
        });


    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}