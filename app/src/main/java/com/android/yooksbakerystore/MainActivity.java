package com.android.yooksbakerystore;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddProductToChartListener, CardChartAdapter.UpdateTotalHargaListener {

    List<Product> productList = new ArrayList<>();
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    private Button btn_checkout;
    private EditText edit_phone_number;
    private EditText edit_pickup_date;
    private Spinner spinner_bank_account;
    private Button btn_upload_image;
    private TextView text_uploaded_file_name;
    private Uri imageUri;
    public RecyclerView recyclerView;
    public CardChartAdapter cardChartAdapter;
    private TextView textTotalValue;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View bottomsheetlayout = inflater.inflate(R.layout.bottomsheetlayout, null);

        recyclerView = bottomsheetlayout.findViewById(R.id.card_charts);
        textTotalValue = bottomsheetlayout.findViewById(R.id.text_total_value);
        edit_phone_number = bottomsheetlayout.findViewById(R.id.edit_phone_number);
        edit_pickup_date = bottomsheetlayout.findViewById(R.id.edit_pickup_date);
        spinner_bank_account = bottomsheetlayout.findViewById(R.id.spinner_bank_account);
        btn_upload_image = bottomsheetlayout.findViewById(R.id.btn_upload_image);
        text_uploaded_file_name = bottomsheetlayout.findViewById(R.id.text_uploaded_file_name);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.bottom_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.bottom_about:
                    replaceFragment(new ShortsFragment());
                    break;
                case R.id.bottom_service:
                    replaceFragment(new SubscriptionsFragment());
                    break;
                case R.id.bottom_settings:
                    replaceFragment(new LibraryFragment());
                    break;
            }

            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        btn_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        // Button Add to Chart di ProductAdapter
        ProductAdapter productAdapter = new ProductAdapter(MainActivity.this, productList, this);
        productAdapter.setAddProductToChartListener(MainActivity.this);

        // Buat objek CardChartAdapter dan set pada RecyclerView
        cardChartAdapter = new CardChartAdapter(productList, MainActivity.this, this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        recyclerView = dialog.findViewById(R.id.card_charts);

        textTotalValue = dialog.findViewById(R.id.text_total_value);
        edit_phone_number = dialog.findViewById(R.id.edit_phone_number);
        edit_pickup_date = dialog.findViewById(R.id.edit_pickup_date);
        spinner_bank_account = dialog.findViewById(R.id.spinner_bank_account);
        btn_upload_image = dialog.findViewById(R.id.btn_upload_image);
        text_uploaded_file_name = dialog.findViewById(R.id.text_uploaded_file_name);
        // Tombol Checkout di Bottom Sheet Layout
        btn_checkout = dialog.findViewById(R.id.btn_checkout);

        // Tambahkan LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(cardChartAdapter);
        Toast.makeText(this, String.valueOf(recyclerView.getAdapter().getItemCount()), Toast.LENGTH_SHORT).show();

        btn_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Mendapatkan data dari user
                // Mendapatkan id user dari sharedPreferences "yyyy-MM-dd"
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int id_user = sharedPreferences.getInt("id_user", 0);

                // Mendapatkan tanggal penjualan saat ini
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String tanggal_penjualan = dateFormat.format(calendar.getTime());

                // Lakukan proses pembaruan total harga
                updateTotalHarga();
                // Dapatkan nilai total harga dari textTotalValue
                String total_penjualan = textTotalValue.getText().toString();

                int id_toko = 1;
                String status_pesanan = "Pending";
                int nomer_telp = Integer.parseInt(edit_phone_number.getText().toString());
                String tanggal_ambil_penjualan = edit_pickup_date.getText().toString();

                // Menyimpan Ke Database dan anu
                String bukti = text_uploaded_file_name.getText().toString();
                String fileUrl = "http://192.168.1.7:8000/asset/image/image-admin/bukti/" + bukti;
                simpanNamaFileKeDatabase(bukti);
                // Mendapatkan byte array data dari gambar
                byte[] fileData = getFileDataFromPath(getRealPathFromURI(imageUri));
                // Membuat permintaan POST menggunakan Volley
                StringRequest stringRequest = new StringRequest(Request.Method.POST, fileUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Respon berhasil dari server
                                // Lakukan operasi penyimpanan nama file pada database
                                simpanNamaFileKeDatabase(bukti);

                                // Lakukan tindakan setelah mengunggah dan menyimpan gambar berhasil, misalnya:
                                // - Menampilkan notifikasi atau pesan sukses
                                // - Mengganti tampilan atau memuat halaman baru

                                Intent intent = new Intent(MainActivity.this, NotaActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Respon gagal dari server
                                Toast.makeText(MainActivity.this, "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return fileData;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "image/jpeg/jpg"; // Ganti dengan tipe konten yang sesuai dengan gambar yang Anda unggah
                    }
                };

                // Menambahkan permintaan ke antrian permintaan Volley
                Volley.newRequestQueue(MainActivity.this).add(stringRequest);

                String metode_pembayaran = spinner_bank_account.getSelectedItem().toString();

                // Lakukan proses posting data ke server
                // Ganti bagian ini dengan kode untuk mengirim data ke server Anda
                // Misalnya, menggunakan metode dari kelas MyServerRequest
                MyServerRequest myServerRequest = new MyServerRequest(MainActivity.this);
                myServerRequest.checkout(id_user, id_toko, String.valueOf(nomer_telp), tanggal_penjualan, tanggal_ambil_penjualan, total_penjualan, metode_pembayaran, bukti, status_pesanan, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Proses berhasil
                        Toast.makeText(MainActivity.this, "Data terkirim ke server", Toast.LENGTH_SHORT).show();

                        // Lakukan tindakan setelah posting data berhasil, misalnya:
                        // - Menampilkan notifikasi atau pesan sukses
                        // - Mengganti tampilan atau memuat halaman baru

                        Intent intent = new Intent(MainActivity.this, NotaActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Proses gagal
                        Toast.makeText(MainActivity.this, "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Tutup dialog atau lakukan tindakan lain setelah klik tombol checkout
                // dialog.dismiss();
            }
        });


        updateTotalHarga();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            String fileName = getFileName(imageUri);
            text_uploaded_file_name.setText(fileName);

            // Simpan nama file ke database
            simpanNamaFileKeDatabase(fileName);
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme != null && scheme.equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }

                cursor.close();
            }
        }

        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }

        return fileName;
    }

    private byte[] getFileDataFromPath(String filePath) {
        File file = new File(filePath);
        byte[] fileData = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            byte[] buffer = new byte[1024];
            int len;

            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            fis.close();
            bos.close();
            fileData = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileData;
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return filePath;
    }
    private void simpanNamaFileKeDatabase(String fileName) {
        // Lakukan proses penyimpanan nama file ke database Anda
        // Misalnya, menggunakan metode dari kelas MyServerRequest
        MyServerRequest myServerRequest = new MyServerRequest(MainActivity.this);
        myServerRequest.simpanNamaFile(fileName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Proses berhasil
                Toast.makeText(MainActivity.this, "Nama file berhasil disimpan di database", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Proses gagal
                Toast.makeText(MainActivity.this, "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // About Product
    @Override
    public void onAddProductToChart(Product product) {
        // Tambahkan produk ke keranjang
        addProductToChart(product);
    }

    private void addProductToChart(Product product) {
        Product produkDitambahkan = null;
        for (Product p : productList) {
            if (p.getId_produk() == product.getId_produk()) {
                produkDitambahkan = p;
                break;
            }
        }

        // Menampilkan hasil pemilihan data
        if (produkDitambahkan != null) {
            produkDitambahkan.setJumlah(produkDitambahkan.getJumlah()+1);
        } else {
            productList.add(product); // Tambahkan produk ke list

            // Update adapter
            cardChartAdapter.notifyDataSetChanged();

            // Hitung total harga
            updateTotalHarga();
        }
    }

    @Override
    public void onUpdateTotalHarga() {
        // Hitung total harga
        updateTotalHarga();
    }

    private void updateTotalHarga() {
        int totalHarga = 0;
        for (Product product : productList) {
            totalHarga += product.getHarga_jual() * product.getJumlah();
        }

        textTotalValue.setText("Rp " + totalHarga);
    }
}
