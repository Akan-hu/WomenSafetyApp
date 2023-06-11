package com.example.kalash.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.kalash.R;

public class IntroActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static final String PHONE_NUMBER = "phoneNumber";
    static final String IS_LOGIN = "is_login";
    static final String SHARED_PREF_NAME = "WomenSafetyApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

   public void navigate(View v){
       if(isLogin())
           startActivity(new Intent(IntroActivity.this,InstructionsActivity.class));
       else
           startActivity(new Intent(IntroActivity.this,LoginActivity.class));
       finish();
    }
    public boolean isLogin() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

}