package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class StartActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tvRegister;
    EditText editTextEmail;
    EditText editTextPassword;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
        {
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("Start","Destroy");
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("Start","Create");


        auth= FirebaseAuth.getInstance();



        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin=findViewById(R.id.btnLogin);
        tvRegister=findViewById(R.id.tvRegister);

        btnLogin.setEnabled(false);

        TextWatcher textWatcher= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnLogin.setEnabled(!TextUtils.isEmpty(editTextEmail.getText().toString())&&!TextUtils.isEmpty(editTextPassword.getText().toString()));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        editTextPassword.addTextChangedListener(textWatcher);
        editTextEmail.addTextChangedListener(textWatcher);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTextEmail.getText().toString())||TextUtils.isEmpty(editTextPassword.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"All fields must be filled",Toast.LENGTH_SHORT).show();
                }else
                {
                    SignIn(editTextEmail.getText().toString(),editTextPassword.getText().toString());
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

    }



    private void SignIn(String email,String password)
    {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }else
                {
                    Toast.makeText(getApplicationContext(),"Wrong email or password",Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }



}
