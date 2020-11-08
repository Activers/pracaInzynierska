package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference fStorage;
    CircleImageView ProfileImage;
    RecyclerView recyclerView;


    private ArrayList<Model> modelList;
    private RecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        fAuth = FirebaseAuth.getInstance();
        ProfileImage = findViewById(R.id.imageViewAvatar);
        recyclerView = findViewById(R.id.list);
        fStore = FirebaseFirestore.getInstance();

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


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        modelList = new ArrayList<>();

        ClearAll();

        GetDataFromFirebase();


    }

    private void GetDataFromFirebase() {

        ////tu skonczylem https://www.youtube.com/watch?v=BrDX6VTgTkg minuta 31:15

    }

    private void ClearAll(){
        if(modelList != null){
            modelList.clear();
        }

        modelList = new ArrayList<>();


    }


}