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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.Contacto;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

           try {
               registrarAlerta(tipoDeEmergencia);
           } catch (JSONException e) {
               e.printStackTrace();
           }
           Toast.makeText(activity,"Mensaje Enviado",Toast.LENGTH_SHORT).show();

       }

   }

    private String crearMensaje(String alerta) {

        Double lat= Math.round(MainActivity.ultimaUbicacion.getLatitude()*100000d) /100000d;
        Double  lon= Math.round(MainActivity.ultimaUbicacion.getLongitude()*100000d) /100000d;
        String nombre=getNombreUsuario();
        String mensaje = nombre + " esta sufriendo un problema de " + alerta + ". " +
                "Ver ubicacion: https://emersec.github.io/?lat="+lat+"&lon="+lon+"&n="+nombre;

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
        editor.putBoolean("registrado",true);
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

    private void registrarAlerta( String tipoDeEmergencia) throws JSONException {
        //averiguar fecha
        Calendar calendario= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("hh:mm:ss");
        String hora=simpleDateFormat.format(calendario.getTime());
        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String fecha=simpleDateFormat.format(calendario.getTime());


        String nombre= getNombreUsuario();

        JSONObject json=new JSONObject();

        //fecha y hora
        json.put("usuario",nombre);
        json.put("hora",hora);
        json.put("fecha",fecha);
        //tipo de emergencia
        json.put("tipo",tipoDeEmergencia);

        //ubicacion
        double lat= Math.round(MainActivity.ultimaUbicacion.getLatitude()*100000d) /100000d;
        double lon= Math.round(MainActivity.ultimaUbicacion.getLongitude()*100000d) /100000d;

        json.accumulate("ubicacion",String.valueOf(lat));
        json.accumulate("ubicacion",String.valueOf(lon));


        RequestQueue solicitud = Volley.newRequestQueue(activity);
        String url ="https://jsonbin.org/josegamarraformosa/emersec";
        JsonObjectRequest stringSolicitud= new JsonObjectRequest(Request.Method.PATCH, url,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(activity, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String,String> getHeaders () throws AuthFailureError {
                Map <String,String> header= new HashMap<String,String>();
                header.put("authorization","token a81b18d2-4616-4936-9c64-4ec43de6b493");
                return header;
            }
        };

        stringSolicitud.setShouldCache(false);
        solicitud.add(stringSolicitud);
    }




}
