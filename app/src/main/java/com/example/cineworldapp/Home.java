package com.example.cineworldapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class Home extends AppCompatActivity {


    DrawerLayout drawerLayout;
    ImageView menuView;
    LinearLayout general, OACConcessions, OACFloor, DailyConcessions, DailyFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            drawerLayout = findViewById(R.id.drawerLayout);
            menuView = findViewById(R.id.menuButton);

            general = findViewById(R.id.general);
            OACConcessions = findViewById(R.id.OACConcessions);
            OACFloor = findViewById(R.id.OACFloor);
            DailyFloor = findViewById(R.id.DailyFloor);
            DailyConcessions = findViewById(R.id.DailyConcessions);

            return insets;
        });



        // Set up menu button to toggle drawer
        menuView.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up click listeners for drawer items
        general.setOnClickListener(view -> {
            replaceFragment(new GeneralFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        OACConcessions.setOnClickListener(view -> {
            replaceFragment(new OACConcessionsFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        OACFloor.setOnClickListener(view -> {
            replaceFragment(new OACFloorFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        DailyConcessions.setOnClickListener(view -> {
            replaceFragment(new DailyConcessionsFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        DailyFloor.setOnClickListener(view -> {
            replaceFragment(new DailyFloorFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Load the default fragment
        if (savedInstanceState == null) {
            replaceFragment(new GeneralFragment());
        }
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closerDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onPause(){
        super.onPause();
        closerDrawer(drawerLayout);
    }
}