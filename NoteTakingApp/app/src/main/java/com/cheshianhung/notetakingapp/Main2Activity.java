package com.cheshianhung.notetakingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;

public class Main2Activity extends AppCompatActivity {

    EditText title;
    EditText content;
    Integer indexFromIntent;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        title = (EditText) findViewById(R.id.noteTitle);
        content = (EditText) findViewById(R.id.noteText);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.cheshianhung.notetakingapp", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        indexFromIntent = intent.getIntExtra("index", -1);

        getIndexFromIntent();
        setEditTextListener();
    }

    private void getIndexFromIntent() {

        if(indexFromIntent != -1){

            title.setText(MainActivity.titleList.get(indexFromIntent));
            content.setText(MainActivity.noteList.get(indexFromIntent));

            if(MainActivity.titleList.get(indexFromIntent).equals("Unknown")){
                title.setText("");
            }

            saveTitle();
            saveContent();
        }
        else{
            Toast.makeText(getApplicationContext(),"Error: Please re-do the process.", Toast.LENGTH_SHORT).show();
        }

    }

    private void setEditTextListener(){

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    MainActivity.titleList.set(indexFromIntent, "Unknown");
                }
                else {
                    MainActivity.titleList.set(indexFromIntent, s.toString());
                }
                saveTitle();
                MainActivity.arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.noteList.set(indexFromIntent, s.toString());
                saveContent();
                MainActivity.arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void saveTitle(){
        try {
            sharedPreferences.edit().putString("title", ObjectSerializer.serialize(MainActivity.titleList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveContent() {
        try {
            sharedPreferences.edit().putString("content", ObjectSerializer.serialize(MainActivity.noteList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
