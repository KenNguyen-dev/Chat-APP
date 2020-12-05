package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.model.Chats;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    private static final  int GALLERY_PICK=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton btn_send;
    ImageButton btn_send_image;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chats> chatsList;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);


        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        storageReference= FirebaseStorage.getInstance().getReference("image_send");

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());
        linearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayout);


        profile_image = findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        btn_send_image = findViewById(R.id.btn_send_photo);

        intent=getIntent();
        final String userid=intent.getStringExtra("userid");


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg= text_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(firebaseUser.getUid(),userid,msg,"text");
                }else
                {
                    Toast.makeText(getApplicationContext(),"Empty message",Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else
                {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(userid);

        btn_send_image.setOnClickListener(new View.OnClickListener() {
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
        startActivityForResult(intent,GALLERY_PICK);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = MessagingActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();

            if(uploadTask!=null&&uploadTask.isInProgress())
            {
                Toast.makeText(MessagingActivity.this," Upload in progress",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadImage();
            }
        }
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(MessagingActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri!=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask= fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        final String userid=intent.getStringExtra("userid");

//                        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put("imageURL",mUri);
//                        reference.updateChildren(map);

                        sendMessage(firebaseUser.getUid(),userid,mUri,"image");



                        pd.dismiss();
                    }else
                    {
                        Toast.makeText(MessagingActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessagingActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else
        {
            Toast.makeText(MessagingActivity.this,"No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void seenMessage(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private  void sendMessage(String sender,String receiver, String message,String type)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String userid = intent.getStringExtra("userid");

        HashMap<String,Object> hashMap= new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        hashMap.put("type",type);


        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessage(final String myID, final String userID, final String imageURL)
    {
        chatsList = new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if(chats.getReceiver().equals(myID)&& chats.getSender().equals(userID)
                            ||chats.getReceiver().equals(userID)&&chats.getSender().equals(myID))
                    {
                        chatsList.add(chats);
                    }
                    messageAdapter= new MessageAdapter(MessagingActivity.this,chatsList,imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status)
    {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }


}