package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.myapplication.ui.Contacto;

import java.util.ArrayList;
import java.util.Map;

public class Emersec {
    private String nombreUsuario;
    private Activity activity;
    private ArrayList<Contacto> listaDeEmergencia = new ArrayList<>();
    public LocationManager ubicacion;


    public Emersec(Activity activity) {
        this.activity = activity;



    }
   public void enviarAlerta(String tipoDeEmergencia){
        actualizarlistaDeEmergencia();
        if (listaDeEmergencia.size()==0){
            Toast.makeText(activity, "No tiene contactos en su lista de emegerencia", Toast.LENGTH_SHORT).show();
            return;
        }

       if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(activity,new String[]{
                   Manifest.permission.SEND_SMS},1);
           Toast.makeText(activity,"Mensaje NO enviado.", Toast.LENGTH_SHORT).show();
       } else {
           SmsManager mensajeria= SmsManager.getDefault();

           String mensaje1 =  crearMensaje(tipoDeEmergencia);

           for (Contacto contacto:listaDeEmergencia){
               String numero= contacto.getNumero();

               mensajeria.sendTextMessage(numero,null,mensaje1,null,null);

           }
        Toast.makeText(activity,"Mensaje Enviado",Toast.LENGTH_SHORT).show();

       }

   }

    private String crearMensaje(String alerta) {

        Double lat= Math.round(MainActivity.ultimaUbicacion.getLatitude()*100000d) /100000d;
        Double  lon= Math.round(MainActivity.ultimaUbicacion.getLongitude()*100000d) /100000d;
        String mensaje = getNombreUsuario() + " esta sufriendo un problema de " + alerta + ". " +
                "Ver ubicacion: https://www.google.com/maps/dir/" +lat+","+lon;

        return mensaje;

    }

    public void add(Contacto contacto){
       listaDeEmergencia.add(contacto);
   }

   public void delete(Contacto contacto){
       listaDeEmergencia.remove(contacto);
   }

    public void setNombreUsuario(String nombreUsuario) {
        SharedPreferences preferencias = activity.getSharedPreferences("nombreUsuario",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("nombre",nombreUsuario);
        editor.apply();
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        SharedPreferences preferencias = activity.getSharedPreferences("nombreUsuario",Context.MODE_PRIVATE);
        return preferencias.getString("nombre","");
    }

    private void actualizarlistaDeEmergencia(){

       SharedPreferences preferencia= activity.getSharedPreferences("lista_contactos",activity.MODE_PRIVATE);
       Map<String, ?> todo = preferencia.getAll();
       for (Map.Entry <String,?> entrada:todo.entrySet()){
           listaDeEmergencia.add(new Contacto(entrada.getKey(),entrada.getValue().toString(),false));
       }
   }



}
