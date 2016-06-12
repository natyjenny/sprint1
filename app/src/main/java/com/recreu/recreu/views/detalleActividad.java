package com.recreu.recreu.views;

import android.app.Fragment;
//import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.recreu.recreu.Modelos.Actividad;
import com.recreu.recreu.Modelos.Usuario;
import com.recreu.recreu.controllers.HttpDelete;
import com.recreu.recreu.controllers.HttpGet;
import com.recreu.recreu.controllers.HttpPost;
import com.recreu.recreu.utilities.AccesoDirecto;
import com.recreu.recreu.utilities.JsonHandler;
import com.recreu.recreu.utilities.SystemUtilities;

import java.util.ArrayList;

import cl.recreu.recreu.taller_android_bd.R;

public class detalleActividad extends Fragment implements View.OnClickListener {
    private TextView cajaTitutlo;
    private TextView cajaCuerpo;
    private TextView cajaRequisitos;
    private Actividad actividad;
    private boolean participando;
    private int resultadoConsulta;
    private BroadcastReceiver br = null;
    private Button botonPC;
    private Usuario usuario;
    private Usuario[] listaUsuarios;
    private String URL_PUT_ACTIVIDAD;
    private ListView listaParticipantes;


    public detalleActividad(Actividad act, Usuario usu) {
            this.actividad=act;
            this.usuario=usu;
            URL_PUT_ACTIVIDAD = (new AccesoDirecto()).getURL();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detalle_actividad, container, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        cajaTitutlo = ((TextView) getView().findViewById(R.id.TextViewTitulo));
        cajaTitutlo.setText(actividad.getTitulo());
        cajaCuerpo = ((TextView) getView().findViewById(R.id.TextViewCuerpo));
        cajaCuerpo.setText(actividad.getCuerpo());
        cajaRequisitos = ((TextView) getView().findViewById(R.id.TextViewRequisitos));
        cajaRequisitos.setText(actividad.getRequerimientos());
        listaParticipantes=(ListView)getView().findViewById(R.id.listaParticipantes);
        botonPC = ((Button) getView().findViewById(R.id.botonPC));
        botonPC.setOnClickListener(this);

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {

        System.out.println("act: "+actividad.getActividadId()+". usuario: "+usuario.getUsuarioId());
        String URL_USUARIOS_EN_ACTIVIDAD=URL_PUT_ACTIVIDAD+"actividades/"+actividad.getActividadId()+"/usuarios";
        IntentFilter intentFilter2 = new IntentFilter("httpData");

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JsonHandler jh = new JsonHandler();
                listaUsuarios = jh.getIdesUsuariosEnAct(intent.getStringExtra("data"));
                participando = false;
                if (listaUsuarios != null){

              // se muestra la listaUsuarios en ListView
                    String[] StringUsuarios = new String[listaUsuarios.length];
                    for (int i=0;i<listaUsuarios.length;i++) {
                             StringUsuarios[i] = ""+listaUsuarios[i].getPrimerNombre() + " " + listaUsuarios[i].getApellidoPaterno() +"";
                    }
                    System.out.println(" string de usuarios antes de adapter:"+StringUsuarios);
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,StringUsuarios);
                    listaParticipantes.setAdapter(adapter);

                    participando = false;
                    for (int i = 0; i < listaUsuarios.length; i++) {
                        if (listaUsuarios[i].getUsuarioId() == usuario.getUsuarioId()){
                            participando = true;
                            listaParticipantes.addFooterView(getView(),listaUsuarios[i].getPrimerNombre(),true);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    if (participando == true) {
                        botonPC.setText("CANCELAR PARTICIPACIÓN");                      // HU: CANCELAR PARTICIPACION
                    } else {

                        resultadoConsulta = listaUsuarios.length;
                        if (actividad.getMaximoPersonas() > resultadoConsulta) {         //  HU : PARTICIPAR DE ACTIVIDAD
                            botonPC.setText("PARTICIPAR");
                        } else {
                            botonPC.setText("SIN CUPOS");
                            botonPC.setClickable(false);
                        }
                    }

                }
            }
        };


        getActivity().registerReceiver(br, intentFilter2);
        SystemUtilities su2 = new SystemUtilities(getActivity().getApplicationContext());
        if (su2.isNetworkAvailable()) {
            try {
                new HttpGet(getActivity().getApplicationContext()).execute(URL_USUARIOS_EN_ACTIVIDAD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }           super.onResume();
    }


    @Override
    public void onPause() {
        if (br != null) {
            getActivity().unregisterReceiver(br);
        }
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        String URL_PARTICIPACION = URL_PUT_ACTIVIDAD+"usuarios/"+usuario.getUsuarioId()+"/actividades/"+actividad.getActividadId()+"";

        // DELETE: borro fila usuario y actividad ( SI ES ORGANIZADOR HAY QUE "NOTIFICAR AL RESTO" y pedirle a otro que sea organizador
        if ( participando == true){
            try {
                SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
                if (su.isNetworkAvailable()) {
                    try {
                        AsyncTask resp = new HttpDelete(getActivity().getApplicationContext()).execute(URL_PARTICIPACION);
                        Toast.makeText(getActivity(), " Cancelando participacion  ...   ", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
            }

        }else{
            String nuevaParticipacion = "{\"usuarioId\":\""+usuario.getUsuarioId()+
                    ",\"actividadId\":"+actividad.getActividadId()+"}";
            try {
                SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
                if (su.isNetworkAvailable()) {
                    try {
                        AsyncTask resp = new HttpPost(getActivity().getApplicationContext()).execute(nuevaParticipacion,URL_PARTICIPACION);
                        Toast.makeText(getActivity(), " Confirmando Participacióm ...   ", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
            }
        }


  //      FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
  //      transaction.replace(R.id.fragment_container, new confirmacion(usuario),"confirmacion");
 //       new detalleActividad(actividad,usuario);
  //      transaction.addToBackStack(null);
 //       transaction.commit();

        // TODO: Agregar metodo OnItemListener para los usuarios y redirigir a perfil. Y ver donde esta la recursiva que hace que se cae a veces :'(

    }
}