package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
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
    CircleImageView ProfileImage;
    RecyclerView recyclerView;

    LinearLayoutManager layoutManager; // lub RecyclerView.LayoutManager

    private ArrayList<Model> modelList;
    private RecyclerAdapter recyclerAdapter; // lub RecyclerView.Adapter (to jest domyslne a RecyclerAdapter to nasza klasa z dodanymi customowymi metodami)

    TextView username, country, age;

    RelativeLayout relativeLayoutAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        fAuth = FirebaseAuth.getInstance();
        ProfileImage = findViewById(R.id.imageViewAvatar);
        recyclerView = findViewById(R.id.recyclerViewMyProfile);
        fStore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.textViewProfileUsername);
        country = findViewById(R.id.textViewCountry);
        age = findViewById(R.id.textViewAge);

        relativeLayoutAddGame = findViewById(R.id.relativeLayoutAddGame);


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


        relativeLayoutAddGame.setOnClickListener(new View.OnClickListener() { // Twoje AddGame1/AddGame2 zamienione na listener na caly relative
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfile.this, AddGame.class));
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        modelList = new ArrayList<>();

        ClearAll();

        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), modelList); // musi byc zadeklarowane przed sciagnieciem danych z bazy przez to ze lekko zamula
        recyclerView.setAdapter(recyclerAdapter);

        GetProfileDataFromFirebase();

        try {
                recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickedListener() {
                    @Override
                    public void onItemClick(int postion) { // klikniecie (na okolo?) card view

                        /*try {
                            removeItem(postion);
                            Log.d(TAG, "4 - ITEM COUNT after remove: " + recyclerAdapter.getItemCount());
                        } catch (Exception e) {
                            Log.d(TAG, "ON ITEM CLICK ERROR: " + e);
                        }
                        changeItem(postion,"Clicked");*/
                    }

                    boolean deleteGameDoubleClick = false;
                    int lastPosition;
                    @Override
                    public void onDeleteClick(int position) { // klikniecie w ikonke kosza usuwa obiekt

                        if (deleteGameDoubleClick && lastPosition == position) { // dodany bajer ze trzeba nacisnac szybko dwukrotnie ikone aby usunac - zabezpieczenie przed missclickami
                            removeItem(position);
                            //Toast.makeText(MyProfile.this, "Usuwam item", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        this.deleteGameDoubleClick = true;
                        this.lastPosition = position;
                        Toast.makeText(MyProfile.this, "Naciśnij ponownie, aby usunąć", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                deleteGameDoubleClick=false;
                            }
                        }, 700);
                    }
                });
        }catch (Exception e) { Log.d(TAG, "RECYCLER LISTENER ERROR: " + e); }
    }

    private void GetProfileDataFromFirebase() {

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
                    String csgoUsername = usernamesMap.get("csgo");
                    String lolUsername = usernamesMap.get("lol");
                    String fortniteUsername = usernamesMap.get("fortnite");
                    String amongusUsername = usernamesMap.get("amongus");
                    String pubgUsername = usernamesMap.get("pubg");
                    String apexUsername = usernamesMap.get("apex");

                    if (csgoUsername != null) {
                        modelList.add(new Model("CS:GO","Steam: " + csgoUsername));
                    }

                    if (lolUsername != null) {
                        modelList.add(new Model("League of Legends","Riot Games: " + lolUsername));
                    }

                    if (fortniteUsername != null) {
                        modelList.add(new Model("Fortnite","Epic Games: " + fortniteUsername));
                    }

                    if (amongusUsername != null) {
                        modelList.add(new Model("Among Us","Discord: " + amongusUsername));
                    }

                    if (pubgUsername != null) {
                        modelList.add(new Model("PUBG","Steam: " + pubgUsername));
                    }

                    if (apexUsername != null) {
                        modelList.add(new Model("APEX","Origin: " + apexUsername));
                    }

                } else {
                    Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                }

                // wyswietlanie listy poprzez adepter i recyclerview
                //recyclerAdapter = new RecyclerAdapter(getApplicationContext(), modelList); // Jak jest tutaj zadeklarowane to jest error ze nie ma adaptera zadeklarowanego gdy listenera sie uzywa
                recyclerView.setAdapter(recyclerAdapter); // wlozenie listy do recyclerView

                //recyclerAdapter.notifyDataSetChanged();

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

    private void removeItem(final int position) {
        final String gameName = modelList.get(position).getmGame();

        if (gameName == "CS:GO") {
            WriteBatch batch = fStore.batch();

            DocumentReference csgoDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("csgo");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.csgo", FieldValue.delete()); // usuwa dane pole z mapy
            batch.delete(csgoDocRef); // usuwa caly dokument

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyProfile.this, gameName + " usunięte z pozycji: " + position, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "League of Legends") {
            WriteBatch batch = fStore.batch();

            DocumentReference lolDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("lol");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.lol", FieldValue.delete()); // usuwa dane pole z mapy
            batch.delete(lolDocRef); // usuwa caly dokument

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyProfile.this, gameName + " usunięte z pozycji: " + position, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "Fortnite") {
            WriteBatch batch = fStore.batch();

            DocumentReference fortniteDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("fortnite");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.fortnite", FieldValue.delete()); // usuwa dane pole z mapy
            batch.delete(fortniteDocRef); // usuwa caly dokument

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyProfile.this, gameName + " usunięte z pozycji: " + position, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "Among Us") {
            WriteBatch batch = fStore.batch();

            DocumentReference amongusDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("amongus");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.amongus", FieldValue.delete()); // usuwa dane pole z mapy
            batch.delete(amongusDocRef); // usuwa caly dokument

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyProfile.this, gameName + " usunięte z pozycji: " + position, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "PUBG") {
            WriteBatch batch = fStore.batch();

            DocumentReference pubgDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("pubg");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.pubg", FieldValue.delete()); // usuwa dane pole z mapy
            batch.delete(pubgDocRef); // usuwa caly dokument

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyProfile.this, gameName + " usunięte z pozycji: " + position, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "APEX") {
            WriteBatch batch = fStore.batch();

            DocumentReference apexDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("apex");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.apex", FieldValue.delete()); // usuwa dane pole z mapy
            batch.delete(apexDocRef); // usuwa caly dokument

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyProfile.this, gameName + " usunięte z pozycji: " + position, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }

    }

    public void changeItem(int position, String text) { // moze kiedys sie przyda (String do podmianki na co chcemy w Model)
        modelList.get(position).changeGameText(text);
        recyclerAdapter.notifyItemChanged(position);
        Toast.makeText(MyProfile.this, "Item name changed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
    }

}