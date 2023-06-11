package com.example.kalash.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kalash.R;
import com.example.kalash.databinding.ActivityContactPickerBinding;
import com.example.kalash.databinding.ActivityEditMessageBinding;

public class EditMessageActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String message="I'm in Trouble!!";
    static final String SHARED_PREF_NAME = "WomenSafetyApp";
    SharedPreferences.Editor editor;
    static final String SMS_MESSAGE="sms_message";
    private ActivityEditMessageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityEditMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding.sosMsg1.setText(getSOSMessage());

    }
    public String getSOSMessage() {
        return sharedPreferences.getString(SMS_MESSAGE, message);
    }

    public void editSOS(View view)
    {
     putMessage(binding.sosMsg1.getText().toString());
        Toast.makeText(this, "Message Saved Successfully.", Toast.LENGTH_SHORT).show();
    }
    public void navigate(View v){

            finish();
        }
    public void putMessage(String sms_message ) {
        editor.putString(SMS_MESSAGE,sms_message);
        editor.apply();
    }
}