package com.android.yooksbakerystore;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private TextView textRegister;
    private Button btnLogin;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_login);

        // Mendapatkan instance dari SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        etUsername = findViewById(R.id.input_email);
        etPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.text_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil Nilai dari EditText email dan password
                String email_user = etUsername.getText().toString();
                String password_user = etPassword.getText().toString();

                // Lakukan request ke server dengan menggunakan Volley
                MyServerRequest serverRequest = new MyServerRequest(LoginActivity.this);
                serverRequest.login(email_user, password_user, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Respon berhasil diterima, lakukan aksi yang diperlukan
                        Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();

                        // Set nilai shared preferences isLoggedIn menjadi true
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        // Arahkan ke halaman utama (HomeActivity)
                        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                        finish(); // Tutup activity ini agar pengguna tidak dapat kembali ke halaman login
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Terjadi Kesalahan" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
