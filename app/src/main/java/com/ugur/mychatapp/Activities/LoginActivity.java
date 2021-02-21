package com.ugur.mychatapp.Activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;
import com.ugur.mychatapp.BaseActivity;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.R;
import com.ugur.mychatapp.SinchService;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {
    private TextInputLayout emailText, passwordText;
    private FirebaseAuth mAuth;
    private boolean initializeSinchService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.email_text_login);
        passwordText = findViewById(R.id.password_text_login);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            initializeSinchService = true;
            getData();
        }
    }

    public void confirmLogin(View view) {
        String emailInput = emailText.getEditText().getText().toString().trim();
        String passwordInput = passwordText.getEditText().getText().toString().trim();
        mAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("LOGGED IN");
                    String userId = mAuth.getCurrentUser().getUid();
                    System.out.println("USER ID: " + userId);
                    System.out.println("SINCH SERVICE INTERFACE: " + getSinchServiceInterface());

                    if (!userId.equals(getSinchServiceInterface().getUserName())) {
                        System.out.println("CLIENT STOPPING");
                        System.out.println("USER ID, " + userId + " getSinchServiceName: " + getSinchServiceInterface().getUserName());
                        getSinchServiceInterface().stopClient();
                    }

                    if (!getSinchServiceInterface().isStarted()) {
                        System.out.println("BAŞLATILIYOR...");
                        getSinchServiceInterface().startClient(userId);
                    } else {
                        System.out.println("ELSE'DEKİ OPEN PLACE CALL ACTIVITY");
                    }

                    System.out.println("BUYUR: " + getSinchServiceInterface().getUserName());

                    getData();
                } else {
                    System.out.println("LOGIN TASK UNSUCCESSFUL");
                }
            }
        });
    }

    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = User.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String firebaseUUID = firebaseUser.getUid();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    if (ds.getKey().equals(firebaseUUID)) {
                        user.setUuid(ds.getKey());
                        user.setImageUri(Uri.parse(hashMap.get("PhotoUri")));
                        user.setEmail(hashMap.get("Email"));
                        user.setName(hashMap.get("Name"));
                        user.setId(hashMap.get("Id"));
                    }
                }

                if (initializeSinchService) {
                    System.out.println("EVETTTTTTT");
                    String userId = mAuth.getCurrentUser().getUid();
                    try {
                        if (!userId.equals(getSinchServiceInterface().getUserName())) {
                            System.out.println("CLIENT STOPPING");
                            System.out.println("USER ID, " + userId + " getSinchServiceName: " + getSinchServiceInterface().getUserName());
                            getSinchServiceInterface().stopClient();
                        }

                        if (!getSinchServiceInterface().isStarted()) {
                            System.out.println("BAŞLATILIYOR...");
                            getSinchServiceInterface().startClient(userId);
                        } else {
                            System.out.println("ELSE'DEKİ OPEN PLACE CALL ACTIVITY");
                        }

                        System.out.println("BUYUR: " + getSinchServiceInterface().getUserName());
                    } catch (Exception e) {
                        System.out.println("GODDAMNIT");
                    }
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void goSignUp(View view) {
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        finish();
    }

    @Override
    protected void onServiceConnected() {
        System.out.println("LOGIN ACTIVITY ON SERVICE CONNECTED");
        getSinchServiceInterface().setStartListener(this);
        System.out.println("GET SINCH SERVICE INTERFACE" + getSinchServiceInterface());
    }

    @Override
    protected void onPause() {
        System.out.println("ON PAUSE");
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        System.out.println("ON START FAILED");
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted() {
        System.out.println("ON STARTED");
        System.out.println("OPEN PLACE CALL ACTIVITY");
    }
}