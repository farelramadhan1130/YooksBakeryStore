package com.android.yooksbakerystore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.yooksbakerystore.Product;
import com.android.yooksbakerystore.ProductAdapter;
import com.android.yooksbakerystore.R;
import com.android.yooksbakerystore.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private static final String API_URL = "http://192.168.1.3:8000/api/tampilmenu"; // Ubah URL sesuai dengan endpoint tampilmenu di server Anda

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.menu_roti);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity(), productList);
        recyclerView.setAdapter(productAdapter);

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject productObject = response.getJSONObject(i);
                                int idProduk = productObject.getInt("id_produk");
                                int hargaJual = productObject.getInt("harga_jual");
                                int hargaCoret = productObject.getInt("harga_coret");
                                int stok = productObject.getInt("stok");
                                String nama = productObject.getString("nama");
                                String deskripsiProduk = productObject.getString("deskripsi_produk");
                                String fotoProduk = productObject.getString("foto_produk");

                                Product product = new Product(idProduk, hargaJual, hargaCoret, stok, nama, deskripsiProduk, fotoProduk);
                                productList.add(product);
                            }

                            productAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error fetching products";
                        if (error != null && error.getMessage() != null) {
                            errorMessage += ": " + error.getMessage();
                        }
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            VolleySingleton volleySingleton = VolleySingleton.getInstance(getActivity());
            if (volleySingleton != null) {
                volleySingleton.addToRequestQueue(request);
        }
    }

    // Tambahkan metode ini untuk mengambil URL gambar dan memuatnya ke ImageView menggunakan Picasso
    private void loadProductImage(String imageUrl, ImageView imageView) {
        Picasso.get().load(imageUrl).into(imageView);
    }
}
