package com.example.kalash.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kalash.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class
ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();


        binding.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    forgotPassword();
                }
            }
        });
    }

    // validation of email and password editfields
    private boolean validation() {
        if (binding.forgot.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Email Field is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // function of sending email to registered email address for resetting the password
    private void forgotPassword() {
        auth.sendPasswordResetEmail(binding.forgot.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Show the toast message and finish the forgot password activity to go back to the login screen.
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Email sent successfully",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                    }
                });
    }
}
