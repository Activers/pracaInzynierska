package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

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

        final FirebaseUser user = fAuth.getCurrentUser();

        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wrongPasswords = false;

                String oldPassword = OldPassword.getText().toString().trim();
                final String newPassword = NewPassword.getText().toString().trim();
                String repeatNewPass = RepeatNewPass.getText().toString().trim();

                if (TextUtils.isEmpty(oldPassword)) { OldPassword.setError("To pole jest wymagane!"); wrongPasswords=true; }
                if (TextUtils.isEmpty(newPassword)) { NewPassword.setError("To pole jest wymagane!"); wrongPasswords=true; }
                if (TextUtils.isEmpty(repeatNewPass)) { RepeatNewPass.setError("To pole jest wymagane!"); wrongPasswords=true; }
                if (oldPassword.length() <6 && !TextUtils.isEmpty(oldPassword)) {
                    OldPassword.setError("Hasło musi zawierać minimum 6 znaków"); wrongPasswords = true;
                }
                if (!oldPassword.equals(newPassword) && newPassword.length() >= 6) NewPassword.setError(null);
                if (newPassword.length() < 6 && !TextUtils.isEmpty(newPassword)){ // checking the password length
                    NewPassword.setError("Hasło musi zawierać minimum 6 znaków"); wrongPasswords = true;
                    if (newPassword.equals(repeatNewPass)) RepeatNewPass.setError(null);
                }
                if (!oldPassword.equals(newPassword) && oldPassword.length() >=6) OldPassword.setError(null);
                if (!newPassword.equals(repeatNewPass) && !TextUtils.isEmpty(repeatNewPass)) { // checking if repeated password is the same
                    RepeatNewPass.setError("Hasła nie są takie same!"); wrongPasswords = true;
                }
                if (!oldPassword.equals(newPassword) && oldPassword.length() >=6 && newPassword.length() >= 6) { OldPassword.setError(null); NewPassword.setError(null);}
                if (newPassword.equals(repeatNewPass) && newPassword.length() >= 6) RepeatNewPass.setError(null);
                if (oldPassword.equals(newPassword) && oldPassword.length() >=6) { NewPassword.setError("Stare i nowe hasło jest identyczne!"); OldPassword.setError("Stare i nowe hasło jest identyczne!"); wrongPasswords=true; }
                if (wrongPasswords) { return; }


                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            final String TAG = "ResetPassword";
                            Log.i(TAG, "ReAuthentication - success");

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
                                        Toast.makeText(ResetPassword.this, "Błąd zmiany hasła! Spróbuj później!", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "ReAuthentication credentials - failure: " + task.getException());
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