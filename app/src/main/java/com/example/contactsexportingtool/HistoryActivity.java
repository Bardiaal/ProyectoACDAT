package com.example.contactsexportingtool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> hs;
    ArrayAdapter arrayAdapter;
    private static final int NONE = -1;
    private static final int INTERN = 0;
    private static final int PUBLIC = 1;
    private static final int PRIVATE = 2;
    int type;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        listView = findViewById(R.id.lv2);
        ShowContactsActivity.historyItems = readSerializedObject();
        hs = new ArrayList<>();
        for (int i = 0; i < ShowContactsActivity.historyItems.size(); i++) {
            hs.add(ShowContactsActivity.historyItems.get(i).getLabel());
        }

        setArrayAdapter();
        listView.setAdapter(arrayAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                try {
                    HistoryItem historyItem = ShowContactsActivity.historyItems.get(pos);
                    readFile(historyItem.getName() + historyItem.getExtension(), historyItem);
                    Intent intent1 = new Intent(HistoryActivity.this, ShowHistoryItemActivity.class);
                    intent1.putExtra(getResources().getString(R.string.titleHistObj), historyItem.getName() + historyItem.getExtension());
                    intent1.putExtra(getResources().getString(R.string.contentHistObj), content);
                    startActivity(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setArrayAdapter() {
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, hs);
    }

    public ArrayList<HistoryItem> readSerializedObject() {
        ArrayList<HistoryItem> historyItemArrayList = new ArrayList<>();
        try {
            FileInputStream fis = HistoryActivity.this.openFileInput(getResources().getString(R.string.serializedObjectsFile));
            ObjectInputStream ois = new ObjectInputStream(fis);

            historyItemArrayList = (ArrayList) ois.readObject();

            ois.close();
            fis.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return historyItemArrayList;
    }

    private boolean isValues(String name, HistoryItem historyItem) {
        type = getCheckedType(historyItem);
        return !(name.isEmpty() || type == NONE); // Devuelve false si está vacío o si es -1
    }

    private void readFile(String name, HistoryItem historyItem) {
        if (isValues(name, historyItem)) {
            readNotes(name);
        }
    }

    private void readNotes(String name) {
        File f = new File(getFile(HistoryActivity.this, type), name);
        Log.v(getResources().getString(R.string.info_saved), f.getAbsolutePath());
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuffer lineas = new StringBuffer("");
            while ((linea = br.readLine()) != null) {
                lineas.append(linea + "\n");
            }
            br.close();
            content = lineas.toString();
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(HistoryActivity.this, getResources().getString(R.string.fileNotFound), Toast.LENGTH_LONG).show();
        }
    }

    private int getCheckedType(HistoryItem historyItem) {
        int tipo = NONE;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storageType = sharedPreferences.getString(getResources().getString(R.string.key_almacenamiento), "");
        if (historyItem.getStorageType().equals(getResources().getString(R.string.INTERN))) {
            tipo = INTERN;
        } else if (historyItem.getStorageType().equals(getResources().getString(R.string.PUBLIC))) {
            tipo = PUBLIC;
        } else if (historyItem.getStorageType().equals(getResources().getString(R.string.PRIVATE))) {
            tipo = PRIVATE;
        }
        return  tipo;
    }

    @SuppressWarnings("deprecation")
    private static File getFile(Context context, int type) {
        File file = null;
        switch (type) {
            case INTERN:
                file = context.getFilesDir();
                break;
            case PUBLIC:
                file = Environment.getExternalStorageDirectory();
                break;
            case PRIVATE:
                file = context.getExternalFilesDir(null);
                break;
        }
        return file;
    }

}
