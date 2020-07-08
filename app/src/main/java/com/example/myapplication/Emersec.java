package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.milista.Contacto;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


//esta clase maneja toda la logica de la aplicacion, los datos.

//--Establecer la lista de contactos de emergencia
//--Establecer el nombre del usuario
//--Mandar las alertas por SMS
//--Registrar la alerta
public class Emersec {
    private String nombreUsuario;
    private Activity activity;
    private ArrayList<Contacto> listaDeEmergencia = new ArrayList<>();
    public LocationManager ubicacion;


    public Emersec(Activity activity) {
        this.activity = activity;



    }
   public void enviarAlerta(String tipoDeEmergencia){
        //antes de enviar la alerta verificamos que tenga contactos en su lista de emergencia
        actualizarlistaDeEmergencia();
        if (listaDeEmergencia.size()==0){
            Toast.makeText(activity, "No tiene contactos en su lista de emegerencia", Toast.LENGTH_SHORT).show();
            return;
        }

        //verificamos que tenemos permiso para mandar SMS sino lo solicitamos
       if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(activity,new String[]{
                   Manifest.permission.SEND_SMS},1);
           Toast.makeText(activity,"Mensaje NO enviado.", Toast.LENGTH_SHORT).show();
       } else {

           SmsManager mensajeria= SmsManager.getDefault();

           //creamos la alerta que se mandara por SMS
           String mensaje1 =  crearMensaje(tipoDeEmergencia);

           //recorremos la lista de contactos de emergencia mandando la alerta a cada uno de ellos
           for (Contacto contacto:listaDeEmergencia){
               String numero= contacto.getNumero();

               mensajeria.sendTextMessage(numero,null,mensaje1,null,null);

           }

           try {
            //una vez mandada la alerta por SMS registramos la alerta en INTERNET
               registrarAlerta(tipoDeEmergencia);
           } catch (JSONException e) {
               e.printStackTrace();
           }
           Toast.makeText(activity,"Mensaje Enviado",Toast.LENGTH_SHORT).show();

       }

   }

    private String crearMensaje(String alerta) {
        //Creamos la cadena de texto que se mandara como SMS
        Double lat= Math.round(MainActivity.ultimaUbicacion.getLatitude()*100000d) /100000d;
        Double  lon= Math.round(MainActivity.ultimaUbicacion.getLongitude()*100000d) /100000d;
        String nombre=getNombreUsuario();
        String mensaje = nombre + " esta sufriendo un problema de " + alerta + ". " +
                "Ver ubicacion: https://emersec.github.io/?lat="+lat+"&lon="+lon+"&n="+nombre;

        return mensaje;

    }

    //Estos dos metodos no se para que estan pero no los borro por las dudas
    public void add(Contacto contacto){
       listaDeEmergencia.add(contacto);
   }

   public void delete(Contacto contacto){
       listaDeEmergencia.remove(contacto);
   }

   //Metodo para establecer o reestablecer el nombre de usuario
    public void setNombreUsuario(String nombreUsuario) {

        //entramos en los datos de preferencia del celular y registamos el usuario
        SharedPreferences preferencias = activity.getSharedPreferences("nombreUsuario",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("nombre",nombreUsuario);
        editor.putBoolean("registrado",true);
        editor.apply();
        this.nombreUsuario = nombreUsuario;
    }

    //obtenemos el nombre del usuario
    public String getNombreUsuario() {
        SharedPreferences preferencias = activity.getSharedPreferences("nombreUsuario",Context.MODE_PRIVATE);
        return preferencias.getString("nombre","");
    }

    //Metodo para actualizar la variable listaDeEmergencia de esta clase con las preferenciadas guardadas del celular
    private void actualizarlistaDeEmergencia(){
       SharedPreferences preferencia= activity.getSharedPreferences("lista_contactos",activity.MODE_PRIVATE);
       Map<String, ?> todo = preferencia.getAll();
       for (Map.Entry <String,?> entrada:todo.entrySet()){
           listaDeEmergencia.add(new Contacto(entrada.getKey(),entrada.getValue().toString(),false));
       }
   }

   //metodo para registrar la alerta en internet con un objeto JSON

    private void registrarAlerta( String tipoDeEmergencia) throws JSONException {
        //averiguar fecha y hora
        Calendar calendario= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("hh:mm:ss");
        String hora=simpleDateFormat.format(calendario.getTime());
        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String fecha=simpleDateFormat.format(calendario.getTime());


        String nombre= getNombreUsuario();

        //instanciamos un objeto JSON donde iremos guardando los distintos datos relacionaos con la alerta
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


        //Iniciamos una peticion HTTP y le mandamos el JSON de la alerta a registrar
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
