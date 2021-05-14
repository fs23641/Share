package com.example.share;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userName;
    String userEmail;
    String userGroup;

    NavigationView navigationView;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName="";
        userGroup="";
        userEmail="";


        fAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    userName = value.getString("name");
                    userGroup = value.getString("group");

                    //first fragment
                    if(savedInstanceState == null) {
                        if(!userGroup.equals("null")) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyRequestsFragment()).commit();
                        else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoGroupFragment()).commit();
                        navigationView.setCheckedItem(R.id.nav_my_requests);
                    }

                }
            }
        });

        userEmail=fAuth.getCurrentUser().getEmail();
    }



    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_my_requests:
                if(!userGroup.equals("null")) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyRequestsFragment()).commit();
                else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoGroupFragment()).commit();
                break;
            case R.id.nav_all_requests:
                if(!userGroup.equals("null")) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllRequestsFragment()).commit();
                else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoGroupFragment()).commit();
                break;
            case R.id.nav_group:
                if(userGroup.equals("null")) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupFragment()).commit();
                else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupPreviewFragment()).commit();
                break;

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //updating header
        TextView fullName, email;
        fullName=findViewById(R.id.menuUserFullName);
        email=findViewById(R.id.menuUserEmail);
        fullName.setText(userName!=null?userName:"Error");
        email.setText(userEmail!=null?userEmail:"Error");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    userName = value.getString("name");
                    userGroup = value.getString("group");

                    TextView fullName, email;
                    fullName=findViewById(R.id.menuUserFullName);
                    email=findViewById(R.id.menuUserEmail);
                    if(fullName!=null)fullName.setText(userName);
                    if(email!=null)email.setText(userEmail);

                    updateMenu();
                }
            }
        });
    }

    void updateMenu(){
        MenuItem item =navigationView.getCheckedItem();
        if(item!=null) {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    break;
                case R.id.nav_my_requests:
                    if (!userGroup.equals("null"))
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyRequestsFragment()).commit();
                    else
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoGroupFragment()).commit();
                    break;
                case R.id.nav_all_requests:
                    if (!userGroup.equals("null"))
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllRequestsFragment()).commit();
                    else
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoGroupFragment()).commit();
                    break;
                case R.id.nav_group:
                    if (userGroup.equals("null"))
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupFragment()).commit();
                    else
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupPreviewFragment()).commit();
                    break;

            }
        }
    }
}