package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.ui.botones.HomeFragment;


//Esta es la clase de registro del usuario. No se me ocurrió otro nombre mas representativo xd
public class Registro extends AppCompatActivity {

    private Button boton;
    private EditText textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        getSupportActionBar().setTitle("EmerSec");

        textView=findViewById(R.id.nombre);

        boton=findViewById(R.id.aceptar);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(),"Debe ingresar un nombre",Toast.LENGTH_SHORT).show();

                } else {
                   HomeFragment.emersec.setNombreUsuario(textView.getText().toString().toUpperCase());
                    finish();
                }

            }
        });


    }
}
