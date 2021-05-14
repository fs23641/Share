package com.example.share;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class NewRequestActivity extends AppCompatActivity {

    Button newRequestBtn;
    EditText requestTitle, requestDesc;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        requestTitle=findViewById(R.id.requestTitle);
        requestDesc=findViewById(R.id.requestDescription);

        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    userGroup = value.getString("group");
                }
            }
        });

        newRequestBtn = findViewById(R.id.finishCreateRequestBtn);
        newRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference documentReference = fStore.collection(userGroup).document(Timestamp.now()+"."+fAuth.getCurrentUser().getUid());
                Map<String, Object> request = new HashMap<>();
                request.put("title", requestTitle.getText().toString());
                request.put("description", requestDesc.getText().toString());
                request.put("helper", "null");
                documentReference.set(request);
                finish();
            }
        });
    }
}