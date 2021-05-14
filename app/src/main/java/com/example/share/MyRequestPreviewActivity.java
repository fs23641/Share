package com.example.share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MyRequestPreviewActivity extends AppCompatActivity {
    TextView titleTv, descriptionTv, authorTv;
    Button deleteRequestBtn, openProfileBtn;
    LinearLayout profile;

    String helper;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request_preview);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        titleTv = findViewById(R.id.requestTitlePreview);
        authorTv = findViewById(R.id.requestAuthor);
        descriptionTv = findViewById(R.id.requestDescriptionPreview);

        profile=findViewById(R.id.profileLinear);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            titleTv.setText(arguments.get("title").toString());
            descriptionTv.setText(arguments.get("description").toString());
            authorTv.setText(arguments.get("author").toString());
        }else{
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        deleteRequestBtn = findViewById(R.id.deleteRequestBtn);
        deleteRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arguments!=null){

                    DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                    documentReference.addSnapshotListener(MyRequestPreviewActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value!=null) {
                                String userGroup = value.getString("group");

                                DocumentReference documentReference = fStore.collection(userGroup).document(arguments.getString("id"));
                                documentReference.delete();
                                finish();
                            }
                        }
                    });

                }
            }
        });

        openProfileBtn = findViewById(R.id.openProfileBtn);
        openProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfilePreviewActivity.class);
                intent.putExtra("id", helper);
                v.getContext().startActivity(intent);
            }
        });




        if(arguments!=null){

            DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            documentReference.addSnapshotListener(MyRequestPreviewActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value!=null) {
                        String userGroup = value.getString("group");

                        DocumentReference documentReference = fStore.collection(userGroup).document(arguments.getString("id"));
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(value.get("helper")!=null){
                                    helper=value.get("helper").toString();
                                    if(helper.equals("null")){
                                        profile.setVisibility(View.GONE);
                                    }else{
                                        profile.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}