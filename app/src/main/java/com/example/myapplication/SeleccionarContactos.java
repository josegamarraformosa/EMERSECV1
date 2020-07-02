package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.myapplication.ui.Contacto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;


public class SeleccionarContactos extends AppCompatActivity {

    Miadaptador adaptador;
    ArrayList<Contacto> contactos;
    private ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_contactos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar a la Lista");


        lista=findViewById(R.id.lista_contactos);


       contactos=obtenerContactos();
       adaptador = new Miadaptador(getApplicationContext(),contactos);


        lista.setAdapter(adaptador);


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox ch=view.findViewById(R.id.check);
                ch.setChecked(!contactos.get(position).isCheck());
                contactos.get(position).setCheck(ch.isChecked());
            }
        });




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buscar,menu);
        MenuItem menuItem= menu.findItem(R.id.buscar);
       SearchView searchView =(SearchView) MenuItemCompat.getActionView(menuItem);
       searchView.setQueryHint("Buscar contactos");


       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {

               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               adaptador.filter(newText);

               return false;
           }
       });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.ok:
                SharedPreferences preferencia= getSharedPreferences("lista_contactos",MODE_PRIVATE);
                Map <String,?> caca =preferencia.getAll();

                SharedPreferences.Editor editor = preferencia.edit();
                int contador=0;
                for (int i=0;i<contactos.size();i++){
                    if (contactos.get(i).isCheck()){
                        String nombre = contactos.get(i).getNombre();
                        String numero = contactos.get(i).getNumero();
                        editor.putString(nombre,numero);
                         contador++;
                    }
                }
                if ((caca.size() + contador)>5 ) {
                    Toast.makeText(getApplicationContext(),"La LISTA DE EMERGENCIA no puede contener mas de 5 contactos",Toast.LENGTH_SHORT).show();
                } else {
                    editor.apply();
                }


                finish();
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }





    private ArrayList<Contacto> obtenerContactos() {
        ArrayList<Contacto> lista= new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(SeleccionarContactos.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_CONTACTS},1);
        } else {
            Cursor cursor= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,null,
                    null,"upper(" +ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

            HashSet <String> hash= new HashSet<>();
            while (cursor.moveToNext()){
                String nombre=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String numero=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (!hash.contains(nombre)){
                    hash.add(nombre);
                    lista.add(new Contacto(nombre,numero,false));
                }


            }



        }

        return lista;

    }
}
