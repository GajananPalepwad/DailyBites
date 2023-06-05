package com.gn4k.dailybites;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


import io.grpc.Compressor;

public class ProfilePageForMess extends AppCompatActivity {


    Uri filepath;
    Bitmap bitmap;
    SharedPreferences sharedPreferences;

    Button uploadDoc, updatePDbtn, updateLocation;
    EditText  addharNo;
    EditText  bankHolderName;
    EditText  ifsc;
    EditText  accountNo;
    EditText  bankName;

    EditText ownerName, messName, number;

    CardView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_for_mess);

        CardView imagePicker = findViewById(R.id.addImg);

        uploadDoc = findViewById(R.id.uploadDoc);
        addharNo = findViewById(R.id.addhar);
        bankHolderName = findViewById(R.id.AHname);
        ifsc = findViewById(R.id.ifsc);
        accountNo = findViewById(R.id.BAnumber);
        bankName = findViewById(R.id.bankName);
        ownerName = findViewById(R.id.ownerName);
        messName = findViewById(R.id.messName);
        number = findViewById(R.id.number);

        updatePDbtn = findViewById(R.id.uploadpd);
        updateLocation = findViewById(R.id.updatelocation);
        back = findViewById(R.id.back);

        uploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankVerify();
            }
        });

        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePageForMess.this, MapToLocateMess.class);
                startActivity(intent);
            }
        });

        updatePDbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePD();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


                sharedPreferences = getSharedPreferences("MessOwnerData", MODE_PRIVATE);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            filepath = data.getData();
                        } else {
                            Toast.makeText(ProfilePageForMess.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image picker
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateAccordingtofirebase();

    }


    private void updateAccordingtofirebase(){

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    ownerName.setText((String) data.get("ownerName"));
                    messName.setText(String.valueOf(data.get("messName")));
                    number.setText(String.valueOf(data.get("mobileNo")));
                    bankName.setText((String) data.get("bankName"));
                    bankHolderName.setText(String.valueOf(data.get("bankHolderName")));
                    accountNo.setText(String.valueOf(data.get("accountNo")));
                    addharNo.setText(String.valueOf(data.get("addharNo")));
                    ifsc.setText(String.valueOf(data.get("ifsc")));
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            filepath=data.getData();
            try
            {
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
            }catch (Exception ex)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        uploadtofirebase();
    }



    private void uploadtofirebase() {

        if (filepath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            // Get a reference to the Firebase Storage location
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Get the file extension
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(filepath));

            // Create a reference to the image file to be uploaded

            String randomString = "CoverImage/"+sharedPreferences.getString("MessOwnerMobileNo", "");

            StorageReference imageRef = storageRef.child(randomString);

            // Upload the image file to Firebase Storage
            UploadTask uploadTask = imageRef.putFile(filepath);

            // Add an OnSuccessListener to get the download URL after the upload completes
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL of the uploaded image

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // Save the download URL to the Realtime Database
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                            databaseRef.child("mess").child(sharedPreferences.getString("MessOwnerMobileNo", "")).child("coverImage").setValue(uri.toString());

                            progressDialog.dismiss();
//                            Toast.makeText(ProfilePageForMess.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            ImageView sign = findViewById(R.id.sign);
                            sign.setImageResource(R.drawable.correct);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfilePageForMess.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            });
        }
    }

    private void updatePD(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").child(sharedPreferences.getString("MessOwnerMobileNo", ""));


        Map<String, Object> data = new HashMap<>();

        data.put("messName", messName.getText().toString());
        data.put("ownerName", ownerName.getText().toString());
        dataRef.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data saved successfully
                        Toast.makeText(ProfilePageForMess.this, "SUCCESSFULLY ADDED", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while saving data
                        Toast.makeText(ProfilePageForMess.this, "Something went wrong", Toast.LENGTH_SHORT).show();                                    }
                });

    }

    private void bankVerify(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").child(sharedPreferences.getString("MessOwnerMobileNo", ""));


        Map<String, Object> data = new HashMap<>();

        data.put("addharNo", addharNo.getText().toString());
        data.put("bankHolderName", bankHolderName.getText().toString());
        data.put("ifsc", ifsc.getText().toString());
        data.put("accountNo", accountNo.getText().toString());
        data.put("bankName", bankName.getText().toString());

        dataRef.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data saved successfully
                        Toast.makeText(ProfilePageForMess.this, "SUCCESSFULLY ADDED", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while saving data
                        Toast.makeText(ProfilePageForMess.this, "Something went wrong", Toast.LENGTH_SHORT).show();                                    }
                });

    }

}