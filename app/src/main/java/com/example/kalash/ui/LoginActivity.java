package com.example.kalash.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kalash.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    String SHARED_PREF_NAME = "WomenSafetyApp";
    static final String PHONE_NUMBER = "phoneNumber";
    static final String IS_LOGIN = "is_login";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        auth = FirebaseAuth.getInstance();

        binding.login.setOnClickListener(view -> {
            if (validation()) {
                login();
            }
        });
        binding.forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        binding.newUser.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    //validation of email and password editfields
    private boolean validation() {
        if (binding.loginEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Email Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.loginPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Password Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void login() {
        auth.signInWithEmailAndPassword(binding.loginEmail.getText().toString().trim(), binding.loginPassword.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        loggedInSuccess();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(
                                getBaseContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loggedInSuccess() {
        putLogin(true);
        //else go to the user profile activity to complete the profile.
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
        finish();
    }

    public void putLogin(boolean isLogin ) {
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.apply();
    }
}
