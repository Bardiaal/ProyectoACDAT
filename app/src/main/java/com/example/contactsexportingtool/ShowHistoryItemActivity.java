package com.example.contactsexportingtool;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ShowHistoryItemActivity extends AppCompatActivity {

    TextView textView3, textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowHistoryItemActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();

        textView4.setText(intent.getStringExtra(getResources().getString(R.string.titleHistObj)));
        textView3.setText(intent.getStringExtra(getResources().getString(R.string.contentHistObj)));
    }

}
