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

import org.json.JSONException;
import org.json.JSONObject;

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

        // Cek apakah pengguna sudah login sebelumnya
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Pengguna sudah login sebelumnya, arahkan ke halaman utama
            Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
            startActivity(intent);
            finish(); // Tutup activity ini agar pengguna tidak dapat kembali ke halaman login
        } else {
            // Pengguna belum login, tetapkan onClickListener ke btnLogin
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
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    JSONObject data = jsonResponse.getJSONObject("data");
                                    int userId = data.getInt("id");
                                    String username = data.getString("nama_user");

                                    // Set nilai shared preferences isLoggedIn menjadi true
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putInt("userId", userId);
                                    editor.putString("username", username);
                                    editor.apply();

                                    Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();

                                    // Arahkan ke halaman utama (HomeActivity)
                                    Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
                                    startActivity(intent);
                                    finish(); // Tutup activity ini agar pengguna tidak dapat kembali ke halaman login
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, "Terjadi Kesalahan" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
