package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    Button btnSignOut;
    CircleImageView profile_pic;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    TextView username,fullName,phoneNumber,cmnd;


    StorageReference storageReference;
    private static final  int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        username=findViewById(R.id.username);
        fullName=findViewById(R.id.fullName);
        phoneNumber=findViewById(R.id.phoneNumber);
        cmnd=findViewById(R.id.identification);

        profile_pic=findViewById(R.id.profile_pic);
        btnSignOut=findViewById(R.id.btnSignOut);

        Toolbar toolbar =findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                username.setText(user.getUsername());
                fullName.setText(user.getFullName());
                phoneNumber.setText(user.getPhoneNumber());
                cmnd.setText(user.getCmnd());


                if (user.getImageURL().equals("default")) {
                    profile_pic.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }

    private void openImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = Profile.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(Profile.this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri!=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask= fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri= task.getResult();
                        String mUri = downloadUri.toString();

                        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }else
                    {
                        Toast.makeText(Profile.this,"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else
        {
            Toast.makeText(Profile.this,"No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();

            if(uploadTask!=null&&uploadTask.isInProgress())
            {
                Toast.makeText(Profile.this," Upload in progress",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadImage();
            }
        }
    }
}