package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity {

    EditText NewPassword, RepeatNewPass, OldPassword;
    Button ChangePassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        OldPassword = findViewById(R.id.editTextOldPassword);
        NewPassword = findViewById(R.id.editTextNewPassword);
        RepeatNewPass = findViewById(R.id.editTextRepeatNewPass);
        ChangePassword = findViewById(R.id.buttonChangeOldPassword);
        fAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fAuth.signInWithEmailAndPassword(user.getEmail(), OldPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final String TAG = "Password";


                            final String newPassword = NewPassword.getText().toString().trim();
                            String repeatNewPass = RepeatNewPass.getText().toString().trim();
                            if (TextUtils.isEmpty(newPassword)) {
                                NewPassword.setError("To pole jest wymagane!");
                                return;
                            }
                            if (!newPassword.equals(repeatNewPass)) {
                                RepeatNewPass.setError("Hasła nie są takie same!");
                                return;
                            }

                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // SharedPreferences
                                        String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName); // preference name - file with key-value
                                        SharedPreferences preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);
                                        if (preferences.contains("pref_automaticLogin")) {
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("pref_password", newPassword);
                                            editor.apply();
                                            Log.i(TAG, "Zmieniono hasło autologowania - zapisano password");
                                        }
                                        // End of SharedPreferences

                                        Log.d(TAG, "Hasło zaktualizowanie");
                                        Toast.makeText(ResetPassword.this, "Hasło zostało zmienione!", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(ResetPassword.this, "Błąd zmiany hasła!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Error! Haslo nie zaktualizowane");
                                    }
                                }
                            });
                        }
                        else { OldPassword.setError("Podano nieprawidłowe stare hasło!"); }
                    }
                });
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}