package com.ugur.mychatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.R;

import java.util.Random;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout emailText, passwordText, pwControlText, nameText;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.email_text_signup);
        passwordText = findViewById(R.id.password_text_signup);
        pwControlText = findViewById(R.id.password_control_text);
        nameText = findViewById(R.id.name_text);

    }

    public void confirmSignup(View view) {
        String emailInput = emailText.getEditText().getText().toString().trim();
        String passwordInput = passwordText.getEditText().getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    User user = User.getInstance();
                    user.setUuid(firebaseUser.getUid());
                    user.setEmail(emailInput);
                    user.setName(nameText.getEditText().getText().toString());
                    Random random = new Random();
                    String id = "#";
                    for (int i=0; i<6; i++) {
                        id += String.valueOf(random.nextInt(10));
                    }
                    User.getInstance().setId(id);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference();
                    reference.child("Users").child(user.getUuid()).child("Email").setValue(user.getEmail());
                    reference.child("Users").child(user.getUuid()).child("Name").setValue(user.getName());
                    reference.child("Users").child(user.getUuid()).child("Id").setValue(user.getId());

                    startActivity(new Intent(getApplicationContext(), ImageSelectionActivity.class));
                    finish();
                } else {
                    System.out.println("HATA");
                    System.out.println(task.getException().getLocalizedMessage());
                    System.out.println(task.getResult().toString());
                }
            }
        });
    }

    public void goLogIn(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}