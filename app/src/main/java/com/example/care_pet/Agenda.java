package com.example.care_pet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Agenda extends AppCompatActivity {

    Spinner spinnerAgenda;
    ImageButton btn_editar;
    ImageButton btn_regresar;
    ImageButton btn_cerrarsesion;
    ImageButton btn_eliminar;
    EditText perAgendo;
    EditText horaAgendo;
    EditText cargasAgendo;
    EditText cargasTipo;
    EditText cargasDos;
    SharedPreferences sesion;
    String nombres[];
    String lista[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        btn_editar = findViewById(R.id.btn_editar);
        btn_regresar = findViewById(R.id.btn_regresar);
        btn_cerrarsesion = findViewById(R.id.btn_cerrarsesion);
        btn_eliminar = findViewById(R.id.btn_eliminar);

        sesion = getSharedPreferences("sesion",0);
        spinnerAgenda = findViewById(R.id.spinnerAgenda);
        perAgendo = findViewById(R.id.perAgendo);
        horaAgendo = findViewById(R.id.horaAgendo);
        cargasAgendo = findViewById(R.id.cargasAgendo);
        cargasTipo = findViewById(R.id.cargasTipo);
        cargasDos = findViewById(R.id.cargasDos);
        llenar();


        spinnerAgenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String categoria = (String) spinnerAgenda.getSelectedItem().toString();
                llenarCosas(categoria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //No selecciono nada
            }
        });

        btn_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Agenda.this, Principal.class));
            }
        });

        btn_cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });

        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoria = (String) spinnerAgenda.getSelectedItem().toString();
                new AlertDialog.Builder(Agenda.this)
                        .setTitle("Eliminar")
                        .setMessage("¿Quieres eliminar la carga?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String tempDel = cargasDos.getText().toString();
                                eliminarCarga(tempDel);
                            }
                        })
                        .setNegativeButton("No",null)
                        .create().show();
            }
        });

        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempDel = cargasDos.getText().toString();
                Bundle extras = new Bundle();
                extras.putString("id",cargasDos.getText().toString());
                extras.putString("nombre",perAgendo.getText().toString());
                extras.putString("hora",horaAgendo.getText().toString());
                extras.putString("extra",cargasAgendo.getText().toString());
                extras.putString("tipo",cargasTipo.getText().toString());

                Intent i = new Intent(Agenda.this, Editar.class);
                i.putExtras(extras);
                startActivity(i);
            }
        });

    }

    private void cerrarSesion() {
        startActivity(new Intent(Agenda.this, Login.class));
    }

    private void llenarCosas(String cat) {
        for (int i=0; i< nombres.length; i++){
            if (lista[i][2].toString()==cat){
                cargasDos.setText(lista[i][0]);
                perAgendo.setText(lista[i][1]);
                horaAgendo.setText(lista[i][2]);
                cargasAgendo.setText(lista[i][3]);
                cargasTipo.setText(lista[i][4]);
            }
        }

    }

    private void llenar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon().build().toString();
        JsonArrayRequest peticion  = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        llenarLista(response);
                        llenarSpinner();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Agenda.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization",
                        sesion.getString("token", "Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void llenarSpinner() {
        spinnerAgenda.setAdapter(new ArrayAdapter<String>(Agenda.this, android.R.layout.simple_spinner_dropdown_item, nombres));
        System.out.println(nombres[0]);
    }

    private void llenarLista(JSONArray response) {
        try {
            nombres = new String[response.length()];
            lista = new String[response.length()][5];
            for (int i = 0; i < response.length(); i++) {
                lista[i][0] = response.getJSONObject(i).getString("id");
                lista[i][1] = response.getJSONObject(i).getString("nombre");
                lista[i][2] = response.getJSONObject(i).getString("hora");
                nombres[i] = response.getJSONObject(i).getString("hora");
                lista[i][3] = response.getJSONObject(i).getString("extra");
                lista[i][4] = response.getJSONObject(i).getString("tipo");
            }
        }catch (Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    private void eliminarCarga(String tempDel){
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon()
                .appendQueryParameter("id", tempDel)
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuestaEliminar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Agenda.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", sesion.getString("token", "Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void respuestaEliminar(JSONObject response) {
        try{
            if (response.getString("delete").compareTo("y") == 0){
                Toast.makeText(this, "Datos eliminados", Toast.LENGTH_SHORT).show();
                llenar();
            }else{
                Toast.makeText(this, "No se puede eliminar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }

}