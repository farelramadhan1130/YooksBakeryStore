package com.android.yooksbakerystore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyServerRequest {
    private static final String TAG = "MyServerRequest";
    private final Context context;
    private final RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    public MyServerRequest(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void login(String email, String password, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        // URL endpoint untuk login
        String url = "http://192.168.1.6:8000/api/login";

        // membuat objek RequestQueue untuk mengirim request ke server
        RequestQueue queue = Volley.newRequestQueue(context);

        // membuat objek StringRequest untuk melakukan request POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                // response dari server jika login berhasil
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                Intent intent = new Intent(context, SplashScreenActivity.class);
                                context.startActivity(intent);
                            } else {
                                // response dari server jika login gagal
                                Toast.makeText(context, "Login gagal", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // response dari server jika terjadi kesalahan pada request atau response dari server
                        Toast.makeText(context, "Terjadi kesalahan pada server" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // menambahkan request ke dalam queue
        queue.add(stringRequest);
    }


}