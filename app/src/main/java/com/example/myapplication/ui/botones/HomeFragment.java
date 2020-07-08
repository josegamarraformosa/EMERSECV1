package com.example.myapplication.ui.botones;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Emersec;
import com.example.myapplication.R;
import com.example.myapplication.Registro;

public class HomeFragment extends Fragment {

    Button buttonInseguridad,buttonViolencia,buttonSalud;
    public static Emersec emersec;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        emersec = new Emersec(getActivity());

        buttonInseguridad=(Button) root.findViewById(R.id.button);
        buttonViolencia= (Button) root.findViewById(R.id.abuso);
        buttonSalud= (Button) root.findViewById(R.id.salud);


        buttonInseguridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emersec.enviarAlerta("INSEGURIDAD");
            }
        });

        buttonViolencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emersec.enviarAlerta("VIOLENCIA");
            }
        });

        buttonSalud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emersec.enviarAlerta("SALUD");
            }
        });



        setHasOptionsMenu(true);
        return root;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_botones,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.restablecernombre:
                startActivity(new Intent(getContext(), Registro.class));
                return true;
            case R.id.menuinfo:
                new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_info_black)
                                                    .setTitle("Informacion")
                                                    .setMessage("Acá se supone que va el tutorial de como se usa la app xD")
                                                    .setCancelable(true).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
