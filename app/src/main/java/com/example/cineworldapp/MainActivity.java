package com.example.cineworldapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    Button signUpButton;
    Button loginButton;
    Button resetButton;
    EditText loginCodeInputView;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            loginCodeInputView = findViewById(R.id.loginCodeInput);

            signUpButton = findViewById(R.id.signUpButton);
            Intent registerIntent = new Intent(this, RegisterUser.class);
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.this.startActivity(registerIntent);
                }
            });

            loginButton = findViewById(R.id.loginButton);
            Intent loginIntent = new Intent(this, Home.class);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String loginCodeInput = loginCodeInputView.getText().toString();
                    db.collection("users").whereEqualTo("loginCode",loginCodeInput).get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    String email = document.getString("email");

                                    auth.signInWithEmailAndPassword(email, loginCodeInput)
                                            .addOnCompleteListener(authTask -> {
                                               if(authTask.isSuccessful()){
                                                   FirebaseUser user = auth.getCurrentUser();
                                                   if(user != null){
                                                       Log.d("Login", "Logged in as: " + user.getEmail());
                                                       startActivity(loginIntent);
                                                       finish();

                                                   }
                                                   else {
                                                       Log.e("Login", "Error: " + authTask.getException());
                                                   }
                                               }
                                            });
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Login not found", Toast.LENGTH_SHORT).show();
                                    Log.e("Login", "Login not found or Error: " + task.getException());
                                }
                            });

                }
            });

            resetButton = findViewById(R.id.clearPreferencesButton);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the directory where shared preferences are stored
                    File sharedPrefsDir = new File(getApplicationContext().getFilesDir().getParent() + "/shared_prefs/");

                    if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory()) {
                        // Loop through all shared preferences files and delete them
                        for (File file : sharedPrefsDir.listFiles()) {
                            if (file.delete()) {
                                Log.d("ClearPrefs", "Deleted: " + file.getName());
                            } else {
                                Log.e("ClearPrefs", "Failed to delete: " + file.getName());
                            }
                        }
                        Toast.makeText(MainActivity.this, "All Shared Preferences Cleared", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ClearPrefs", "Shared preferences directory does not exist");
                    }
                }

            });

            return insets;
        });
    }
}