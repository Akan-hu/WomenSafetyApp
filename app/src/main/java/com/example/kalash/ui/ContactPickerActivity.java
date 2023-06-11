package com.example.kalash.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kalash.adapter.NumbersListAdapter;
import com.example.kalash.databinding.ActivityContactPickerBinding;
import com.example.kalash.utils.DeleteNumber;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContactPickerActivity extends AppCompatActivity implements DeleteNumber {

    static int CONTACT_PICKER_REQUEST=123;
    static int READ_CONTACTS =1;
    List<ContactResult> results = new ArrayList<>();
    List<String> numbersList = new ArrayList<>();
    private ActivityContactPickerBinding binding;
    static final String PHONE_NUMBER = "phoneNumber";
    static final String IS_LOGIN = "is_login";
    static final String SHARED_PREF_NAME = "WomenSafetyApp";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityContactPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Set<String> set =  getPhoneNumber();
        numbersList.addAll(set);
        if (numbersList.size()>1) {
            NumbersListAdapter numbersListAdapter = new NumbersListAdapter(numbersList, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            binding.rvNumberList.setAdapter(numbersListAdapter);
            binding.rvNumberList.setLayoutManager(linearLayoutManager);
        }



        binding.chooseContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             requestPermission();
            }
        });
    }

    public void navigate(View v){
        if (numbersList.size()==0)
            Toast.makeText(this, "Select Atleast 1 Number", Toast.LENGTH_SHORT).show();
       else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==READ_CONTACTS && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            launchPicker();
        }

    }

    void requestPermission()
    {
        if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},READ_CONTACTS );
        else{
            launchPicker();
        }
    }

    public void launchPicker(){

        new MultiContactPicker.Builder(this)//Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setTitleText("Select Contacts")
                .setLoadingType(MultiContactPicker.LOAD_SYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                results = MultiContactPicker.obtainResult(data);
                for (int i = 0;i<results.size();i++) {
                    for (int j=0;j<results.get(i).getPhoneNumbers().size();j++) {

                        numbersList.add(results.get(i).getPhoneNumbers().get(j).getNumber());
                        List<String> newList = numbersList.stream()
                                .distinct()
                                .collect(Collectors.toList());

                        NumbersListAdapter  numbersListAdapter = new NumbersListAdapter( newList, this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                        binding.rvNumberList.setAdapter(numbersListAdapter);
                        binding.rvNumberList.setLayoutManager(linearLayoutManager);

                    }
                }

            } else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "User closed the picker without selecting items.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void deleteNumber(String number) {
        numbersList.remove(number);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set<String> set = new HashSet<>(numbersList);

        putPhoneNumber(set);
    }

    public void putPhoneNumber( Set<String> number) {
        editor.putStringSet(PHONE_NUMBER, number);
        editor.apply();
    }

    public Set<String> getPhoneNumber() {
        Set<String> mutableSet = new HashSet<String>();
        return sharedPreferences.getStringSet(PHONE_NUMBER, mutableSet);
    }
}