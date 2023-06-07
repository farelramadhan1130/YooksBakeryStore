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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddProductToChartListener, CardChartAdapter.UpdateTotalHargaListener {

    List<Product> productList = new ArrayList<>();
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    private Button btn_checkout;
    public RecyclerView recyclerView;
    public CardChartAdapter cardChartAdapter;
    private TextView textTotalValue;

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
        textTotalValue.setText("Kont");
        // Tambahkan LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(cardChartAdapter);
        Toast.makeText(this, String.valueOf(recyclerView.getAdapter().getItemCount()), Toast.LENGTH_SHORT).show();

        // Tombol Checkout di Bottom Sheet Layout
//        btn_checkout = dialog.findViewById(R.id.btn_checkout);
//        btn_checkout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, NotaActivity.class);
//                startActivity(intent);
//                dialog.dismiss();
//            }
//        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onAddProductToChart(Product product) {
        // Tambahkan produk ke keranjang
        addProductToChart(product);
    }

    private void addProductToChart(Product product) {
        productList.add(product); // Tambahkan produk ke list

        // Update adapter
        cardChartAdapter.notifyDataSetChanged();

        // Hitung total harga
        updateTotalHarga();
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
