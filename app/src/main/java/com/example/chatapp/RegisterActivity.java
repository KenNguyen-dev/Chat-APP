package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userName,passWord,email,fullName,phoneNumber,id,cfpassword;
    Button btnRegister;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("Register","Destroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);


        Log.e("Register","Create");

        auth=FirebaseAuth.getInstance();

        userName=findViewById(R.id.editUsername);
        passWord=findViewById(R.id.editPassword);
        cfpassword=findViewById(R.id.editcfPassword);
        email=findViewById(R.id.editTextEmail);
        fullName=findViewById(R.id.fullName);
        phoneNumber=findViewById(R.id.phoneNumber);
        id=findViewById(R.id.identification);
        btnRegister=findViewById(R.id.btn_Register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userName.getText().toString())||TextUtils.isEmpty(passWord.getText().toString())||TextUtils.isEmpty(email.getText().toString())||
                TextUtils.isEmpty(fullName.getText().toString())||TextUtils.isEmpty(phoneNumber.getText().toString())||TextUtils.isEmpty(id.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"All fields must be filled",Toast.LENGTH_SHORT).show();
                }else if(passWord.getText().toString().length()<6)
                {
                    Toast.makeText(getApplicationContext(),"Passwords length must be greater than 6 characters",Toast.LENGTH_SHORT).show();
                }else if(!passWord.getText().toString().equals(cfpassword.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Confirm password is not correct",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String txt_username=userName.getText().toString();
                    String txt_password=passWord.getText().toString();
                    String txt_email=email.getText().toString();
                    String txt_fullname=fullName.getText().toString();
                    String txt_phonenumber=phoneNumber.getText().toString();
                    String txt_id=id.getText().toString();
                    register(txt_username,txt_password,txt_email,txt_fullname,txt_phonenumber,txt_id);


                }
            }
        });

    }


    private void register(final String username, String password, String email, final String fullname, final String phoneNumber, final String id)
    {
        
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userID = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userID);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        hashMap.put("full_name",fullname);
                        hashMap.put("phone_number",phoneNumber);
                        hashMap.put("cmnd",id);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        });
                    } else{
                        Toast.makeText(getApplicationContext(), "Can't register this email", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
}