package com.bunny.ai.asyncai.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.models.ReadWriteUserDetails;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LoginSignUpActivity extends AppCompatActivity {

    LottieAnimationView login_animationView;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    SignInButton google_sign_in_button;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 20;
    boolean profileFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        login_animationView = findViewById(R.id.login_animationView);
        google_sign_in_button = findViewById(R.id.google_sign_in_button);
        progressBar = findViewById(R.id.progressBar);
        login_animationView.setVisibility(View.VISIBLE);

        authProfile = FirebaseAuth.getInstance();


        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                login_animationView.setAnimation("login_signup_dark.json");
                login_animationView.playAnimation();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                login_animationView.setAnimation("login_signup_light.json");
                login_animationView.playAnimation();
                break;
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginSignUpActivity.this, googleSignInOptions);

        google_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            progressBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthForGoogle(account.getIdToken(), account);
            } catch (Exception e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void firebaseAuthForGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authProfile.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = authProfile.getCurrentUser();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference referenceProfile = firebaseDatabase.getReference("users");
                    referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                                if(readWriteUserDetails != null){
                                    if(readWriteUserDetails.userId.contains(firebaseUser.getUid())){
                                        profileFound = true;
                                        Intent intent = new Intent(LoginSignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    }
                                }
                            }
                            if(!profileFound){
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                String textDateJoined = sdf1.format(new Date());
                                ReadWriteUserDetails writeUserDetails;
                                if(firebaseUser.getPhotoUrl() != null){
                                    writeUserDetails = new ReadWriteUserDetails(firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString(),
                                            textDateJoined, firebaseUser.getUid(), firebaseUser.getEmail());
                                } else {
                                    writeUserDetails = new ReadWriteUserDetails(firebaseUser.getDisplayName(), "n/a",
                                            textDateJoined, firebaseUser.getUid(), firebaseUser.getEmail());
                                }
                                firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(LoginSignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginSignUpActivity.this, "Some Error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}