    package com.android.yooksbakerystore;

    import static android.content.ContentValues.TAG;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import android.app.DatePickerDialog;
    import android.app.Dialog;
    import android.content.ContentResolver;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Color;
    import android.graphics.drawable.ColorDrawable;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Parcelable;
    import android.provider.MediaStore;
    import android.provider.OpenableColumns;
    import android.util.Base64;
    import android.util.Log;
    import android.view.Gravity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.Window;
    import android.webkit.MimeTypeMap;
    import android.widget.Button;
    import android.widget.DatePicker;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.content.Intent;
    import android.net.Uri;
    import android.provider.MediaStore;

    import okhttp3.Call;
    import okhttp3.Callback;
    import okhttp3.MediaType;
    import okhttp3.MultipartBody;
    import okhttp3.OkHttpClient;
    import okhttp3.RequestBody;

    import androidx.appcompat.widget.Toolbar;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.android.volley.AuthFailureError;
    import com.android.volley.NetworkResponse;
    import com.android.volley.RequestQueue;
    import com.android.volley.Request;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonObjectRequest;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.android.material.navigation.NavigationView;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.ByteArrayOutputStream;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;

    public class MainActivity extends AppCompatActivity implements AddProductToChartListener, CardChartAdapter.UpdateTotalHargaListener {

        List<Product> productList = new ArrayList<>();
        List<Product> selectedProducts = new ArrayList<>();
        private static final String CHECKOUT_URL = "http://192.168.1.6:8000/api/checkout";
        FloatingActionButton fab;
        DrawerLayout drawerLayout;
        BottomNavigationView bottomNavigationView;
        private Button btn_checkout;
        private EditText edit_phone_number;
        private TextView edit_pickup_date;
        private Spinner spinner_bank_account;
        private Button btn_upload_image;
        private TextView text_uploaded_file_name;
        private Uri imageUri;
        public RecyclerView recyclerView;
        public CardChartAdapter cardChartAdapter;
        private TextView textTotalValue;
        private static final int PICK_IMAGE_REQUEST = 1;
        Bitmap bitmap;
        String encodeImageString;
        int biaya_produksi = 0;
        int harga_jual = 0;
        String nama_produk = "";
        int jumlah = 0;
        int total_harga = 0;
        int id_produk = 0;
        private Calendar calendar;
        private String tanggal_ambil_penjualan;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            calendar = Calendar.getInstance();
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

            edit_pickup_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker();
                }
            });
            btn_upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                }
            });
            btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkout();
                }
            });


            updateTotalHarga();
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        private void checkout() {
            // Mendapatkan id user dari sharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            final int id_user = sharedPreferences.getInt("userId", 0);
            final String nama_user = sharedPreferences.getString("username", "");

            // Mendapatkan tanggal penjualan saat ini
            calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String tanggal_penjualan = dateFormat.format(calendar.getTime());

            // Dapatkan nilai total harga dari textTotalValue
            final String total_penjualan = String.valueOf(textTotalValue.getText());

            final String nomer_telp = edit_phone_number.getText().toString();
            final String tanggal_ambil_penjualan = this.tanggal_ambil_penjualan;
            final String metode_pembayaran = spinner_bank_account.getSelectedItem().toString();

            // Membuat daftar produk
            ArrayList<Product> productList = new ArrayList<>(selectedProducts);
            // Navigasi ke NotaActivity dengan mengirim daftar produk
            navigateToNotaActivity(productList);

            // Mengirim request ke server menggunakan Volley dengan VolleyMultipartRequest
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECKOUT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Proses berhasil
                            Toast.makeText(MainActivity.this, "Data terkirim ke server", Toast.LENGTH_SHORT).show();

                            // - Mengganti tampilan atau memuat halaman baru
                            Intent intent = new Intent(MainActivity.this, NotaActivity.class);
                            intent.putExtra("id_user", id_user);
                            intent.putExtra("nama_user", nama_user);
                            intent.putExtra("nomer_telp", nomer_telp);
                            intent.putExtra("tanggal_penjualan", tanggal_penjualan);
                            intent.putExtra("tanggal_ambil_penjualan", tanggal_ambil_penjualan);
                            intent.putExtra("total_penjualan", total_penjualan);

                            // Menambahkan data produk ke dalam parameter request
                            for (int i = 0; i < productList.size(); i++) {
                                Product product = productList.get(i);
                                intent.putExtra("produk[" + i + "][id_produk]", String.valueOf(product.getId_produk()));
                                intent.putExtra("produk[" + i + "][nama_produk]", product.getNama());
                                intent.putExtra("produk[" + i + "][harga_produk]", String.valueOf(product.getHarga_jual()));
                                intent.putExtra("produk[" + i + "][jumlah_produk]", String.valueOf(product.getJumlah()));
                            }

                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Proses gagal
                            Toast.makeText(MainActivity.this, "Terjadi kesalahan : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    String imageData = imageToString(bitmap);
                    // Request Tabel Penjualan
                    params.put("id_user", String.valueOf(id_user));
                    params.put("tanggal_penjualan", tanggal_penjualan);
                    params.put("tanggal_ambil_penjualan", tanggal_ambil_penjualan);
                    params.put("metode_pembayaran", metode_pembayaran);
                    params.put("bukti", imageData);
                    params.put("total_penjualan", String.valueOf(total_harga));

                    // Tambahan Buat Tabel Penjualan Produk
                    params.put("biaya_produk", String.valueOf(biaya_produksi));
                    params.put("harga_produk", String.valueOf(harga_jual));
                    params.put("nama_produk", nama_produk);
                    params.put("jumlah_produk", String.valueOf(jumlah));
                    params.put("id_produk", String.valueOf(id_produk));
                    return params;
                }
            };

            // Tambahkan VolleyMultipartRequest ke antrian Volley
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                String fileName = getFileName(imageUri);
                text_uploaded_file_name.setText(fileName);

                // Mengubah URI menjadi objek Bitmap
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    // Gunakan objek bitmap sesuai kebutuhan Anda
                    // Misalnya, tampilkan di ImageView:
                    // imageView.setImageBitmap(bitmap);

                    // Jangan lupa menutup InputStream setelah digunakan
                    // inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String imageToString(Bitmap bitmap) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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

        private void encodeBitmapImage(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesofimage = byteArrayOutputStream.toByteArray();
            encodeImageString = Base64.encodeToString(bytesofimage, Base64.DEFAULT);
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
                if (!selectedProducts.contains(product)) {
                    selectedProducts.add(product);
                }
            } else {
                productList.add(product); // Tambahkan produk ke list

                // Update adapter
                cardChartAdapter.notifyDataSetChanged();

                // Hitung total harga
                updateTotalHarga();
            }
            // Dapatkan informasi yang dibutuhkan
            biaya_produksi = product.getHarga_produksi();
            harga_jual = product.getHarga_jual();
            nama_produk = product.getNama();
            jumlah = product.getJumlah();
            id_produk = product.getId_produk();
        }

        @Override
        public void onUpdateTotalHarga() {
            // Hitung total harga
            updateTotalHarga();
        }

        private void updateTotalHarga() {
            int totalHarga = 0;
            for (Product product : productList) {
                total_harga = totalHarga += product.getHarga_jual() * product.getJumlah();
            }
            textTotalValue.setText("Rp " + totalHarga);
        }

        // Mengirimkan Data Produk Ke Nota Activity
        private void navigateToNotaActivity(ArrayList<Product> productList) {
            Intent intent = new Intent(MainActivity.this, NotaActivity.class);
            intent.putParcelableArrayListExtra("productList", productList);
            startActivity(intent);
        }

        // Date Picker
        private void showDatePicker() {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Simpan tanggal yang dipilih ke variabel tanggal_ambil_penjualan
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    tanggal_ambil_penjualan = dateFormat.format(calendar.getTime());

                    // Update text tanggal pada edit_pickup_date
                    edit_pickup_date.setText(tanggal_ambil_penjualan);
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // Batasi pemilihan tanggal hanya pada tanggal hari ini...
            datePickerDialog.show();
        }
    }
