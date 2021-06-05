package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Write;

import org.jetbrains.annotations.NotNull;

public class DeleteAccount extends AppCompatActivity {

    final String TAG = "DeleteAccount";

    EditText Password, RepeatPassword;
    Button Confirm;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Password = findViewById(R.id.editTextDeleteAccountPassword);
        RepeatPassword = findViewById(R.id.editTextDeleteAccountRepeatPassword);
        Confirm = findViewById(R.id.buttonDeleteAccountConfirm);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = fAuth.getCurrentUser();






        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wrongPasswords = false;

                String password = Password.getText().toString().trim();
                String repeatPassword = RepeatPassword.getText().toString().trim();

                if (TextUtils.isEmpty(password)) { Password.setError("To pole jest wymagane!"); wrongPasswords=true; }
                if (TextUtils.isEmpty(repeatPassword)) { RepeatPassword.setError("To pole jest wymagane!"); wrongPasswords=true; }

                if (password.length() < 6 && !TextUtils.isEmpty(password)){ // checking the password length
                    Password.setError("Hasło musi zawierać minimum 6 znaków"); wrongPasswords = true;
                    if (password.equals(repeatPassword)) RepeatPassword.setError(null);
                }
                if (!password.equals(repeatPassword) && !TextUtils.isEmpty(repeatPassword)) { // checking if repeated password is the same
                    RepeatPassword.setError("Hasła nie są takie same!"); wrongPasswords = true;
                }
                if (password.equals(repeatPassword) && password.length() >= 6) RepeatPassword.setError(null);


                if (wrongPasswords) { return; }

                AlertDialog.Builder dialog = new AlertDialog.Builder(DeleteAccount.this);
                dialog.setTitle("Czy na pewno chcesz usunąć konto?");
                dialog.setMessage("Konto zostanie trwale usunięte z systemu wraz z zawartościa profilu! Dotychczasowy dostęp do aplikacji będzie niemożliwy!");
                dialog.setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), Password.getText().toString().trim());

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "ReAuthentication - success");

                                    // Storage - deleting before user.delete(), because we lose permission to access it while we have no account
                                    StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + MyProfile.globalUsername + "/profile.jpg");
                                    StorageReference profileRef2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://pracainz-ddfd5.appspot.com/users/" + MyProfile.globalUsername + "/profile.jpg"); // alternative version
                                    profileRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) { Log.i(TAG, "Storage File Deleted (NOT FULL URL) - success"); }
                                            else { Log.i(TAG, "Storage Delete File Failure (NOT FULL URL) - Niepowodzenie spowodowane: ", task.getException()); }
                                        }
                                    });
                                    profileRef2.delete().addOnCompleteListener(new OnCompleteListener<Void>() { // second attempt - to be sure
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) { Log.i(TAG, "Storage File Deleted (FULL URL) - success"); }
                                            else { Log.i(TAG, "Storage Delete File Failure (FULL URL) - Niepowodzenie spowodowane: ", task.getException()); }
                                        }
                                    });
                                    // End of Storage

                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() { // Deleting user account
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i(TAG, "Main Account Delete - success");

                                                String[] gameArray = new String[] {"csgo", "lol", "fortnite", "amongus", "pubg", "apex"};
                                                WriteBatch batch = fStore.batch();

                                                // MyProfile
                                                DocumentReference usersDocRef = fStore.collection("users").document(user.getUid());
                                                CollectionReference usersGamesColRef = usersDocRef.collection("games");

                                                batch.delete(usersDocRef);
                                                for (int j=0; j<gameArray.length; j++) {
                                                    batch.delete(usersGamesColRef.document(gameArray[j]));
                                                }
                                                // End of MyProfile

                                                // Players
                                                CollectionReference playersGamesColRef = fStore.collection("games");

                                                for (int j=0; j<gameArray.length; j++) {
                                                    batch.update(playersGamesColRef.document(gameArray[j]), "players." + MyProfile.globalUsername, FieldValue.delete());
                                                    batch.delete(playersGamesColRef.document(gameArray[j]).collection("players").document(MyProfile.globalUsername));
                                                }
                                                // End of Players

                                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if (task.isSuccessful()) { Log.i(TAG, "Remaining Data Batch Delete - success"); }
                                                        else { Log.i(TAG, "Remaining Data Batch Delete - failure: " + task.getException()); }
                                                    }
                                                });

                                                // SharedPreferences
                                                String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName); // preference name - file with key-value
                                                SharedPreferences preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);
                                                if (preferences.contains("pref_automaticLogin")) {
                                                    preferences.edit().clear().apply();
                                                    Log.i(TAG, "SharedPreferences Delete - success");
                                                }
                                                // End of SharedPreferences

                                                Intent intent = new Intent(DeleteAccount.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            }
                                            else {
                                                Toast.makeText(DeleteAccount.this, "Usunięcie konta nie powiodło się! Spróbuj później!", Toast.LENGTH_SHORT).show();
                                                Log.i(TAG, "Account Delete - failure: " + task.getException());
                                            }
                                        }
                                    });
                                }
                                else {
                                    Password.setError("Podano nieprawidłowe hasło!");
                                    Log.i(TAG, "ReAuthentication credentials - failure: " + task.getException());
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

    }
}