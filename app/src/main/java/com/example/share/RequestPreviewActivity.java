package com.example.share;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class RequestPreviewActivity extends AppCompatActivity {
    TextView titleTv, descriptionTv, authorTv;
    Button fulfillRequestBtn;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_preview);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        titleTv = findViewById(R.id.requestTitlePreview);
        authorTv = findViewById(R.id.requestAuthor);
        descriptionTv = findViewById(R.id.requestDescriptionPreview);

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

        fulfillRequestBtn = findViewById(R.id.deleteRequestBtn);
        fulfillRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arguments!=null){

                    DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                    documentReference.addSnapshotListener(RequestPreviewActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value!=null) {
                                String userGroup = value.getString("group");

                                DocumentReference documentReference = fStore.collection(userGroup).document(arguments.getString("id"));
                                Map<String, Object> helper = new HashMap<>();
                                helper.put("helper", fAuth.getUid());

                                documentReference.update(helper);

                                fulfillRequestBtn.setText("Thank you");
                                finish();
                            }
                        }
                    });

                }
            }
        });
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