package com.example.myapplication.ui.milista;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Miadaptador2;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Map;

public class DashboardFragment extends Fragment {
    public static boolean isActionMode=false;
    public static ActionMode actionMode=null;
    public static ArrayList<Contacto> seleccionUsuario= new ArrayList<>();

    public static final int OTRA_VENTANA=123;
    private Miadaptador2 adaptador;
    private ListView listaEmergencia;
    public ArrayList<Contacto> contactos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        obtenerContactos(contactos);
        adaptador= new Miadaptador2(getContext(),contactos);
        listaEmergencia = root.findViewById(R.id.lista_emergencia);
        listaEmergencia.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listaEmergencia.setMultiChoiceModeListener(modeListener);
        listaEmergencia.setAdapter(adaptador);




        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.contactos,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.contacto:
                Intent i = new Intent(getActivity(), SeleccionarContactos.class);
                startActivityForResult(i,OTRA_VENTANA);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void obtenerContactos(ArrayList<Contacto> lista){
        lista.clear();
        SharedPreferences preferencia= getActivity().getSharedPreferences("lista_contactos",getContext().MODE_PRIVATE);
        Map<String, ?> todo = preferencia.getAll();
        for (Map.Entry <String,?> entrada:todo.entrySet()){
            lista.add(new Contacto(entrada.getKey(),entrada.getValue().toString(),false));
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==OTRA_VENTANA){
            obtenerContactos(contactos);
            adaptador.notifyDataSetChanged();
        }
    }

    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_eliminar,menu);
            isActionMode=true;
            actionMode=mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.eliminar:
                    adaptador.removeItem(seleccionUsuario);
                    borrarPreferencias(seleccionUsuario);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isActionMode=false;
            actionMode=null;

            seleccionUsuario.clear();
        }
    };

    public void borrarPreferencias(ArrayList<Contacto>lista){
        Context context=getContext();
        SharedPreferences preferences= context.getSharedPreferences("lista_contactos",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (Contacto contacto:lista){
            editor.remove(contacto.getNombre());
        }
         editor.apply();
    }
}
