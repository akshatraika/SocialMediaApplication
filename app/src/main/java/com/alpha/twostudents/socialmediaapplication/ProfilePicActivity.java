package com.alpha.twostudents.socialmediaapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ProfilePicActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Button uploadBtn;
    private Uri imageUri;
    private ImageView imageView;

    public ProfilePicActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_pic);
    imageView = findViewById(R.id.imageViewProfilePic);

    firebaseAuth = FirebaseAuth.getInstance();
     storageReference = FirebaseStorage.getInstance().getReference();
    uploadBtn = findViewById(R.id.button_add_profile_picture);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //TODO Add the implementation for image insertion
            Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            //startActivityForResult(getImage, 1);

            finish();
//jumping to the next activity
                Intent intent = new Intent(ProfilePicActivity.this, ProfileActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
    if (resultCode == RESULT_OK){
        imageUri = Data.getData();
        storageReference.child(firebaseAuth.getCurrentUser().getUid()).child("ProfilePic").putFile(imageUri);
        imageView.setImageURI(imageUri);



    }

    }



}


