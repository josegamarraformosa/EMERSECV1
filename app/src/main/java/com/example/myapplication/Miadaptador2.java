package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.myapplication.ui.milista.Contacto;
import com.example.myapplication.ui.milista.DashboardFragment;

import java.util.ArrayList;


//esta es otra clase adaptador, esta mal que haya dos ya sé... Pero metodologia agil xd
public class Miadaptador2 extends BaseAdapter {
    Context context;
    ArrayList<Contacto> datos;

    public Miadaptador2(Context context, ArrayList<Contacto> datos) {
        this.context = context;
        this.datos = datos;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_lista_emergencia,parent,false);

        TextView nombre= convertView.findViewById(R.id.texto1);
        TextView numero= convertView.findViewById(R.id.texto2);
        CheckBox checkbox= convertView.findViewById(R.id.check);

        checkbox.setTag(position);


        nombre.setText(datos.get(position).getNombre());
        numero.setText(datos.get(position).getNumero());

        if (DashboardFragment.isActionMode){
            checkbox.setVisibility(View.VISIBLE);

        } else {
            checkbox.setVisibility(View.GONE);
        }

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position= (int)buttonView.getTag();
                if(DashboardFragment.seleccionUsuario.contains(datos.get(position))){
                    DashboardFragment.seleccionUsuario.remove(datos.get(position));
                }else
                    DashboardFragment.seleccionUsuario.add(datos.get(position));
                   DashboardFragment.actionMode.setTitle(DashboardFragment.seleccionUsuario.size()+"");
            }
        });
        checkbox.setChecked(datos.get(position).isCheck());


        return convertView;
    }

    public void removeItem (ArrayList<Contacto> lista){


        for (Contacto contacto: lista){
            datos.remove(contacto);

        }
        notifyDataSetChanged();
    }


}
