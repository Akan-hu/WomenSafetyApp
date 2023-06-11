package com.example.kalash.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kalash.R;
import com.example.kalash.adapter.NumbersListAdapter;
import com.example.kalash.databinding.ActivityDeleteBinding;
import com.example.kalash.databinding.ActivityEditMessageBinding;
import com.example.kalash.utils.DeleteNumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DeleteActivity extends AppCompatActivity implements DeleteNumber {

    private ActivityDeleteBinding binding;
    SharedPreferences.Editor editor;
    static final String SHARED_PREF_NAME = "WomenSafetyApp";

    SharedPreferences sharedPreferences;
    static final String PHONE_NUMBER = "phoneNumber";

    List<String> numbersList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityDeleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        numbersList.addAll(getPhoneNumber());

        NumbersListAdapter numbersListAdapter = new NumbersListAdapter( numbersList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvNumberList.setAdapter(numbersListAdapter);
        binding.rvNumberList.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void deleteNumber(String number) {
        numbersList.remove(number);
        Toast.makeText(this, "Number Deleted", Toast.LENGTH_SHORT).show();
    }
    public Set<String> getPhoneNumber() {
        Set<String> mutableSet = new HashSet<String>();
        return sharedPreferences.getStringSet(PHONE_NUMBER, mutableSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set<String> set = new HashSet<String>();
        for (int i=0;i<numbersList.size();i++)
        {
            set.add(numbersList.get(i));
        }

        putPhoneNumber(set);
    }
    public void navigate(View v) {

            finish();

    }
    public void putPhoneNumber( Set<String> number) {
        editor.putStringSet(PHONE_NUMBER, number);
        editor.apply();
    }
}