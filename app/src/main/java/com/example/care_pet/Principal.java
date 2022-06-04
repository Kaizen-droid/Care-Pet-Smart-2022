package com.example.care_pet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Principal extends AppCompatActivity {

    TimePicker hora;
    EditText editTextTime;
    Spinner spinnerCargas;
    Spinner spinnerTipo;
    ImageButton btn_verAgendados;
    ImageButton btn_agendar;
    ImageButton btn_alimentar;
    ImageButton btn_cerrarsesion;
    ImageButton btn_acerca;
    SharedPreferences sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        sesion = getSharedPreferences("sesion",0);
        hora = (TimePicker)findViewById(R.id.hora);
        hora.setIs24HourView(true);

        spinnerCargas = findViewById(R.id.spinnerCargas);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        editTextTime = findViewById(R.id.editTextTime);

        btn_verAgendados = findViewById(R.id.btn_verAgendados);
        btn_agendar = findViewById(R.id.btn_agendar);
        btn_alimentar = findViewById(R.id.btn_alimentar);
        btn_cerrarsesion = findViewById(R.id.btn_cerrarsesion);
        btn_acerca = findViewById(R.id.btn_acerca);
        btn_alimentar.setEnabled(false);
        notificarCarga();

        btn_agendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int horas= hora.getCurrentHour();
                int min=hora.getCurrentMinute();
                editTextTime.setText(String.format("%02d:%02d",horas,min));
                if (spinnerCargas.getSelectedItem().toString().equals("Ninguna")){
                    Toast.makeText(Principal.this, "Selecciona cuantas cargas porfavor", Toast.LENGTH_SHORT).show();
                }else if (spinnerTipo.getSelectedItem().toString().equals("Ninguno")){
                    Toast.makeText(Principal.this, "Selecciona que tipo de cargas porfavor", Toast.LENGTH_SHORT).show();
                }else {
                    agregar();
                }
            }
        });

        btn_verAgendados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { respuesta(); }
        });

        btn_cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Principal.this, Login.class));
            }
        });

        btn_alimentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realizarCarga();
                Toast.makeText(Principal.this, "Listo!!!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_acerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Principal.this, "App developed by Dagr Kaizen ;3", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void realizarCarga() {
        String url = Uri.parse(Config.URL + "sensor.php")
                .buildUpon()
                .appendQueryParameter("tipo", spinnerTipo.getSelectedItem().toString())
                .appendQueryParameter("alimentar", "si")
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Principal.this, "Alimentado su majestad :)", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Principal.this, "Error de red", Toast.LENGTH_SHORT).show();
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

    private void agregar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon().build().toString();
        StringRequest peticion  = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        agregarRespuesta(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Error", error.getMessage());
                Toast.makeText(Principal.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization",
                        sesion.getString("token", "Error"));
                return header;
            }
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("hora", editTextTime.getText().toString());
                params.put("extra", spinnerCargas.getSelectedItem().toString());
                params.put("tipo", spinnerTipo.getSelectedItem().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void agregarRespuesta(String response) {
        try{
            JSONObject r = new JSONObject(response);
            if(r.getString("add").compareTo("y")==0){
                Toast.makeText(Principal.this, "Agendado correctamente ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Principal.this, "Error no se pudo agendar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }

    private void respuesta(){
        startActivity(new Intent(this, Agenda.class));
    }

    private void notificarCarga(){
        String url = Uri.parse(Config.URL + "sensor.php")
                .buildUpon()
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuestaNotificar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Principal.this, "Error de red", Toast.LENGTH_SHORT).show();
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

    private void respuestaNotificar(JSONObject response) {
        try{
            if (response.getString("resultado").compareTo("1") == 0){
                Toast.makeText(this, "Tengo hambre :c", Toast.LENGTH_SHORT).show();
                btn_alimentar.setEnabled(true);
            }
        }catch (Exception e){}
    }

}