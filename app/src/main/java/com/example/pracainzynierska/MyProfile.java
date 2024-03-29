package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {


    final String TAG = "MyProfile";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference fStorage;
    CircleImageView ProfileImage,AddAvatar;

    ImageView ProfileEdit;

    RecyclerView recyclerView;

    LinearLayoutManager layoutManager; //or RecyclerView.LayoutManager

    private ArrayList<Model> modelList;
    private RecyclerAdapter recyclerAdapter;

    TextView username, country, age;

    RelativeLayout relativeLayoutAddGame;

    // Popup Window
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    CircleImageView popupWindowProfileImage;
    TextView popupWindowProfileUsername, popupWindowCountry, popupWindowAge, popupWindowNick, popupWindowRank, popupWindowMic, popupWindowHours, popupWindowDesc;
    ImageView popupWindowImageView;
    Button PopupWindowBack;
    // End of Popup Window

    // Global Variables
    static String globalUsername;
    static String globalCountry;
    static String globalAge;
    // End of Global Variables

    boolean inEdit;
    boolean downloadProfileImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        fAuth = FirebaseAuth.getInstance();
        ProfileEdit = findViewById(R.id.imageViewProfileEdit);
        ProfileImage = findViewById(R.id.imageViewAvatar);
        recyclerView = findViewById(R.id.recyclerViewMyProfile);
        fStore = FirebaseFirestore.getInstance();
        AddAvatar = findViewById(R.id.ImageViewAddAvatar);
        username = findViewById(R.id.textViewProfileUsername);
        country = findViewById(R.id.textViewCountry);
        age = findViewById(R.id.textViewAge);

        relativeLayoutAddGame = findViewById(R.id.relativeLayoutAddGame);

        if (globalUsername != null) { // jest problem ze zmienna globalna ze przy pierwszym wejsciu do activity
            DownloadProfileImage();
            downloadProfileImage = true;
        }

        // MyProfile Edit
        inEdit = false;
        ProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inEdit) {
                    inEdit = false;
                    for (int i=0; i < modelList.size(); i++) {
                        modelList.get(i).setVisibility(8);
                    }
                }
                else {
                    inEdit = true;
                    for (int i=0; i < modelList.size(); i++) {
                        modelList.get(i).setVisibility(0);
                    }
                }
                recyclerView.setAdapter(recyclerAdapter);
            }
        });
        // End MyProfile Edit

        relativeLayoutAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfile.this, AddGame.class);
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        modelList = new ArrayList<>();

        ClearAll();

        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), modelList); // have to be declared before downloading the data (even if the list is empty)
        recyclerView.setAdapter(recyclerAdapter);

        GetProfileDataFromFirebase();

        try {
                recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickedListener() {
                    @Override
                    public void onItemClick(int position) {

                        CreatePopupWindow(position);
                    }

                    boolean deleteGameDoubleClick = false;
                    int lastPosition;
                    @Override
                    public void onDeleteClick(int position) {

                        if (deleteGameDoubleClick && lastPosition == position) {
                            RemoveItem(position);
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

        AddAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });

    }



    private void GetProfileDataFromFirebase() {
        DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    globalUsername = document.getString("username");
                    globalCountry = document.getString("country");
                    globalAge = document.getString("age");

                    username.setText(getResources().getString(R.string.textViewProfileUsername) + " " + document.getString("username"));
                    country.setText(getResources().getString(R.string.textViewCountry) + " " + document.getString("country"));
                    age.setText(getResources().getString(R.string.textViewAge) + " " + document.getString("age"));

                    if (!downloadProfileImage) DownloadProfileImage();

                    if (document.contains("usernames")) {

                        Map<String, String> usernamesMap = (Map<String, String>) document.get("usernames");
                        String csgoUsername = usernamesMap.get("csgo");
                        String lolUsername = usernamesMap.get("lol");
                        String fortniteUsername = usernamesMap.get("fortnite");
                        String amongusUsername = usernamesMap.get("amongus");
                        String pubgUsername = usernamesMap.get("pubg");
                        String apexUsername = usernamesMap.get("apex");

                        if (csgoUsername != null) {
                            modelList.add(new Model("CS:GO", "Steam: " + csgoUsername));
                        }

                        if (lolUsername != null) {
                            modelList.add(new Model("League of Legends", "Riot Games: " + lolUsername));
                        }

                        if (fortniteUsername != null) {
                            modelList.add(new Model("Fortnite", "Epic Games: " + fortniteUsername));
                        }

                        if (amongusUsername != null) {
                            modelList.add(new Model("Among Us", "Discord: " + amongusUsername));
                        }

                        if (pubgUsername != null) {
                            modelList.add(new Model("PUBG", "Steam: " + pubgUsername));
                        }

                        if (apexUsername != null) {
                            modelList.add(new Model("APEX", "Steam: " + apexUsername));
                        }

                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
                recyclerView.setAdapter(recyclerAdapter);
            }
        });
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

    private void RemoveItem(final int position) {
        final String gameName = modelList.get(position).getGame();

        if (gameName == "CS:GO") {
            WriteBatch batch = fStore.batch();

            // MyProfile
            DocumentReference csgoDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("csgo");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.csgo", FieldValue.delete());
            batch.delete(csgoDocRef);
            // End MyProfile

            // Players
            DocumentReference gamesDocRef = fStore.collection("games").document("csgo");
            DocumentReference gamesPlayersDocRef = fStore.collection("games").document("csgo").collection("players").document(globalUsername);

            batch.update(gamesDocRef, "players." + globalUsername, FieldValue.delete());
            batch.delete(gamesPlayersDocRef);
            // End Players

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "League of Legends") {
            WriteBatch batch = fStore.batch();

            // MyProfile
            DocumentReference lolDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("lol");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.lol", FieldValue.delete());
            batch.delete(lolDocRef);
            // End MyProfile

            // Players
            DocumentReference gamesDocRef = fStore.collection("games").document("lol");
            DocumentReference gamesPlayersDocRef = fStore.collection("games").document("lol").collection("players").document(globalUsername);

            batch.update(gamesDocRef, "players." + globalUsername, FieldValue.delete());
            batch.delete(gamesPlayersDocRef);
            // End Players

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "Fortnite") {
            WriteBatch batch = fStore.batch();

            // MyProfile
            DocumentReference fortniteDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("fortnite");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.fortnite", FieldValue.delete());
            batch.delete(fortniteDocRef);
            // End MyProfile

            // Players
            DocumentReference gamesDocRef = fStore.collection("games").document("fortnite");
            DocumentReference gamesPlayersDocRef = fStore.collection("games").document("fortnite").collection("players").document(globalUsername);

            batch.update(gamesDocRef, "players." + globalUsername, FieldValue.delete());
            batch.delete(gamesPlayersDocRef);
            // End Players

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "Among Us") {
            WriteBatch batch = fStore.batch();

            // MyProfile
            DocumentReference amongusDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("amongus");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.amongus", FieldValue.delete());
            batch.delete(amongusDocRef);
            // End MyProfile

            // Players
            DocumentReference gamesDocRef = fStore.collection("games").document("amongus");
            DocumentReference gamesPlayersDocRef = fStore.collection("games").document("amongus").collection("players").document(globalUsername);

            batch.update(gamesDocRef, "players." + globalUsername, FieldValue.delete());
            batch.delete(gamesPlayersDocRef);
            // End Players

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "PUBG") {
            WriteBatch batch = fStore.batch();

            // MyProfile
            DocumentReference pubgDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("pubg");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.pubg", FieldValue.delete());
            batch.delete(pubgDocRef);
            // End MyProfile

            // Players
            DocumentReference gamesDocRef = fStore.collection("games").document("pubg");
            DocumentReference gamesPlayersDocRef = fStore.collection("games").document("pubg").collection("players").document(globalUsername);

            batch.update(gamesDocRef, "players." + globalUsername, FieldValue.delete());
            batch.delete(gamesPlayersDocRef);
            // End Players

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }


        if (gameName == "APEX") {
            WriteBatch batch = fStore.batch();

            // MyProfile
            DocumentReference apexDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("apex");
            DocumentReference usersDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

            batch.update(usersDocRef, "usernames.apex", FieldValue.delete());
            batch.delete(apexDocRef);
            // End MyProfile

            // Players
            DocumentReference gamesDocRef = fStore.collection("games").document("apex");
            DocumentReference gamesPlayersDocRef = fStore.collection("games").document("apex").collection("players").document(globalUsername);

            batch.update(gamesDocRef, "players." + globalUsername, FieldValue.delete());
            batch.delete(gamesPlayersDocRef);
            // End Players

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    modelList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Log.d(TAG, gameName + " usunięte z pozycji: " + position);
                }
            });
        }

    }


    private void CreatePopupWindow(final int position) {
        final String gameName = modelList.get(position).getGame();

        dialogBuilder = new AlertDialog.Builder(this);
        final View windowPopupView = getLayoutInflater().inflate(R.layout.popup_window,null);

        popupWindowProfileImage = windowPopupView.findViewById(R.id.imageViewPopupWindowAvatar);

        popupWindowProfileUsername = windowPopupView.findViewById(R.id.textViewPopupWindowProfileUsername);
        popupWindowCountry = windowPopupView.findViewById(R.id.textViewPopupWindowCountry);
        popupWindowAge = windowPopupView.findViewById(R.id.textViewPopupWindowAge);

        popupWindowImageView = windowPopupView.findViewById(R.id.imageViewPopupWindow);

        popupWindowNick = windowPopupView.findViewById(R.id.textViewPopupWindowNick);
        popupWindowRank = windowPopupView.findViewById(R.id.textViewPopupWindowRank);
        popupWindowMic = windowPopupView.findViewById(R.id.textViewPopupWindowMic);
        popupWindowHours = windowPopupView.findViewById(R.id.textViewPopupWindowHours);
        popupWindowDesc = windowPopupView.findViewById(R.id.textViewPopupWindowDesc);

        PopupWindowBack = windowPopupView.findViewById(R.id.buttonPopupWindowBack);

        // Filling up profile data
        popupWindowProfileImage.setImageDrawable(ProfileImage.getDrawable());
        popupWindowProfileUsername.setText(username.getText());
        popupWindowCountry.setText(country.getText());
        popupWindowAge.setText(age.getText());


        // Filling up specific game data
        if (gameName == "CS:GO") {
            popupWindowImageView.setBackgroundResource(R.drawable.csgo);
            DocumentReference csgoDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("csgo");
            csgoDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document, gameName);
                    }
                    else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (gameName == "League of Legends") {
            popupWindowImageView.setBackgroundResource(R.drawable.lol);
            DocumentReference lolDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("lol");
            lolDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document, gameName);
                    }
                    else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (gameName == "Fortnite") {
            popupWindowImageView.setBackgroundResource(R.drawable.fortnite);
            DocumentReference fortniteDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("fortnite");
            fortniteDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document, gameName);
                    }
                    else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (gameName == "Among Us") {
            popupWindowImageView.setBackgroundResource(R.drawable.amongus);
            DocumentReference amongusDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("amongus");
            amongusDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document, gameName);
                    }
                    else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (gameName == "PUBG") {
            popupWindowImageView.setBackgroundResource(R.drawable.pubg);
            DocumentReference pubgDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("pubg");
            pubgDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document, gameName);
                    }
                    else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (gameName == "APEX") {
            popupWindowImageView.setBackgroundResource(R.drawable.apex);
            DocumentReference apexDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("games").document("apex");
            apexDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document, gameName);
                    }
                    else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }
        // end of game data

        dialogBuilder.setView(windowPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        PopupWindowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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


        final StorageReference profileRef = fStorage.child("users/" + globalUsername + "/profile.jpg");
        profileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Awatar został dodany");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).resize(250, 300).noFade().rotate(270).into(ProfileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Awatar nie został dodany");
            }
        });

    }

    private void DownloadProfileImage() {
        try {
            fStorage = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = fStorage.child("users/" + globalUsername + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).resize(250, 300).noFade().rotate(270).into(ProfileImage);
                }
            });
        } catch (Exception e) {}
    }

    private void InsertDocumentIntoPopupWindow(DocumentSnapshot document, String gameName) {
        // Filling up game data
        String platform = "";
        switch (gameName) {
            case "CS:GO" :
            case "PUBG" :
            case "APEX" :
                platform = getResources().getString(R.string.textViewPopupWindowNickSteam); break;
            case "League of Legends" :
                platform = getResources().getString(R.string.textViewPopupWindowNickRiotGames); break;
            case "Fortnite" :
                platform = getResources().getString(R.string.textViewPopupWindowNickEpicGames); break;
            case "Among Us" :
                platform = getResources().getString(R.string.textViewPopupWindowNickDiscord); break;
            default:
                platform = getResources().getString(R.string.textViewPopupWindowNick); break;
        }
        popupWindowNick.setText(platform + " " + document.getString("nick"));
        popupWindowRank.setText(getResources().getString(R.string.textViewPopupWindowRank) + " " + document.getString("rank"));
        popupWindowMic.setText(getResources().getString(R.string.textViewPopupWindowMic) + " " + document.getString("mic"));
        String hours = document.get("hours").toString();
        if (hours.contains("[") || hours.contains("]"))
            hours = hours.substring(hours.indexOf("[")+1, hours.indexOf("]"));
        popupWindowHours.setText(getResources().getString(R.string.textViewPopupWindowHours) + " " + hours);
        popupWindowDesc.setText(getResources().getString(R.string.textViewPopupWindowDesc) + " " + document.getString("desc"));
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Dashboard.class));
    }

}