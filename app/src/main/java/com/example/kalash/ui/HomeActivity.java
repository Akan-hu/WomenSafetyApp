package com.example.kalash.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kalash.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
   public void goToInstructions(View view)
    {
        Intent i = new Intent(this, InstructionsActivity.class);
        startActivity(i);

    }
    public void navigate(View v){

            Intent i = new Intent(this, ContactPickerActivity.class);
            startActivity(i);
        }

    public void editSOS(View view)
    {
        Intent i = new Intent(this, EditMessageActivity.class);
        startActivity(i);

    }

    public void deleteNumber(View view)
    {
        Intent i = new Intent(this, DeleteActivity.class);
        startActivity(i);

    }

}