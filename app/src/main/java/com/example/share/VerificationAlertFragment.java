package com.example.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class VerificationAlertFragment extends Fragment {
    TextView verifyMsg;
    Button verifyBtn;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verification_alert_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();

        verifyMsg = view.findViewById(R.id.verifyEmailMsg);
        verifyBtn = view.findViewById(R.id.verifyEmailBtn);

        if(!fAuth.getCurrentUser().isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);
        }else{
            verifyMsg.setVisibility(View.GONE);
            verifyBtn.setVisibility(View.GONE);
        }

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        verifyMsg.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.GONE);
                    }
                });
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
