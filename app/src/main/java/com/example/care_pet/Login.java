package com.example.care_pet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextInputLayout tUsuario, tPassword;
    ImageButton btn_ingresar;
    SharedPreferences sesion;
    CheckBox cbox_aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tUsuario = (TextInputLayout) findViewById(R.id.txtUsuario);
        tPassword = (TextInputLayout) findViewById(R.id.txtPassword);
        btn_ingresar = findViewById(R.id.btn_ingresar);
        cbox_aceptar = findViewById(R.id.cbox_aceptar);

        sesion = getSharedPreferences("sesion",0);

        btn_ingresar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (cbox_aceptar.isChecked()){
                login();
                }else{
                    Toast.makeText(Login.this, "Debe de aceptar Termninos y Condiciones", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login() {
        String url = Uri.parse(Config.URL + "login.php")
                .buildUpon()
                .appendQueryParameter("user",tUsuario.getEditText().getText().toString())
                .appendQueryParameter("pass",tPassword.getEditText().getText().toString())
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuesta(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Server", error.getMessage());
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void respuesta(JSONObject response) {
        try{
            if (response.getString("login").compareTo("y")==0){
                String jwt = response.getString("token");
                SharedPreferences.Editor editor = sesion.edit();
                editor.putString("user",tUsuario.getEditText().getText().toString());
                editor.putString("token", jwt);
                editor.commit();
                startActivity(new Intent(this,Principal.class));
            }else{
                Toast.makeText(this, "Error de usuario o contraseña", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){ }
    }
}