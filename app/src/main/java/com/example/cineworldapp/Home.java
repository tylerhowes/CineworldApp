package com.example.cineworldapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import DailyConcessionsSideNav.DailyConcessionsFragment;
import DailyFloorSideNav.DailyFloorFragment;
import OACConcessionsSideNav.OACConcessionsFragment;
import OACFloorSideNav.OACFloorFragment;
import SideNav.generalFragment;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;

    private TextView userName;
    private TextView userEmail;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Access the header view of the NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Reference the TextView elements in the header
        userName = headerView.findViewById(R.id.usernameText);
        userEmail = headerView.findViewById(R.id.userEmailText);

        String UID = auth.getCurrentUser().getUid();
        db.collection("users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docRef =  task.getResult();
                userName.setText(docRef.get("email").toString());
                userEmail.setText(docRef.get("firstName").toString() + docRef.get("lastName").toString());
            }
        });
        // Update the text
        userName.setText("John Doe");
        userEmail.setText("john.doe@example.com");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.nav_general)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new generalFragment()).commit();
        else if(item.getItemId() == R.id.nav_oac_concessions)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new OACConcessionsFragment()).commit();
        else if(item.getItemId() == R.id.nav_daily_concessions)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new DailyConcessionsFragment()).commit();
        else if(item.getItemId() == R.id.nav_oac_floor)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new OACFloorFragment()).commit();
        else if(item.getItemId() == R.id.nav_daily_floor)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new DailyFloorFragment()).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}