package com.example.contactsexportingtool;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import  java.io.Serializable;

public class ShowContactsActivity extends AppCompatActivity {

    ArrayAdapter arrayAdapter;
    //RecyclerView recyclerView;
    ListView listView;
    private static final int NONE = -1;
    private static final int INTERN = 0;
    private static final int PUBLIC = 1;
    private static final int PRIVATE = 2;
    String ext = "", st = "";
    String name, value;
    int type;

    public static ArrayList<HistoryItem> historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contacts);
        //recyclerView = findViewById(R.id.recyclerView);
        listView = findViewById(R.id.lv);
        historyItems = readSerializedObject();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShowContactsActivity.this);
        ext = sharedPreferences.getString(getResources().getString(R.string.key_extension), "");
        st = sharedPreferences.getString(getResources().getString(R.string.key_almacenamiento), "");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowContactsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ShowContactsActivity.this);
                final EditText input = new EditText(ShowContactsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setTitle(R.string.titleBuilder);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                if (!input.getText().toString().trim().equals("")) {
                                    Date date = Calendar.getInstance().getTime();
                                    writeFile(input.getText().toString(), textoArchivo(listContacts(getContactList())));
                                    String label = input.getText().toString() + ext + "\n"
                                            + getResources().getString(R.string.info_saved) + " " + st + "\n"
                                            + getResources().getString(R.string.info_date) + " " + date.toString();
                                    HistoryItem historyItem = new HistoryItem(label, input.getText().toString(), ext, st, date);
                                    historyItems.add(historyItem);
                                    serializeObjects();
                                    //readFile(input.getText().toString() + ext);
                                    Toast.makeText(ShowContactsActivity.this,
                                            R.string.saved,
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ShowContactsActivity.this,
                                            R.string.nameEmpty,
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(ShowContactsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.show();
            }
        });

        setArrayAdapter();
        //recyclerView.setAdapter(arrayAdapter);
        listView.setAdapter(arrayAdapter);
    }

    public String showHistoryItem(HistoryItem historyItem){
        String salida = "";
        salida += historyItem.getName() + " | " + historyItem.getExtension() + " | "
                + historyItem.getStorageType() + " | " + historyItem.getDate().toString();
        return salida;
    }

    public void serializeObjects() {
        try {
            FileOutputStream fos = ShowContactsActivity.this.openFileOutput(getResources().getString(R.string.serializedObjectsFile), Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(historyItems);
            oos.close();
            fos.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public ArrayList<HistoryItem> readSerializedObject() {
        ArrayList<HistoryItem> historyItemArrayList = new ArrayList<>();
        try {
            FileInputStream fis = ShowContactsActivity.this.openFileInput(getResources().getString(R.string.serializedObjectsFile));
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

    private boolean isValues(String name) {
        type = getCheckedType();
        return !(name.isEmpty() || type == NONE); // Devuelve false si está vacío o si es -1
    }

    private void writeFile(String name, String valor) {
        value = valor;
        if (isValues(name) && !value.isEmpty()) {
                writeNotes(name + ext);
        }
    }

    private void writeNotes(String name) {
        File f = new File(getFile(ShowContactsActivity.this, type), name);
        Log.v(getResources().getString(R.string.info_saved), f.getAbsolutePath());
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(value);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getCheckedType() {
        int tipo = NONE;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storageType = sharedPreferences.getString(getResources().getString(R.string.key_almacenamiento), "");
        if (storageType.equals(getResources().getString(R.string.INTERN))) {
            tipo = INTERN;
        } else if (storageType.equals(getResources().getString(R.string.PUBLIC))) {
            tipo = PUBLIC;
        } else if (storageType.equals(getResources().getString(R.string.PRIVATE))) {
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

    private static File getSerializedObjFile(Context context) {
        File file = context.getFilesDir();
        return file;
    }

    public void setArrayAdapter() {
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listContacts(getContactList()));
    }

    public String textoArchivo(ArrayList<String> archivo) {
        String salida = "";
        for (int i = 0; i < archivo.size(); i++) {
            salida += archivo.get(i) + "\n";
        }
        return salida;
    }

    public ArrayList<String> listContacts(ArrayList<Contacto> contactos) {
        ArrayList<String> c = new ArrayList<>();
        for (int i = 0; i < contactos.size(); i++) {
            String salida = "";
            salida += contactos.get(i).getNombre()+ ": \n";
            salida += contactos.get(i).getTelefonos() + "\n";
            String s = salida.substring(0, salida.length() - 3);
            c.add(s);
        }
        return c;
    }

    public ArrayList<Contacto> getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        ArrayList<Contacto> contactoArrayList = new ArrayList<>();

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {

                String id = "";
                String name = "";
                String telefonos = "";

                id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        telefonos += phoneNo + ", ";
                        //Log.i(TAG, "Name: " + name);
                        //Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
                Contacto contacto = new Contacto(id, name, telefonos);
                contactoArrayList.add(contacto);
            }
        }
        if(cur!=null){
            cur.close();
        }
        return contactoArrayList;
    }

}
