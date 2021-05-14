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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText newPass, newPassConf;
    Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        newPass = findViewById(R.id.newUserPassword);
        newPassConf = findViewById(R.id.newPassConfirm);

        resetBtn = findViewById(R.id.resetPasswordBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass.getText().toString().isEmpty()){
                    newPass.setError("Password is Required");
                    return;
                }
                if(newPassConf.getText().toString().isEmpty()){
                    newPassConf.setError("Password Confirmation is Required");
                    return;
                }

                if(!newPass.getText().toString().equals(newPassConf.getText().toString())){
                    newPassConf.setError("Password Do not Match");
                    return;
                }

                FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResetPasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}