package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapplication.ui.milista.Contacto;

import java.util.ArrayList;
import java.util.Locale;


//Esta es una clase adaptador para manejar las listas, esta medio raro porque todavia no entiendo mjuy bien como funciona
public class Miadaptador extends BaseAdapter  {
    private Context context;
    private ArrayList<Contacto> datosOriginal;
    private ArrayList<Contacto> arraylist=null;
  /*  private ArrayList<MyBean> myList;  // for loading main list
    private ArrayList<MyBean> arraylist=null;*/

    public Miadaptador(Context context, ArrayList<Contacto> datos) {
        this.context = context;
        this.datosOriginal = datos;
        this.arraylist= new ArrayList<Contacto>();
        this.arraylist.addAll(datosOriginal);

    }

    @Override
    public int getCount() {
        return datosOriginal.size();
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

        nombre.setText(datosOriginal.get(position).getNombre());
        numero.setText(datosOriginal.get(position).getNumero());

        checkbox.setChecked(datosOriginal.get(position).isCheck());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        datosOriginal.clear();
        if (charText.length() == 0) {
            datosOriginal.addAll(arraylist);
        }
        else
        {
            for (Contacto wp : arraylist) {
                if (wp.getNombre().toLowerCase(Locale.getDefault()).contains(charText)) {
                    datosOriginal.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

