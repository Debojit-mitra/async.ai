package com.bunny.ai.asyncai.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.ai.asyncai.R;
import com.bunny.ai.asyncai.models.ReadWriteUserDetails;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    ImageView update_profile_image;
    EditText edittext_name_editProfile, edittext_email_editProfile;
    ImageButton back_btn, delete_profile_btn;
    CircularProgressButton btn_update_profile;
    TextView remove_profile_picture, textView_joinedOn_editProfile, logout_btn;
    private String fullName, profileImage, dateJ, email;
    Uri uriImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    RelativeLayout btn_relative_layout;
    String TagImage = "oldImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        update_profile_image = findViewById(R.id.update_profile_image);
        edittext_name_editProfile = findViewById(R.id.edittext_name_editProfile);
        edittext_email_editProfile = findViewById(R.id.edittext_email_editProfile);
        textView_joinedOn_editProfile = findViewById(R.id.textView_joinedOn_editProfile);
        btn_relative_layout = findViewById(R.id.btn_relative_layout);
        back_btn = findViewById(R.id.back_btn);
        logout_btn = findViewById(R.id.logout_btn);
        delete_profile_btn = findViewById(R.id.delete_profile_btn);
        remove_profile_picture = findViewById(R.id.remove_profile_picture);
        btn_update_profile = findViewById(R.id.btn_update_profile);

        showProfile();

        Animation startAnimation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.bottom_up);
        btn_relative_layout.setVisibility(View.VISIBLE);
        btn_relative_layout.startAnimation(startAnimation);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChoser();
                if (edittext_name_editProfile.hasFocus()) {
                    edittext_name_editProfile.clearFocus();
                }
            }
        });


        remove_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder removeProfilePicture = new AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                View view1 = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                Button ok_button, cancel_button;
                TextView custom_textview;
                LottieAnimationView custom_animationView;
                ok_button = view1.findViewById(R.id.ok_button);
                cancel_button = view1.findViewById(R.id.cancel_button);
                ProgressBar progressBar = view1.findViewById(R.id.progressBar);
                ok_button.setText(R.string.confirm);
                ok_button.setTextColor(getResources().getColor(R.color.red));
                cancel_button.setText(R.string.cancel);
                custom_animationView = view1.findViewById(R.id.custom_animationView);
                custom_textview = view1.findViewById(R.id.custom_textview);
                custom_animationView.setAnimation("red_warning.json");
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                String custom_text = "Please confirm, You want to remove your email?";
                custom_textview.setText(custom_text);
                ok_button.setVisibility(View.VISIBLE);
                cancel_button.setVisibility(View.VISIBLE);
                custom_textview.setVisibility(View.VISIBLE);
                int width = 400;
                int height = 400;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.setMargins(10, 10, 10, 10);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);
                removeProfilePicture.setView(view1);
                AlertDialog alertDialog = removeProfilePicture.create();
                alertDialog.setCanceledOnTouchOutside(false);
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
                        referenceProfile.child(firebaseUser.getUid()).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String imageUrl = snapshot.getValue(String.class);
                                if (imageUrl != null) {
                                    if (imageUrl.equals("n/a")) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(EditProfileActivity.this, "Bro! set a profile picture to remove it!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    } else {
                                        referenceProfile.child(firebaseUser.getUid()).child("profileImage").setValue("n/a").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(EditProfileActivity.this, "Profile picture has been removed!", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        alertDialog.dismiss();
                                                        showProfile();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(EditProfileActivity.this, "Failed to remove profile picture!", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        alertDialog.dismiss();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditProfileActivity.this, "Failed to remove profile picture!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                alertDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setWindowAnimations(R.style.RoundedCornersDialog);
                }
                alertDialog.show();

            }
        });

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name;

                name = edittext_name_editProfile.getText().toString().trim();

                if (!fullName.equals(name) || !TagImage.equals("oldImage")) {

                    if (!name.equals("")) {
                        btn_update_profile.startAnimation();
                        updateProfile(name);

                    } else {
                        Toast.makeText(EditProfileActivity.this, "Enter name to save details!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Edit something to update!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog box for delete profile
                AlertDialog.Builder delete = new AlertDialog.Builder(EditProfileActivity.this, R.style.RoundedCornersDialog);
                View viewDeleteProfile = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.custom_alert_dialog, null);
                Button ok_button = viewDeleteProfile.findViewById(R.id.ok_button);
                Button cancel_button = viewDeleteProfile.findViewById(R.id.cancel_button);
                ProgressBar progressBar = viewDeleteProfile.findViewById(R.id.progressBar);
                cancel_button.setVisibility(View.VISIBLE);
                ok_button.setVisibility(View.VISIBLE);
                LottieAnimationView custom_animationView = viewDeleteProfile.findViewById(R.id.custom_animationView);
                custom_animationView.setAnimation("red_warning.json");
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                int width = 800;
                int height = 600;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);

                TextView custom_textview = viewDeleteProfile.findViewById(R.id.custom_textview);
                String custom_text = getString(R.string.data_will_be_removed);
                custom_textview.setText(custom_text);
                custom_textview.setVisibility(View.VISIBLE);

                delete.setView(viewDeleteProfile);
                AlertDialog alertDialog = delete.create();
                alertDialog.setCanceledOnTouchOutside(false);
                ok_button.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.red));
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                            if (firebaseUser != null) {
                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            authProfile.signOut();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Bro! Check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setWindowAnimations(R.style.RoundedCornersDialog);
                }
                alertDialog.show();

            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                if(authProfile.getCurrentUser() == null){
                    Toast.makeText(EditProfileActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, LoginSignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });


    }

    private void updateProfile(String name) {

        String textFullName, textDateJoined;
        textFullName = name;
        textDateJoined = dateJ;

        if (TagImage.equals("newImage") && (uriImage != null)) {

            firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference(firebaseUser.getUid()).child("ProfilePics");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            StorageReference fileReference = storageReference.child(currentDateandTime + ".jpg");
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String textProfileImage = uri.toString();
                            String textUserId = firebaseUser.getUid();
                            String textEmail = email;
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textProfileImage, textDateJoined, textUserId, textEmail);
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                            referenceProfile.setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Bitmap correctBitmap = getBitmap(R.drawable.ic_correct);
                                        btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.green), correctBitmap);
                                        Toast.makeText(EditProfileActivity.this, "Profile successfully updated!", Toast.LENGTH_SHORT).show();
                                        btn_update_profile.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showProfile();
                                                btn_update_profile.revertAnimation();
                                                btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                                            }
                                        }, 800);
                                    } else {
                                        Bitmap correctBitmap = getBitmap(R.drawable.ic_clear);
                                        btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.red), correctBitmap);
                                        Toast.makeText(EditProfileActivity.this, "Profile update failed!", Toast.LENGTH_SHORT).show();
                                        btn_update_profile.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                btn_update_profile.revertAnimation();
                                                btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                                            }
                                        }, 800);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Bitmap correctBitmap = getBitmap(R.drawable.ic_clear);
                                    btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.red), correctBitmap);
                                    Toast.makeText(EditProfileActivity.this, "Profile update failed!", Toast.LENGTH_SHORT).show();
                                    if (e.getMessage() != null) {
                                        Log.e("updateProfileError", e.getMessage());
                                    }
                                    btn_update_profile.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            btn_update_profile.revertAnimation();
                                            btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                                        }
                                    }, 800);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap correctBitmap = getBitmap(R.drawable.ic_clear);
                            btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.red), correctBitmap);
                            Toast.makeText(EditProfileActivity.this, "Profile update failed!", Toast.LENGTH_SHORT).show();
                            if(e.getMessage() != null){
                                Log.e("updateProfileError", e.getMessage());
                            }
                            btn_update_profile.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_update_profile.revertAnimation();
                                    btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                                }
                            }, 800);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Bitmap correctBitmap = getBitmap(R.drawable.ic_clear);
                    btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.red), correctBitmap);
                    Toast.makeText(EditProfileActivity.this, "Profile update failed!", Toast.LENGTH_SHORT).show();
                    if(e.getMessage() != null){
                        Log.e("updateProfileError", e.getMessage());
                    }
                    btn_update_profile.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_update_profile.revertAnimation();
                            btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                        }
                    }, 800);
                }
            });


        } else {

            String textProfileImage = profileImage;
            String textUserId = firebaseUser.getUid();
            String textEmail = email;
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textProfileImage, textDateJoined, textUserId, textEmail);
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            referenceProfile.setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Bitmap correctBitmap = getBitmap(R.drawable.ic_correct);
                        btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.green), correctBitmap);
                        Toast.makeText(EditProfileActivity.this, "Profile successfully updated!", Toast.LENGTH_SHORT).show();
                        btn_update_profile.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showProfile();
                                btn_update_profile.revertAnimation();
                                btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                            }
                        }, 800);
                    } else {
                        Bitmap correctBitmap = getBitmap(R.drawable.ic_clear);
                        btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.red), correctBitmap);
                        Toast.makeText(EditProfileActivity.this, "Profile update failed!", Toast.LENGTH_SHORT).show();
                        btn_update_profile.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btn_update_profile.revertAnimation();
                                btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                            }
                        }, 800);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Bitmap correctBitmap = getBitmap(R.drawable.ic_clear);
                    btn_update_profile.doneLoadingAnimation(ContextCompat.getColor(EditProfileActivity.this, R.color.red), correctBitmap);
                    Toast.makeText(EditProfileActivity.this, "Profile update failed!", Toast.LENGTH_SHORT).show();
                    if (e.getMessage() != null) {
                        Log.e("updateProfileError", e.getMessage());
                    }
                    btn_update_profile.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_update_profile.revertAnimation();
                            btn_update_profile.setBackground(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.button_fullround_card));
                        }
                    }, 800);
                }
            });

        }


    }

    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE_REQUEST);
    }

    private Bitmap getBitmap(int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = ContextCompat.getDrawable(EditProfileActivity.this, drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            uriImage = data.getData();
            TagImage = "newImage";
            update_profile_image.setImageURI(uriImage);
        }
    }

    private void showProfile() {

        edittext_email_editProfile.setEnabled(false);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
        referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {

                    fullName = readWriteUserDetails.fullName;
                    profileImage = readWriteUserDetails.profileImage;
                    dateJ = readWriteUserDetails.dateJoined;
                    email = readWriteUserDetails.email;


                    String joinedOn = "Joined on " + dateJ;
                    textView_joinedOn_editProfile.setText(joinedOn);

                    if (!profileImage.equals("n/a")) {
                        Glide.with(EditProfileActivity.this).load(profileImage)
                                .placeholder(R.drawable.ic_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(update_profile_image);
                    }

                    edittext_email_editProfile.setText(email);


                    edittext_name_editProfile.setText(fullName);
                    referenceProfile.keepSynced(true);


                } else {
                    Toast.makeText(EditProfileActivity.this, "Error getting user info!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Error getting user info!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (firebaseUser != null) {
            showProfile();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}