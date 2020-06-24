package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapplication.ui.Contacto;

import java.util.ArrayList;

public class Miadaptador extends BaseAdapter {
    Context context;
    ArrayList<Contacto> datos;

    public Miadaptador(Context context, ArrayList<Contacto> datos) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.itemlista,parent,false);

        TextView nombre= convertView.findViewById(R.id.texto1);
        TextView numero= convertView.findViewById(R.id.texto2);
        CheckBox checkbox= convertView.findViewById(R.id.check);

        nombre.setText(datos.get(position).getNombre());
        numero.setText(datos.get(position).getNumero());

        checkbox.setChecked(datos.get(position).isCheck());

        return convertView;
    }
}
