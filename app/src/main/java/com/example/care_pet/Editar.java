package com.example.care_pet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Editar extends AppCompatActivity {
    ImageButton btn_guardarEdit;
    ImageButton btn_cancelarEdit;

    EditText editPersonaAgendo;
    EditText editHoraAgendo;
    EditText editaCargasAgendo;
    EditText editCargasTipo;
    EditText idEdit;

    SharedPreferences sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        btn_guardarEdit = findViewById(R.id.btn_guardarEdit);
        btn_cancelarEdit = findViewById(R.id.btn_cancelarEdit);

        editPersonaAgendo = findViewById(R.id.editPersonaAgendo);
        editHoraAgendo = findViewById(R.id.editHoraAgendo);
        editaCargasAgendo = findViewById(R.id.editaCargasAgendo);
        editCargasTipo = findViewById(R.id.editCargasTipo);
        idEdit = findViewById(R.id.editCargasDos);

        sesion = getSharedPreferences("sesion",0);
        getSupportActionBar().setTitle("Modificar : "+sesion.getString("user",""));

        Bundle datos = this.getIntent().getExtras();
        String id = datos.getString("id");
        String nombre = datos.getString("nombre");
        String hora = datos.getString("hora");
        String extra = datos.getString("extra");
        String tipo = datos.getString("tipo");

        editPersonaAgendo.setText(nombre);
        editHoraAgendo.setText(hora);
        editaCargasAgendo.setText(extra);
        editCargasTipo.setText(tipo);
        idEdit.setText(id);

        btn_cancelarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Editar.this, Agenda.class));
            }
        });

        btn_guardarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });
    }

    private void guardar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon()
                .appendQueryParameter("id", idEdit.getText().toString())
                .appendQueryParameter("nombre", editPersonaAgendo.getText().toString())
                .appendQueryParameter("hora", editHoraAgendo.getText().toString())
                .appendQueryParameter("extra", editaCargasAgendo.getText().toString())
                .appendQueryParameter("tipo", editCargasTipo.getText().toString())
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuestaGuardar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Editar.this, "Error de red", Toast.LENGTH_SHORT).show();
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

    private void respuestaGuardar(JSONObject response) {
        try{
            if (response.getString("update").compareTo("y") == 0){
                Toast.makeText(this, "Agenda Actualizada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Editar.this, Agenda.class));
            }else{
                Toast.makeText(this, "No se pueden guardar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }
}