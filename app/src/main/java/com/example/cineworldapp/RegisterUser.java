package com.example.cineworldapp;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterUser extends AppCompatActivity {

    EditText firstNameView;
    EditText lastNameView;
    EditText emailView;
    EditText loginCodeView;
    Button registerButton;

    boolean loginCodeIsValid = false;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            firstNameView = findViewById(R.id.firstNameInput);
            lastNameView = findViewById(R.id.lastNameInput);
            emailView = findViewById(R.id.emailInput);
            loginCodeView = findViewById(R.id.loginCodeInput);
            registerButton = findViewById(R.id.registerButton);

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String firstName = String.valueOf(firstNameView.getText());
                    String lastName = String.valueOf(lastNameView.getText());
                    String email = emailView.getText().toString();
                    String loginCode = loginCodeView.getText().toString();

                    checkUniqueLoginCode(loginCode);

                    if(loginCodeIsValid){
                        auth.createUserWithEmailAndPassword(email, loginCode)
                                .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                if(user != null){
                                    String userID = user.getUid();
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", email);
                                    userData.put("loginCode", loginCode);
                                    userData.put("firstName", firstName);
                                    userData.put("lastName", lastName);
                                    userData.put("initials", "" + firstName.charAt(0) + lastName.charAt(0));

                                    db.collection("users").document(userID).set(userData)
                                            .addOnSuccessListener(aVoid -> Log.d("SignUp","Sign up success"))
                                            .addOnFailureListener(e -> Log.e("SignUp", "Error signing up: " + e));
                                }
                                finish();
                            }
                            else{
                                Log.e("SignUp", "Error" + task.getException().getMessage());
                            }
                        });
                    }
                }
            });

            return insets;
        });
    }

    private void checkUniqueLoginCode(String loginCode){
        db.collection("users").whereEqualTo("loginCode", loginCode).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult().isEmpty()){
                        loginCodeIsValid = true;
                    }else{
                        Toast.makeText(this, "Invalid 4 Digit Code Please Enter New Code", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}