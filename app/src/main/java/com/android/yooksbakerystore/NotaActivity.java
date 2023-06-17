package com.android.yooksbakerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotaActivity extends AppCompatActivity {

    private ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_nota);

        // Mendapatkan nilai id_pemesanan yang dikirim dari MainActivity
        String nama_user = getIntent().getStringExtra("nama_user");
        String nomer_telp = getIntent().getStringExtra("nomer_telp");
        String tanggal_penjualan = getIntent().getStringExtra("tanggal_penjualan");
        String tanggal_ambil_penjualan = getIntent().getStringExtra("tanggal_ambil_penjualan");
        String total_penjualan = getIntent().getStringExtra("total_penjualan");

        // Mendapatkan daftar produk yang dikirim dari MainActivity
        productList = getIntent().getParcelableArrayListExtra("productList");

        // Menginisialisasi TextView dengan id text_nama_pelanggan
        TextView namaPelangganTextView = findViewById(R.id.text_nama_pelanggan);
        namaPelangganTextView.setText(nama_user);

        // Menginisialisasi TextView dengan id text_no_telepon
        TextView noTeleponTextView = findViewById(R.id.text_no_telepon);
        noTeleponTextView.setText(nomer_telp);

        // Menginisialisasi TextView dengan id text_tanggal_pemesanan
        TextView tanggalPemesananTextView = findViewById(R.id.text_tanggal_pemesanan);
        tanggalPemesananTextView.setText(tanggal_penjualan);

        // Menginisialisasi TextView dengan id text_tanggal_ambil_pemesanan
        TextView tanggalAmbilPemesananTextView = findViewById(R.id.text_tanggal_pengambilan);
        tanggalAmbilPemesananTextView.setText(tanggal_ambil_penjualan);

        // Mendapatkan referensi ke TableLayout di layout
        TableLayout tableLayout = findViewById(R.id.tabel_roti);

        // Loop melalui setiap produk dan membuat TableRow
        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);

            // Buat TableRow baru
            TableRow row = new TableRow(this);

            // Atur layout params untuk TableRow
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            );
            row.setLayoutParams(layoutParams);

            // Tambahkan data produk ke dalam TableRow
            TextView namaTextView = new TextView(this);
            namaTextView.setText(product.getNama());
            namaTextView.setId(R.id.tabel_nama_roti + i); // Set ID untuk TextView

            TextView hargaTextView = new TextView(this);
            hargaTextView.setText(String.valueOf(product.getHarga_jual()));
            hargaTextView.setId(R.id.tabel_harga_roti + i); // Set ID untuk TextView

            TextView jumlahTextView = new TextView(this);
            jumlahTextView.setText(String.valueOf(product.getJumlah()));
            jumlahTextView.setId(R.id.tabel_jumlah_roti + i); // Set ID untuk TextView

            TextView subtotalTextView = new TextView(this);
            subtotalTextView.setText(String.valueOf(product.getHarga_jual() * product.getJumlah()));
            subtotalTextView.setId(R.id.tabel_subtotal_roti + i); // Set ID untuk TextView

            // Tambahkan TextView ke dalam TableRow
            row.addView(namaTextView);
            row.addView(hargaTextView);
            row.addView(jumlahTextView);
            row.addView(subtotalTextView);

            // Tambahkan TableRow ke dalam TableLayout
            tableLayout.addView(row, layoutParams);
        }
    }
}
