package com.example.myapplication.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.Emersec;
import com.example.myapplication.R;

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
}
