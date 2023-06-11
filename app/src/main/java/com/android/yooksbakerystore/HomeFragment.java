package com.android.yooksbakerystore;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.yooksbakerystore.Product;
import com.android.yooksbakerystore.ProductAdapter;
import com.android.yooksbakerystore.R;
import com.android.yooksbakerystore.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AddProductToChartListener {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private static final String API_URL = "http://192.168.1.4:8000/api/tampilmenu"; // Ubah URL sesuai dengan endpoint tampilmenu di server Anda

    // Interface untuk menangani callback saat produk ditambahkan ke keranjang
//    public interface AddProductToChartListener {
//        void onAddProductToChart(Product product);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.menu_roti);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity(), productList, this );
        productAdapter.setAddProductToChartListener(this); // Set listener ke adapter
        recyclerView.setAdapter(productAdapter);

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray data = null;
                        try {
                            data = response.getJSONArray("data");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObject = data.getJSONObject(i);

                                int idProduk = dataObject.getInt("id_produk");
                                int idKategori = dataObject.getInt("id_kategori");
                                int idSupplier = dataObject.getInt("id_supplier");
                                int idToko = dataObject.getInt("id_toko");
                                int hargaJual = dataObject.getInt("jual_produk");
                                int hargaCoret = dataObject.getInt("harga_coret");
                                int stok = dataObject.getInt("stock_produk");
                                String nama = dataObject.getString("nama_produk");
                                String deskripsiProduk = dataObject.getString("keterangan_produk");
                                String fotoProduk = dataObject.getString("foto_produk");

                                Product product = new Product(idProduk, idKategori, idSupplier, idToko, hargaJual, hargaCoret, stok, nama, deskripsiProduk, fotoProduk);
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
                            Log.e(TAG, "onErrorResponse: " + errorMessage);
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

    @Override
    public void onAddProductToChart(Product product) {
        // Panggil metode onAddProductToChart di MainActivity dengan meneruskannya ke instance MainActivity yang ada
        if (getActivity() instanceof AddProductToChartListener) {
            AddProductToChartListener listener = (AddProductToChartListener) getActivity();
            listener.onAddProductToChart(product);
        }
    }

}
