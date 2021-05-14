package com.example.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsFragment extends Fragment {

    ArrayList<Request> requests;
    RecyclerView recyclerView;
    RecyclerRequestAdapter adapter;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_requests, container, false);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();

        requests=new ArrayList<>();

        recyclerView=view.findViewById(R.id.myRequestsRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter=new RecyclerRequestAdapter(getActivity().getApplicationContext(), requests);
        recyclerView.setAdapter(adapter);


        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    userGroup = value.getString("group");
                        CollectionReference cr = fStore.collection(userGroup);
                        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                String uID = fAuth.getUid();
                                for (int i = documentSnapshots.size() - 1; i >= 0; i--) {
                                    DocumentSnapshot ds = documentSnapshots.get(i);
                                    if (ds.getId().split("\\.")[1].equals(uID)) {

                                        final String[] userName = new String[1];

                                        DocumentReference documentReference = fStore.collection("users").document(ds.getId().split("\\.")[1]);
                                        if(getActivity()!=null) {
                                            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    if (value != null) {
                                                        userName[0] = value.getString("name");
                                                        requests.add(new Request(ds.get("title").toString(), ds.get("description").toString(), userName[0], ds.get("helper").toString(), ds.getId()));
                                                        adapter = new RecyclerRequestAdapter(getActivity() != null ? getActivity().getApplicationContext() : null, requests);
                                                        recyclerView.setAdapter(adapter);
                                                    }
                                                }
                                            });
                                        }
                                    }


                                }
                            }
                        });
                    }
                }
        });





        ImageButton newRequest = view.findViewById(R.id.new_request_button2);
        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), NewRequestActivity.class));
            }
        });
    }
}
