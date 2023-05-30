package com.android.yooksbakerystore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Product> productList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = new ArrayList<>();
        fetchDataFromAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void fetchDataFromAPI() {
        String url = "http://10.10.5.184:8000/api/tampilmenu";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject productObj = response.getJSONObject(i);
                                int id_produk = Integer.parseInt(productObj.getString("id_produk"));
                                int harga_jual = Integer.parseInt(productObj.getString("harga_jual"));
                                int harga_coret = Integer.parseInt(productObj.getString("harga_coret"));
                                int stok = Integer.parseInt(productObj.getString("stok"));
                                String nama = productObj.getString("nama");
                                String deskripsi_produk = productObj.getString("deskripsi_produk");
                                String foto_produk = productObj.getString("foto_produk");

                                // Buat objek Product dari data yang diterima
                                Product product = new Product(id_produk, harga_jual, harga_coret, stok, nama, deskripsi_produk, foto_produk );
                                productList.add(product);
                            }

                            // Panggil metode untuk menampilkan data produk di UI
                            displayProducts();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void displayProducts() {
        ListView listView = getView().findViewById(R.id.menu_roti);

        ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(getContext(), R.layout.menu_roti, productList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_roti, parent, false);
                }

                Product product = getItem(position);

                TextView namaRotiTextView = convertView.findViewById(R.id.nama_roti);
                TextView deskripsiRotiTextView = convertView.findViewById(R.id.deskripsi_roti);
                TextView hargaRotiTextView = convertView.findViewById(R.id.harga_roti);
                TextView diskonRotiTextView = convertView.findViewById(R.id.diskon_roti);
                ImageView fotoRotiImageView = convertView.findViewById(R.id.foto_roti);

                namaRotiTextView.setText(product.getNama());
                deskripsiRotiTextView.setText(product.getDeskripsi_produk());
                hargaRotiTextView.setText(String.valueOf(product.getHarga_jual()));
                diskonRotiTextView.setText(String.valueOf(product.getHarga_coret()));
                // Menggunakan library untuk memuat gambar dari URL ke ImageView
                Picasso.get().load(product.getFoto_produk()).into(fotoRotiImageView);

                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }
}
