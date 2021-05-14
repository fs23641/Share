package com.example.share;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class GroupPreviewFragment extends Fragment {
    TextView name, description, id;
    Button leaveGroup;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    LinearLayout copyL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_preview_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name=view.findViewById(R.id.groupNamePreview);
        description=view.findViewById(R.id.groupDescriptionPreview);
        id=view.findViewById(R.id.groupID);

        copyL=view.findViewById(R.id.copy);
        copyL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", id.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "id copied", Toast.LENGTH_SHORT).show();
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    String userGroup = value.getString("group");

                    DocumentReference documentReference = fStore.collection("groups").document(userGroup);
                    if(getActivity()!=null) {
                        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    name.setText(value.getString("name"));
                                    description.setText(value.getString("description"));
                                    id.setText(userGroup);
                                }
                            }
                        });
                    }

                }
            }
        });

        leaveGroup=view.findViewById(R.id.leaveGroupBtn);
        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                Map<String, Object> user = new HashMap<>();
                user.put("group", "null");
                documentReference.update(user);
            }
        });
    }
}
