package com.cheshianhung.notetakingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> titleList;
    static ArrayList<String> noteList;
    static ArrayAdapter<String> arrayAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize variables
        listView = (ListView) findViewById(R.id.list);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.cheshianhung.notetakingapp", Context.MODE_PRIVATE);

        loadFile(); //Load the file (length == 1 || length == saved length)
        saveData(); //Save the loaded file
        displayList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add){
            titleList.add("Unknown");
            noteList.add("");
            saveData();
            arrayAdapter.notifyDataSetChanged();

            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("index", titleList.size() - 1);
            startActivity(intent);

            return true;
        }
        else
            return false;

    }

    private void displayList() {

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Do you wish to delete this note?")
                        .setMessage("The deleted note will not be able to retrieve again.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                titleList.remove(position);
                                noteList.remove(position);
                                saveData();
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    private void loadFile() {
        //Read from sharedPreferences
        try {
            titleList =  (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("title", ObjectSerializer.serialize(new ArrayList<String>())));
            noteList =  (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("content", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
            arrayInit();
        }

        //If the data is not valid
        if(titleList.size() == 0 || noteList.size() == 0 || titleList.size() != noteList.size()) {
            //Add a sample note (length == 1)
            arrayInit();
        }
    }

    private void saveData() {
        try {
            sharedPreferences.edit().putString("title", ObjectSerializer.serialize(titleList)).apply();
            sharedPreferences.edit().putString("content", ObjectSerializer.serialize(noteList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void arrayInit() {
        titleList = new ArrayList<String>();
        noteList = new ArrayList<String>();
        titleList.add("Example Note");
        noteList.add("");
    }
}
