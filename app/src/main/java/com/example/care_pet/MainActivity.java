package com.example.care_pet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btn_ingresar;
    ImageButton btn_registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_ingresar = findViewById(R.id.btn_ingresar);
        btn_registrar = findViewById(R.id.btn_registrar);

        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { respuesta(); }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registro();
            }
        });
    }

    private void registro() {
        startActivity(new Intent(this, Registro.class));
    }

    private void respuesta(){
        startActivity(new Intent(this, Login.class));
    }

}