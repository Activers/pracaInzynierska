package com.example.pracainzynierska;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    final String TAG = "Dashboard";

    Animation topAnim, bottomAnim;

    ImageButton FindPeople, GoProfile, Settings, About, Logout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
       // bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        FindPeople = findViewById(R.id.buttonFindPeople);
        GoProfile = findViewById(R.id.buttonGoProfile);
        Settings = findViewById(R.id.buttonSettings);
        About = findViewById(R.id.buttonAbout);
        Logout = findViewById(R.id.buttonLogout);

       // appName.setAnimation(topAnim);
        //FindPeople.setAnimation(bottomAnim);
        //GoProfile.setAnimation(bottomAnim);
       // Settings.setAnimation(bottomAnim);
        //Logout.setAnimation(bottomAnim);


        FindPeople.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        startActivity(new Intent(getApplicationContext(),FindPeople.class));

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        GoProfile.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        startActivity(new Intent(getApplicationContext(),MyProfile.class));

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        Settings.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        startActivity(new Intent(getApplicationContext(), com.example.pracainzynierska.Settings.class));

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        About.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        startActivity(new Intent(getApplicationContext(), com.example.pracainzynierska.About.class));

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        Logout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        String AUTO_LOGIN_PREF_NAME = getString(R.string.autoLoginPreferenceName); // nazwa preferencji / pliku gdzie skladowane beda klucz-wartosc
                        SharedPreferences preferences = getSharedPreferences(AUTO_LOGIN_PREF_NAME, MODE_PRIVATE);
                        preferences.edit().clear().apply(); //usuwa autologowanie po wylogowaniu sie
                        Log.i(TAG, "Usunieto autologowanie");
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

    }

    boolean singleBack = false;

    @Override
    public void onBackPressed() {
        if (singleBack) {
            finishAffinity();
            return;
        }

        this.singleBack = true;
        Toast.makeText(this, "Naciśnij ponownie WSTECZ, aby wyjść", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                singleBack=false;
            }
        }, 2000);
    }
}