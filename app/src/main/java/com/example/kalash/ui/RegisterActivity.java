package com.example.kalash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kalash.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth auth;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    register();
                }
            }
        });
        binding.gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validation() {
        if (binding.firstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "First Name Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.lastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Last Name Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Email Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.password.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Password Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.confirmPassword.getText().toString().trim().isEmpty()
                && binding.confirmPassword.getText().toString().equals(binding.password.getText().toString())) {
            Toast.makeText(this, "Confirm Password Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.checkbox.isChecked()) {
            Toast.makeText(this, "Please accept the Terms&Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void register() {
        auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            auth.signOut();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(
                                    getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    }});
    }}


