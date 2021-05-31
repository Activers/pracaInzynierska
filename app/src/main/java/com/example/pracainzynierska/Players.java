package com.example.pracainzynierska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Players extends AppCompatActivity {

    final String TAG = "Players";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference fStorage;

    DocumentReference playersDocRef;

    RecyclerView recyclerViewPlayers;
    LinearLayoutManager layoutManager; // or RecyclerView.LayoutManager

    private ArrayList<Model> modelListPlayers;
    private RecyclerAdapterPlayers recyclerAdapterPlayers; // or RecyclerView.Adapter (<-- to jest domyslne... a RecyclerAdapterPlayers to nasza klasa z dodanymi customowymi metodami)


    RelativeLayout relativeLayoutFilter;

    ImageView imageViewGame;

    String game; // jaka gre wybral uzytkownik - sciagane z intentu

    // Popup Window
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    CircleImageView popupWindowProfileImage;
    TextView popupWindowProfileUsername, popupWindowCountry, popupWindowAge, popupWindowNick, popupWindowRank, popupWindowMic, popupWindowHours, popupWindowDesc;
    ImageView popupWindowImageView;
    Button PopupWindowBack;
    // End of Popup Window

    // Filter Popup Window
    Button FilterPopupWindowSubmit, FilterPopupWindowPrefHoursMorning, FilterPopupWindowPrefHoursAfternoon, FilterPopupWindowPrefHoursEvening, FilterPopupWindowPrefHoursNight;
    EditText FilterPopupWindowAge;
    Spinner FilterPopupWindowCountry, FilterPopupWindowRank, FilterPopupWindowMic;

    boolean prefMorning;
    boolean prefAfternoon;
    boolean prefEvening;
    boolean prefNight;
    // End of Filter Popup Window



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance().getReference();

        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers);

        relativeLayoutFilter = findViewById(R.id.relativeLayoutPlayersFilter);
        imageViewGame = findViewById(R.id.imageViewPlayersGame);

        game = getIntent().getStringExtra("game");

        if (game.equals("csgo")) {
            imageViewGame.setBackgroundResource(R.drawable.csgo);
            playersDocRef = fStore.collection("games").document("csgo");
        }

        if (game.equals("lol")) {
            imageViewGame.setBackgroundResource(R.drawable.lol);
            playersDocRef = fStore.collection("games").document("lol");
        }

        if (game.equals("fortnite")) {
            imageViewGame.setBackgroundResource(R.drawable.fortnite);
            playersDocRef = fStore.collection("games").document("fortnite");
        }

        if (game.equals("amongus")) {
            imageViewGame.setBackgroundResource(R.drawable.amongus);
            playersDocRef = fStore.collection("games").document("amongus");
        }

        if (game.equals("pubg")) {
            imageViewGame.setBackgroundResource(R.drawable.pubg);
            playersDocRef = fStore.collection("games").document("pubg");
        }

        if (game.equals("apex")) {
            imageViewGame.setBackgroundResource(R.drawable.apex);
            playersDocRef = fStore.collection("games").document("apex");
        }

        relativeLayoutFilter.setOnClickListener(new View.OnClickListener() { // filtr
            @Override
            public void onClick(View view) {
                CreateFilterPopupWindow();
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerViewPlayers.setHasFixedSize(false);
        recyclerViewPlayers.setLayoutManager(layoutManager);

        modelListPlayers = new ArrayList<>();

         ClearAll(); // nie wiem czy jest sens dodawac - w MyProfile niby jest ale to nie robi roznicy

        recyclerAdapterPlayers = new RecyclerAdapterPlayers(getApplicationContext(), modelListPlayers); // musi byc zadeklarowane przed sciagnieciem danych z bazy przez to ze lekko zamula
        recyclerViewPlayers.setAdapter(recyclerAdapterPlayers);

        GetPlayersDataFromFirebase();

        try {
            recyclerAdapterPlayers.setOnItemClickListener(new RecyclerAdapterPlayers.OnItemClickedListener() {
                @Override
                public void onItemClick(int position) {
                    CreatePopupWindow(position);
                }
            });
        }
        catch (Exception e) { Log.d(TAG, "RECYCLER LISTENER ERROR: " + e); }

    }

    private void GetPlayersDataFromFirebase() {
        playersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Map<String,Map<String,String>> playersMap = (Map<String, Map<String,String>>) document.get("players");

                    String[] playersUsernameArray = playersMap.keySet().toArray(new String[0]);

                    Map<String,String>[] playersMapArray = playersMap.values().toArray(new Map[0]); // po konwersji na tablice dane sa wkladane odwrotnie niz jest w bazie

                    if (playersUsernameArray.length == playersMapArray.length) {
                        if (playersMapArray != null) {

                            for (int i = 0; i < playersMapArray.length; i++) {
                                modelListPlayers.add(new Model(playersUsernameArray[i], playersMapArray[i].get("nick"), playersMapArray[i].get("rank"), playersMapArray[i].get("mic")));
                            }
                            Collections.shuffle(modelListPlayers);
                        }
                    }


                    recyclerViewPlayers.setAdapter(recyclerAdapterPlayers); // wlozenie listy do recyclerView
                }
            }
        });


    }

    private void CreatePopupWindow(final int position) {

        dialogBuilder = new AlertDialog.Builder(this);
        final View windowPopupView = getLayoutInflater().inflate(R.layout.popup_window, null);

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

        String username = modelListPlayers.get(position).getUsername();

        try {
            fStorage = FirebaseStorage.getInstance().getReference();
            //StorageReference profileRef = fStorage.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
            StorageReference profileRef = fStorage.child("users/" + username + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).resize(250, 300).noFade().rotate(270).into(popupWindowProfileImage);
                }
            });
        } catch (Exception e) {}


        if (game.equals("csgo")) {
            popupWindowImageView.setBackgroundResource(R.drawable.csgo);
            DocumentReference csgoPlayersDocRef = fStore.collection("games").document("csgo").collection("players").document(username);
            csgoPlayersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document);
                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (game.equals("lol")) {
            popupWindowImageView.setBackgroundResource(R.drawable.lol);
            DocumentReference lolPlayersDocRef = fStore.collection("games").document("lol").collection("players").document(username);
            lolPlayersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document);
                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (game.equals("fortnite")) {
            popupWindowImageView.setBackgroundResource(R.drawable.fortnite);
            DocumentReference fortnitePlayersDocRef = fStore.collection("games").document("fortnite").collection("players").document(username);
            fortnitePlayersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document);
                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (game.equals("amongus")) {
            popupWindowImageView.setBackgroundResource(R.drawable.amongus);
            DocumentReference amongusPlayersDocRef = fStore.collection("games").document("amongus").collection("players").document(username);
            amongusPlayersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document);
                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (game.equals("pubg")) {
            popupWindowImageView.setBackgroundResource(R.drawable.pubg);
            DocumentReference pubgPlayersDocRef = fStore.collection("games").document("pubg").collection("players").document(username);
            pubgPlayersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document);
                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }

        if (game.equals("apex")) {
            popupWindowImageView.setBackgroundResource(R.drawable.apex);
            DocumentReference apexPlayersDocRef = fStore.collection("games").document("apex").collection("players").document(username);
            apexPlayersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        InsertDocumentIntoPopupWindow(document);
                    } else {
                        Log.i(TAG, "Document onComplete failure - Niepowodzenie spowodowane: ", task.getException());
                    }
                }
            });
        }
        // koniec uzupelniania danych

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

    private void InsertDocumentIntoPopupWindow(DocumentSnapshot document) { // precz kodzie spaghetti!
        // uzupelnianie danych profilowych
        popupWindowProfileUsername.setText(getResources().getString(R.string.textViewProfileUsername) + " " + document.getString("username"));
        popupWindowCountry.setText(getResources().getString(R.string.textViewCountry) + " " + document.getString("country"));
        popupWindowAge.setText(getResources().getString(R.string.textViewAge) + " " + document.getString("age"));

        // uzupelnianie danych gry
        popupWindowNick.setText(getResources().getString(R.string.textViewPopupWindowNick) + " " + document.getString("nick"));
        popupWindowRank.setText(getResources().getString(R.string.textViewPopupWindowRank) + " " + document.getString("rank"));
        popupWindowMic.setText(getResources().getString(R.string.textViewPopupWindowMic) + " " + document.getString("mic"));
        String hours = document.get("hours").toString();
        if (hours.contains("[") || hours.contains("]"))
            hours = hours.substring(hours.indexOf("[")+1, hours.indexOf("]"));
        popupWindowHours.setText(getResources().getString(R.string.textViewPopupWindowHours) + " " + hours);
        popupWindowDesc.setText(getResources().getString(R.string.textViewPopupWindowDesc) + " " + document.getString("desc"));
    }


    private void CreateFilterPopupWindow() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View windowPopupView = getLayoutInflater().inflate(R.layout.filter_popup_window, null);

        FilterPopupWindowCountry = windowPopupView.findViewById(R.id.spinnerFilterPopupWindowCountry);
        FilterPopupWindowAge = windowPopupView.findViewById(R.id.editTextFilterPopupWindowAge);
        FilterPopupWindowRank = windowPopupView.findViewById(R.id.spinnerFilterPopupWindowRank);
        FilterPopupWindowMic = windowPopupView.findViewById(R.id.spinnerFilterPopupWindowMic);

        FilterPopupWindowPrefHoursMorning = windowPopupView.findViewById(R.id.buttonFilterPopupWindowPrefHoursMorning);
        FilterPopupWindowPrefHoursAfternoon = windowPopupView.findViewById(R.id.buttonFilterPopupWindowPrefHoursAfternoon);
        FilterPopupWindowPrefHoursEvening = windowPopupView.findViewById(R.id.buttonFilterPopupWindowPrefHoursEvening);
        FilterPopupWindowPrefHoursNight = windowPopupView.findViewById(R.id.buttonFilterPopupWindowPrefHoursNight);

        FilterPopupWindowSubmit = windowPopupView.findViewById(R.id.buttonFilterPopupWindowSubmit);

        prefMorning = false;
        prefAfternoon = false;
        prefEvening = false;
        prefNight = false;

        // Country
        ArrayAdapter adapterCountries =  ArrayAdapter.createFromResource(this,R.array.ArrayFilterCountries,R.layout.spinner_item_filter);
        adapterCountries.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FilterPopupWindowCountry.setAdapter(adapterCountries);

        // Ranks
        ArrayAdapter adapterRanks;
        switch (game) {
            case "csgo":
                adapterRanks = ArrayAdapter.createFromResource(this, R.array.ArrayFilterCsgoRanks, R.layout.spinner_item_filter);
                adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
                FilterPopupWindowRank.setAdapter(adapterRanks); break;

            case "lol":
                adapterRanks = ArrayAdapter.createFromResource(this, R.array.ArrayFilterLolRanks, R.layout.spinner_item_filter);
                adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
                FilterPopupWindowRank.setAdapter(adapterRanks); break;

            case "fortnite": // identyczna arrayka dla fortnite i amongus
            case "amongus":
                adapterRanks = ArrayAdapter.createFromResource(this, R.array.ArrayFilterFortniteRanks, R.layout.spinner_item_filter);
                adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
                FilterPopupWindowRank.setAdapter(adapterRanks); break;

            case "pubg":
                adapterRanks = ArrayAdapter.createFromResource(this, R.array.ArrayFilterPubgRanks, R.layout.spinner_item_filter);
                adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
                FilterPopupWindowRank.setAdapter(adapterRanks); break;

            case "apex":
                adapterRanks = ArrayAdapter.createFromResource(this, R.array.ArrayFilterApexRanks, R.layout.spinner_item_filter);
                adapterRanks.setDropDownViewResource(R.layout.spinner_dropdown_item);
                FilterPopupWindowRank.setAdapter(adapterRanks);break;

        }

        // Mic
        ArrayAdapter adapterUseMic =  ArrayAdapter.createFromResource(this,R.array.ArrayFilterUseMic,R.layout.spinner_item_filter);
        adapterUseMic.setDropDownViewResource(R.layout.spinner_dropdown_item);
        FilterPopupWindowMic.setAdapter(adapterUseMic);

        // Hours
        //Pref Hours
        FilterPopupWindowPrefHoursMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefMorning) {
                    prefMorning = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefMorning = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });

        FilterPopupWindowPrefHoursAfternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefAfternoon) {
                    prefAfternoon = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefAfternoon = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });

        FilterPopupWindowPrefHoursEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefEvening) {
                    prefEvening = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefEvening = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });

        FilterPopupWindowPrefHoursNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefNight) {
                    prefNight = false;
                    view.setBackgroundResource(R.drawable.button_bg);
                } else {
                    prefNight = true;
                    view.setBackgroundResource(R.drawable.button_green_bg);
                }
            }
        });
        // End of Pref Hours




        dialogBuilder.setView(windowPopupView);
        dialog = dialogBuilder.create();
        dialog.show();


        FilterPopupWindowSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionReference playersDocRef = fStore.collection("games").document(game).collection("players");

                int filterCount = 0;
                String[] filtersKeys = new String[5];
                String[] filtersValues = new String[5];
                ArrayList<String> hoursValuesList = new ArrayList<>();

                if (FilterPopupWindowCountry.getSelectedItemId() > 0) {
                    filtersKeys[filterCount] = "country";
                    filtersValues[filterCount] = FilterPopupWindowCountry.getSelectedItem().toString();
                    filterCount++;
                }

                if (!TextUtils.isEmpty(FilterPopupWindowAge.getText())) {
                    filtersKeys[filterCount] = "age";
                    filtersValues[filterCount] = FilterPopupWindowAge.getText().toString();
                    filterCount++;
                }

                if (FilterPopupWindowRank.getSelectedItemId() > 0) {
                    filtersKeys[filterCount] = "rank";
                    filtersValues[filterCount] = FilterPopupWindowRank.getSelectedItem().toString();
                    filterCount++;
                }

                if (FilterPopupWindowMic.getSelectedItemId() > 0) {
                    filtersKeys[filterCount] = "mic";
                    filtersValues[filterCount] = FilterPopupWindowMic.getSelectedItem().toString();
                    filterCount++;
                }

                Query query = null;
                if (prefMorning || prefAfternoon || prefEvening || prefNight) {
                    ArrayList<String> prefHoursArrayList = new ArrayList<>();
                    if (prefMorning) prefHoursArrayList.add("Rano");
                    if (prefAfternoon) prefHoursArrayList.add("Po południu");
                    if (prefEvening) prefHoursArrayList.add("Wieczorem");
                    if (prefNight) prefHoursArrayList.add("W nocy");
                    filterCount++;
                    //Toast.makeText(Players.this, "Wchodze do query", Toast.LENGTH_SHORT).show();

                    switch (filterCount) { // ile zastosowac filtrow (z hours)
                        case 1:
                            query = playersDocRef.whereArrayContainsAny("hours", prefHoursArrayList);
                            break;
                        case 2:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereArrayContainsAny("hours", prefHoursArrayList);
                            break;
                        case 3:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereEqualTo(filtersKeys[1], filtersValues[1]).whereArrayContainsAny("hours", prefHoursArrayList);
                            break;
                        case 4:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereEqualTo(filtersKeys[1], filtersValues[1]).whereEqualTo(filtersKeys[2], filtersValues[2]).whereArrayContainsAny("hours", prefHoursArrayList);
                            break;
                        case 5:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereEqualTo(filtersKeys[1], filtersValues[1]).whereEqualTo(filtersKeys[2], filtersValues[2]).whereEqualTo(filtersKeys[3], filtersValues[3]).whereArrayContainsAny("hours", prefHoursArrayList);
                            break;
                    }
                } else {
                    switch (filterCount) { // ile zastosowac filtrow (bez hours)
                        case 0:
                            query = playersDocRef;
                            break;
                        case 1:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]);
                            break;
                        case 2:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereEqualTo(filtersKeys[1], filtersValues[1]);
                            break;
                        case 3:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereEqualTo(filtersKeys[1], filtersValues[1]).whereEqualTo(filtersKeys[2], filtersValues[2]);
                            break;
                        case 4:
                            query = playersDocRef.whereEqualTo(filtersKeys[0], filtersValues[0]).whereEqualTo(filtersKeys[1], filtersValues[1]).whereEqualTo(filtersKeys[2], filtersValues[2]).whereEqualTo(filtersKeys[3], filtersValues[3]);
                            break;
                    }
                }
                if (query != null) {
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //ClearAll();
                                modelListPlayers.clear();
                                QuerySnapshot query = task.getResult();
                                List<DocumentSnapshot> documentList = query.getDocuments();
                                Toast.makeText(Players.this, "Ilość graczy: " + documentList.size(), Toast.LENGTH_SHORT).show();

                                for (int i = 0; i < documentList.size(); i++) {

                                    DocumentSnapshot document = documentList.get(i);

                                    Map<String, Object> playerDataMap = (Map<String, Object>) document.getData();
                                    String playerUsername = (String) playerDataMap.get("username");
                                    //String playerCountry = (String) playerDataMap.get("country");
                                    //String playerAge = (String) playerDataMap.get("age");

                                    String playerNick = (String) playerDataMap.get("nick");
                                    String playerMic = (String) playerDataMap.get("mic");
                                    //ArrayList<String> playerHours = (ArrayList<String>) playerDataMap.get("hours");
                                    String playerRank = (String) playerDataMap.get("rank");
                                    modelListPlayers.add(new Model(playerUsername, playerNick, playerRank, playerMic));
                                }

                                Collections.shuffle(modelListPlayers);

                                recyclerViewPlayers.setAdapter(recyclerAdapterPlayers); // wlozenie listy do recyclerView
                            }
                        }
                    });
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(Players.this, "Nie można przefiltrować graczy", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ClearAll(){
        if(modelListPlayers != null){
            modelListPlayers.clear();
            if (recyclerAdapterPlayers != null) {
                recyclerAdapterPlayers.notifyDataSetChanged();
            }
        }
        modelListPlayers = new ArrayList<>();
    }

}