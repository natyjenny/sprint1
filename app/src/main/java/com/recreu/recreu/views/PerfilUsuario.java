package com.recreu.recreu.views;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.recreu.recreu.Modelos.Usuario;
import com.recreu.recreu.utilities.AccesoDirecto;



import cl.recreu.recreu.taller_android_bd.R;


public class PerfilUsuario extends Fragment implements View.OnClickListener {
    private final String URL_POST = (new AccesoDirecto()).getURL() + "actividades";
    private Context c;
    private Usuario usuarioPerfil, usuarioSesion;
    private View vistaPerfil;
    private TextView nombrePerfil;
    private TextView correoPerfil;
    private TextView carreraPerfil;
    private TextView cumpleanosPerfil;
    private TextView interesesPerfil;
    private TextView telefonoPerfil;
    private Button activOrganizador, activParticipante;


    public PerfilUsuario(Usuario usuSesion, Usuario usuPerfil) {
        this.usuarioPerfil=usuSesion;
        this.usuarioPerfil = usuPerfil;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        vistaPerfil = inflater.inflate(R.layout.perfil_usuario, container, false);
        nombrePerfil = (TextView) vistaPerfil.findViewById(R.id.nombrePerfil);
        correoPerfil = (TextView) vistaPerfil.findViewById(R.id.correoPerfil);
        carreraPerfil = (TextView) vistaPerfil.findViewById(R.id.carreraPerfil);
        cumpleanosPerfil = (TextView) vistaPerfil.findViewById(R.id.cumpleanosPerfil);
        interesesPerfil = (TextView) vistaPerfil.findViewById(R.id.interesesPerfil);
        telefonoPerfil = (TextView) vistaPerfil.findViewById(R.id.telefonoPerfil);
        activOrganizador=(Button)vistaPerfil.findViewById(R.id.actividadesOrganizador);
        activOrganizador.setOnClickListener(this);
        activParticipante=(Button)vistaPerfil.findViewById(R.id.actividadesParticipante);
        activParticipante.setOnClickListener(this);

        //Set
        nombrePerfil.setText(usuarioPerfil.getPrimerNombre() + " " + usuarioPerfil.getApellidoPaterno() + "\n" + usuarioPerfil.getApellidoMaterno());
        correoPerfil.setText(usuarioPerfil.getCorreo() + "@usach.cl");
        //carreraPerfil.setText(usuarioPerfil.getCarrera().getNombreCarrera());
        cumpleanosPerfil.setText(usuarioPerfil.getFechaNacimiento());
        //interesesPerfil.setText(usuarioPerfil.getIntereses());
        //telefonoPerfil.setText(usuarioPerfil.getNumeroTelefono());
        return vistaPerfil;
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction transaccion;
        switch (v.getId()) {
            case R.id.actividadesOrganizador:
                transaccion = getFragmentManager().beginTransaction();
                transaccion.replace(R.id.fragment_container, new Explorar(usuarioSesion, usuarioPerfil.getId(), true), "explorar");
                new Principal();
                transaccion.addToBackStack(null);
                transaccion.commit();
                break;

            case R.id.actividadesParticipante:
                transaccion = getFragmentManager().beginTransaction();
                transaccion.replace(R.id.fragment_container, new Explorar(usuarioSesion, usuarioPerfil.getId(), false), "explorar");
                new Principal();
                transaccion.addToBackStack(null);
                transaccion.commit();
                break;
        }

    }
}