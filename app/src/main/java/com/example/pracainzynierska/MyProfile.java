package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {


    final String TAG = "MyProfile";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference fStorage;
    CircleImageView ProfileImage,addGame;
    RecyclerView recyclerView;

    LinearLayoutManager layoutManager; // lub RecyclerView.LayoutManager

    private ArrayList<Model> modelList;
    private RecyclerAdapter recyclerAdapter; // lub RecyclerView.Adapter

    TextView username, country, age, addGame2;

    RelativeLayout list_root;
    CardView List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        fAuth = FirebaseAuth.getInstance();
        ProfileImage = findViewById(R.id.imageViewAvatar);
        addGame = findViewById(R.id.imageViewAddGame);
        addGame2 = findViewById(R.id.textViewAddGame);
        recyclerView = findViewById(R.id.list);
        fStore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.textViewProfileUsername);
        country = findViewById(R.id.textViewCountry);
        age = findViewById(R.id.textViewAge);

        list_root = findViewById(R.id.list_root);
        List = findViewById(R.id.cardViewProfile);


        /// to jest na profilowe to dziala
        try {
            fStorage = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = fStorage.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).resize(250, 300).noFade().rotate(270).into(ProfileImage);
                }
            });
        } catch (Exception e) {}
        ///tu sie konczy profilowe


        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfile.this,ChooseGame.class));
            }
        });


        addGame2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfile.this,ChooseGame.class));
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        modelList = new ArrayList<>();

        ClearAll();

        GetDataFromFirebase();
    }

    private void GetDataFromFirebase() {

        ////tu skonczylem https://www.youtube.com/watch?v=BrDX6VTgTkg minuta 31:15

        // sciaganie i ustawianie danych profilowych z bazy
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    username.setText(getResources().getString(R.string.textViewProfileUsername) + " " + document.getString("username"));
                    country.setText(getResources().getString(R.string.textViewCountry) + " " + document.getString("country"));
                    age.setText(getResources().getString(R.string.textViewAge) + " " + document.getString("age"));

                    ////// BRAKUJE JESZCZE OPISU

                    Map<String,String> usernamesMap = (Map<String, String>) document.get("usernames");
                    String apexUsername = usernamesMap.get("APEX");
                    String csgoUsername = usernamesMap.get("CS:GO");
                    String lolUsername = usernamesMap.get("LOL");
                    String pubgUsername = usernamesMap.get("PUBG");

                    if (apexUsername != null) {
                        modelList.add(new Model("APEX","Nick: " + apexUsername));
                    }

                    if (csgoUsername != null) {
                        modelList.add(new Model("CS:GO","Nick: " + csgoUsername));
                    }

                    if (lolUsername != null) {
                        modelList.add(new Model("League of Legends","Nick: " + lolUsername));
                    }

                    if (pubgUsername != null) {
                        modelList.add(new Model("PUBG","Nick: " + pubgUsername));
                    }
                } else {
                    Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                }

                // wyswietlanie listy poprzez adepter i recyclerview
                recyclerAdapter = new RecyclerAdapter(getApplicationContext(), modelList);
                recyclerView.setAdapter(recyclerAdapter); // wlozenie listy do recyclerView
                recyclerAdapter.notifyDataSetChanged();
            }
        });
        // koniec rzeczy zwiazanych z baza
    }

    private void ClearAll(){
        if(modelList != null){
            modelList.clear();
            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        modelList = new ArrayList<>();
    }

}