package com.alpha.twostudents.socialmediaapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilePicActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Button uploadBtn;
    private Uri imageUri;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);
        imageView = findViewById(R.id.imageViewProfilePic);
        progressDialog = new ProgressDialog(ProfilePicActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        //    checkPermissions();
        uploadBtn = findViewById(R.id.button_add_profile_picture);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Getting image");
                progressDialog.show();
                //TODO Add the implementation for image insertion
                Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(getImage, 1);

            }
        });
    }


//    private void checkPermissions(){
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//            int permissionCheck = ProfilePicActivity.this.checkSelfPermission("Manifest.permissions.READ_EXTERNAL_STORAGE");
//            permissionCheck += ProfilePicActivity.this.checkSelfPermission("Manifest.permissions.WRITE_EXTERNAL_STORAGE");
//
//            if(permissionCheck != 0){
//                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},0 );
//            }
//            else{
//                Log.d(TAG,"No need to check permissions: SDK < lollipop");
//            }
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            imageUri = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(imageUri,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
            try {
                storageReference.child(firebaseAuth.getCurrentUser().getUid()).child("ProfilePic")
                        .putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        //jumping to the next activity
                        Intent intent = new Intent(ProfilePicActivity.this, ProfileActivity.class);
                        // set the new task and clear flags
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        finish();
                    }
                });
            } catch (NullPointerException e){
                Log.e("","User is null");
                Intent intent = new Intent(ProfilePicActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
      }
}
