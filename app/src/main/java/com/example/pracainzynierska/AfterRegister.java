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
import android.widget.Toast;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AfterRegister extends AppCompatActivity {

    ImageView ProfileImage;
    TextView ChangeAvatar;
    Button EndReg;
    EditText Name;
    EditText Age;
    Spinner Countries;
    FirebaseAuth fAuth;
    StorageReference fStorage;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_register);

            ProfileImage = findViewById(R.id.imageViewAvatar);
            ChangeAvatar = findViewById(R.id.textViewAvatar);
            Countries = findViewById(R.id.spinnerCoutries);
            EndReg = findViewById(R.id.buttonEndReg);
            Name = findViewById(R.id.editTextName);
            Age = findViewById(R.id.editTextAge);
            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();


            ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.ArrayCountries, R.layout.countries_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            Countries.setAdapter(adapter);

            try {
                fStorage = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = fStorage.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(ProfileImage);
                    }
                });
            } catch (Exception e) {}


            ChangeAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGallery, 1000);
                }
            });

            ProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGallery, 1000);
                }
            });

            EndReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = Name.getText().toString();
                    String age = Age.getText().toString();
                    String country = Countries.getSelectedItem().toString();

                    final String TAG = "AfterRegister";

                    if (TextUtils.isEmpty(name)) {
                        Name.setError("To pole jest wymagane!");
                        return;
                    }

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
                    user.put("name", name);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);

            }
        }

    }
    private void uploadImageToFirebase(Uri imageUri) {

        final String TAG = "AfterRegister";
        final StorageReference fileRef = fStorage.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Awatar został dodany"); // zmienic na log w konsoli
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(ProfileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Awatar nie został dodany"); // zmienic na log
            }
        });

    }
}