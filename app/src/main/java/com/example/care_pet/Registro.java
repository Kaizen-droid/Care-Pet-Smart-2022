package com.example.care_pet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    TextInputLayout txtNombre, txtApellidos;
    TextInputLayout txtTelefono, txtCorreo;
    CheckBox gato, perro;
    CheckBox chica, mediana, grande;
    TextInputLayout txtPassword, txtConfPassword;
    ImageButton btn_cancelar, btn_registrar;
    SharedPreferences sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = (TextInputLayout) findViewById(R.id.txtNombre);
        txtApellidos = (TextInputLayout) findViewById(R.id.txtApellidos);
        txtTelefono = (TextInputLayout) findViewById(R.id.txtTelefono);
        txtCorreo = (TextInputLayout) findViewById(R.id.txtCorreo);
        txtPassword = (TextInputLayout) findViewById(R.id.txtPassword);
        txtConfPassword = (TextInputLayout) findViewById(R.id.txtConfPassword);
        String nombre = txtNombre.getEditText().getText().toString();
        String apellidos = txtApellidos.getEditText().getText().toString();
        gato = findViewById(R.id.gato);
        perro = findViewById(R.id.perro);
        chica = findViewById(R.id.chica);
        mediana = findViewById(R.id.mediana);
        grande = findViewById(R.id.grande);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        btn_registrar = findViewById(R.id.btn_registrar);

        gato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gato.isChecked()){
                    perro.setEnabled(true);
                }else
                    perro.setEnabled(false);
            }
        });

        perro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!perro.isChecked()){
                    gato.setEnabled(true);
                }else
                    gato.setEnabled(false);
            }
        });

        chica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chica.isChecked()){
                    mediana.setEnabled(true);
                    grande.setEnabled(true);
                }else{
                    mediana.setEnabled(false);
                    grande.setEnabled(false);}
            }
        });

        mediana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediana.isChecked()){
                    chica.setEnabled(true);
                    grande.setEnabled(true);
                }else{
                    chica.setEnabled(false);
                    grande.setEnabled(false);}
            }
        });

        grande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!grande.isChecked()){
                    chica.setEnabled(true);
                    mediana.setEnabled(true);
                }else{
                    chica.setEnabled(false);
                    mediana.setEnabled(false);}
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (txtNombre.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(Registro.this, "Nombre Vacio", Toast.LENGTH_SHORT).show();
                    }else if (txtApellidos.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(Registro.this, "Apellidos Vacio", Toast.LENGTH_SHORT).show();
                    }else if (txtTelefono.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(Registro.this, "No. Telefono Vacio", Toast.LENGTH_SHORT).show();
                    }else if (txtCorreo.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(Registro.this, "Correo Vacio", Toast.LENGTH_SHORT).show();
                    }else if (txtPassword.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(Registro.this, "Contraseña Vacia", Toast.LENGTH_SHORT).show();
                    }else if (txtPassword.getEditText().getText().toString().equals(txtConfPassword.getEditText().getText().toString())){
                        if (gato.isChecked() || perro.isChecked()){
                            if (chica.isChecked() || mediana.isChecked() || grande.isChecked()){
                                registro();
                            }else{
                                Toast.makeText(Registro.this, "Selecciona la raza", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Registro.this, "Selecciona la mascota", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Registro.this, "Contraseñas Incorrectas", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login.class));
            }
        });
    }

    private void registro() {
        String url = Uri.parse(Config.URL + "regusuario.php")
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
                Toast.makeText(Registro.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("nombre", txtNombre.getEditText().getText().toString());
                params.put("apellidos", txtApellidos.getEditText().getText().toString());
                params.put("telefono", txtTelefono.getEditText().getText().toString());
                params.put("user", txtCorreo.getEditText().getText().toString());
                if (gato.isChecked()){
                    params.put("mascota", gato.getText().toString());
                }if (perro.isChecked()){
                    params.put("mascota", perro.getText().toString());
                }
                if (chica.isChecked()){
                    params.put("raza", chica.getText().toString());
                }
                if (mediana.isChecked()){
                    params.put("raza", mediana.getText().toString());
                }
                if (grande.isChecked()){
                    params.put("raza", grande.getText().toString());
                }
                params.put("pass", txtPassword.getEditText().getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void agregarRespuesta(String response) {
        try{
            JSONObject r = new JSONObject(response);
            if(r.getString("add").compareTo("y")==0){
                Toast.makeText(Registro.this, "Registrado correctamente " + r.getString("id"), Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(Registro.this, "Error no se pudo registrar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }

}