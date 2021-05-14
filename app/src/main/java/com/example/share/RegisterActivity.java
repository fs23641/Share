package com.example.share;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText registerFullName, registerEmail, registerPassword, registerConfPassword, contactData;
    Button registerUserBtn, gotologin;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerFullName=findViewById(R.id.registerFullName);
        registerEmail=findViewById(R.id.registerEmailAddress);
        registerPassword=findViewById(R.id.registerPassword);
        registerConfPassword=findViewById(R.id.confPassword);
        registerUserBtn=findViewById(R.id.register);
        gotologin=findViewById(R.id.gotologin);
        contactData=findViewById(R.id.contactData);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registerFullName.getText().toString().isEmpty()){
                    registerFullName.setError("Full Name is Required");
                    return;
                }
                if(registerEmail.getText().toString().isEmpty()){
                    registerEmail.setError("Email is Required");
                    return;
                }
                if(registerPassword.getText().toString().isEmpty()){
                    registerPassword.setError("Password is Required");
                    return;
                }
                if(registerConfPassword.getText().toString().isEmpty()){
                    registerConfPassword.setError("Password Confirmation is Required");
                    return;
                }

                if(!registerPassword.getText().toString().equals(registerConfPassword.getText().toString())){
                    registerConfPassword.setError("Password Do not Match");
                    return;
                }

                if(contactData.getText().toString().isEmpty()){
                    contactData.setError("Others won't be able to find you");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(registerEmail.getText().toString(), registerPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", registerFullName.getText().toString());
                        user.put("group", "null");
                        user.put("contactData", contactData.getText().toString());
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(RegisterActivity.this, "Database updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}