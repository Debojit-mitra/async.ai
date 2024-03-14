package com.bunny.ai.asyncai.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bunny.ai.asyncai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    Animation startAnimation;
    TextView app_name_textview;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    DatabaseReference newUserDetails;
    boolean userLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser != null) {
            userLoggedIn = true;
            newUserDetails = FirebaseDatabase.getInstance().getReference().child("users");
        }

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
           *//* splash_logo = findViewById(R.id.splash_logo);
            splash_logo.setVisibility(View.GONE);*//*
            SplashScreen splashScreen = SplashScreen.installSplashScreen(SplashActivity.this);
            splashScreen.setKeepOnScreenCondition(() -> false);
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                        splashScreenView,
                        "alpha",
                        1f,
                        0.0f);
                // fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setDuration(800);

                userCheck();

                // Call SplashScreenView.remove at the end of your custom animation.
                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                    }
                });
                // Run your animation.
                fadeOut.start();

            });
        } else {*/
            startAnimation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fadein);
            app_name_textview = findViewById(R.id.app_name_textview);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    app_name_textview.setVisibility(View.VISIBLE);
                    app_name_textview.startAnimation(startAnimation);
                }
            }, 300);

            userCheck();

      //  }
    }

    private void userCheck() {
        if (userLoggedIn) {
            newUserDetails.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                    Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
           gotoNewActivity();
        }
    }
    private void gotoNewActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, 800);
    }
}