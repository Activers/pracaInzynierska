package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Settings extends AppCompatActivity {

    LinearLayout Profile,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Profile =findViewById(R.id.ChangeProfile);
        Password = findViewById(R.id.ChangePassword);

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChangeProfileData.class));
            }
        });

        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetPassword.class));
            }
        });

    }


}