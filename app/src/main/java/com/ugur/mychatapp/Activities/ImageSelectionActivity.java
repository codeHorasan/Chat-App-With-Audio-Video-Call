package com.ugur.mychatapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ugur.mychatapp.Classes.UploadImage;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.R;

public class ImageSelectionActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        imageView = findViewById(R.id.userImageView);
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    public void pressedContinue(View view) {
        User user = User.getInstance();
        if (selectedImageUri != null) {
            user.setImageUri(selectedImageUri);
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfileP").child(user.getUuid() + ".");
        if (user.getImageUri() != null) {
            storageReference.putFile(user.getImageUri()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        UploadImage uploadImage = new UploadImage(User.getInstance().getUuid(), downloadUri.toString());
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference();
                        reference.child("Users").child(User.getInstance().getUuid()).child("PhotoUri").setValue(uploadImage.getImageUri());
                    }
                }
            });

            StyleableToast.makeText(getApplicationContext(),"Photo selected successfully!", R.style.SuccessToast).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }
}