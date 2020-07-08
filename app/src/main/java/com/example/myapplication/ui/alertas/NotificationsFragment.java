package com.example.myapplication.ui.alertas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {
    GoogleMap map;
    JSONArray alertas;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        traerDatos();
        setHasOptionsMenu(true);
        return root;
    }
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map=googleMap;
            //una vez que un mapa termina de cargar hacemos zoom en formosa capital
            LatLng formosa = new LatLng(-26.1775303, -58.1781387);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(formosa,13f));
        }
    };



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.mapa,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void traerDatos(){
        //traemos las alertas con una solititud HTTP
        RequestQueue solicitud = Volley.newRequestQueue(getContext());
        String url ="https://jsonbin.org/josegamarraformosa/emersec";
        StringRequest stringSolicitud= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    alertas=new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.violencia:
                try {
                    filtrarAlertas("violencia");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.inseguridad:
                try {
                    filtrarAlertas("inseguridad");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.salud:
                try {
                    filtrarAlertas("salud");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.todas:
                try {
                    mostrarTodasLasAlertas();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void filtrarAlertas(String tipoDeAlerta) throws JSONException {

        //si el mapa ya esta cargado podemos filtrarlo sino no
        if (map!=null){
            //limpiamos el mapá de cualquier marcador que contenga
            map.clear();
            //Se establece el color del marcador segun el tipo de violencia
            BitmapDescriptor colorMarcador;
            switch (tipoDeAlerta){
                case "inseguridad":
                    colorMarcador=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case "violencia":
                    colorMarcador=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                    break;
                default:
                    colorMarcador=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            }


            JSONObject alerta;
            //recorremos todas las alertas registradas
            for(int i=0;i<alertas.length();i++){

                alerta=alertas.getJSONObject(i);
                String tipo= alerta.getString("tipo");

                //filtramos las alertas por el tipo deseado y la colocamos en el mapa
                if(tipo.equalsIgnoreCase(tipoDeAlerta)){
                    JSONArray ubicacion=alerta.getJSONArray("ubicacion");
                    LatLng latlng= new LatLng(ubicacion.getDouble(0),ubicacion.getDouble(1));
                    map.addMarker(new MarkerOptions().position(latlng).icon(colorMarcador).title(tipoDeAlerta).snippet("jeje caca"));
                }

            }


        } else {
            Toast.makeText(getContext(),"Espere a que el mapa termine de cargar",Toast.LENGTH_SHORT);
        }


    }
    public void mostrarTodasLasAlertas() throws JSONException {
        //muestra todas las alertas en el mapa
        //verificamos que el mapa ya esta cargado
        if (map!=null){
            map.clear();

            JSONObject alerta;

            for(int i=0;i<alertas.length();i++){

                alerta=alertas.getJSONObject(i);
                String tipoDeAlerta= alerta.getString("tipo");

                //Se establece el color del marcador segun el tipo de violencia
                BitmapDescriptor colorMarcador;
                switch (tipoDeAlerta){
                    case "INSEGURIDAD":
                        colorMarcador=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        break;
                    case "VIOLENCIA":
                        colorMarcador=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        break;
                    default:
                        colorMarcador=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                }

                //obtenemos la latitud y longitud de la alerta para el colocarle al marcador
                    JSONArray ubicacion=alerta.getJSONArray("ubicacion");
                    LatLng latlng= new LatLng(ubicacion.getDouble(0),ubicacion.getDouble(1));

                    //agregamos el marcador en el mapa
                    map.addMarker(new MarkerOptions().position(latlng).icon(colorMarcador).title(tipoDeAlerta));


            }


        } else {
            Toast.makeText(getContext(),"Espere a que el mapa termine de cargar",Toast.LENGTH_SHORT);
        }
    }
}
