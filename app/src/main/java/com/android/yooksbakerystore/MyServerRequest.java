package com.android.yooksbakerystore;

import android.content.Context;
import android.content.Intent;
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

    public MyServerRequest(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void login(String username, String password, Response.Listener<String> successListener) {
        // URL endpoint untuk login
        String url = "http://yooksbakery.wsmif3a.id/public/api/login";

        // membuat objek RequestQueue untuk mengirim request ke server
        RequestQueue queue = Volley.newRequestQueue(context);

        // membuat objek StringRequest untuk melakukan request POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean error = jsonResponse.getBoolean("success");
                            if (error) {
                                // response dari server jika login berhasil
                                Intent intent = new Intent(context, HomeFragment.class);
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
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", username);
                params.put("password", password);
                return params;
            }
        };
        // menambahkan request ke dalam queue
        queue.add(stringRequest);
    }
}
