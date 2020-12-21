package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.util.GAuthToken;

import java.io.FileReader;

public class Activity_reset_password extends AppCompatActivity {
    EditText sendEmail;
    Button btnReset;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();

        sendEmail=findViewById(R.id.send_email);
        btnReset=findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=sendEmail.getText().toString();
                if(email.equals("")){
                    Toast.makeText(Activity_reset_password.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Activity_reset_password.this, "Please check your Email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Activity_reset_password.this, StartActivity.class));
                            } else{
                                String error=task.getException().getMessage();
                                Toast.makeText(Activity_reset_password.this, error, Toast.LENGTH_SHORT ).show();
                            }


                        }

                    });
                }
            }
        });
    }
}