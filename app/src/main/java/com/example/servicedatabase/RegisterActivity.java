package com.example.servicedatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextView login;
    EditText Email, Password, cPassword;
    Button register;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.txt_signin);
        Email = findViewById(R.id.txt_email);
        Password = findViewById(R.id.txt_pass);
        cPassword = findViewById(R.id.txt_conpass);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        userId = FirebaseAuth.getInstance().getUid();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //to add into the database
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to create an authenticated user. Athentication using email/password authentication
                createUser();
            }
        });
    }


    private void createUser() {
        String email = Email.getText().toString();
        String pass = Password.getText().toString();
        String cPass = cPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Email.setError("Email cannot be empty");
            Email.requestFocus();
        }else if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(cPass)){
            Password.setError("Password cannot be empty");
            Password.requestFocus();
        }else {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, "Registration Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        Map<String, Object> values = new HashMap<>();
        values.put("Email", email);
        values.put("Password", pass);
        values.put("Confirm Password", cPass);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
         reference.setValue(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // to create a new user if no user is registered
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Toast.makeText(this, "Register Here", Toast.LENGTH_LONG).show();
        }
    }
}