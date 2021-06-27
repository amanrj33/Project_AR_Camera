package com.android.example.projectarcamera.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.android.example.projectarcamera.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //hooking variables with the views
        View flowerInner = findViewById(R.id.flower1);
        View flowerOuter = findViewById(R.id.flower2);
        View cameraCard = findViewById(R.id.cardView);

        //set animations to the views
        flowerInner.setAnimation(AnimationUtils.loadAnimation(this, R.anim.flower_anim_inner));
        flowerOuter.setAnimation(AnimationUtils.loadAnimation(this, R.anim.flower_anim_outer));
        cameraCard.setAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_anim));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        //start the main activity
                        startActivity(new Intent(this, CameraActivity.class));
                    else
                        //start the Phone authentication activity
                        startActivity(new Intent(this, PhoneAuthActivity.class));
                    finish();
                }
                , 4500);

    }
}