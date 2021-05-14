package com.example.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllRequestsFragment extends Fragment {
    ArrayList<Request> requests;
    RecyclerView recyclerView;
    RecyclerRequestAdapter adapter;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        requests=new ArrayList<>();

        recyclerView=view.findViewById(R.id.allRequestsRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter=new RecyclerRequestAdapter(getActivity().getApplicationContext(), requests);
        recyclerView.setAdapter(adapter);

        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    userGroup = value.getString("group");
                    CollectionReference cr =fStore.collection(userGroup);
                    cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            for(int i=documentSnapshots.size()-1; i>=0;i--){
                                DocumentSnapshot ds=documentSnapshots.get(i);

                                final String[] userName = new String[1];

                                DocumentReference documentReference = fStore.collection("users").document(ds.getId().split("\\.")[1]);
                                int finalI = i;
                                documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(value!=null) {
                                            userName[0] = value.getString("name");
                                            if(ds.get("helper").toString().equals("null")) requests.add(new Request(ds.get("title").toString(),ds.get("description").toString(),userName[0], ds.get("helper").toString(), ds.getId()));
                                            if(finalI ==0) {
                                                Activity a = getActivity();
                                                if(a!=null)adapter = new RecyclerRequestAdapter(a.getApplicationContext(), requests);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });

        ImageButton newRequest = view.findViewById(R.id.new_request_button);
        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), NewRequestActivity.class));
            }
        });

    }
}
