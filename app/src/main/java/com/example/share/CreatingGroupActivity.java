package com.example.share;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatingGroupActivity extends AppCompatActivity {
    Button createGroupBtn;
    EditText groupName, groupDesc;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_group);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        groupName=findViewById(R.id.groupName);
        groupDesc=findViewById(R.id.groupDescription);

        createGroupBtn=findViewById(R.id.finishCreateGroupBtn);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> group = new HashMap<>();
                group.put("name", groupName.getText().toString());
                group.put("description", groupDesc.getText().toString());
                Task<DocumentReference> addedDocRef = fStore.collection("groups").add(group).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                        Map<String, Object> user = new HashMap<>();
                        user.put("group",task.getResult().getId());
                        documentReference.update(user);
                    }
                });

                finish();
            }
        });
    }
}