package com.android.yooksbakerystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardChartAdapter extends RecyclerView.Adapter<CardChartAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;

    public CardChartAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.namaProduk.setText(product.getNama());
        holder.hargaProduk.setText("Rp " + product.getHarga_jual());

        String imageUrl = "http://192.168.1.6:8000/asset/image/image-admin/produk/" + product.getFoto_produk();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.fotoProduk);

        // Aksi saat tombol "+" diklik
        holder.tambahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tambah jumlah
                int jumlah = product.getJumlah();
                jumlah++;
                product.setJumlah(jumlah);

                // Update tampilan jumlah dan total harga
                holder.jumlahRoti.setText(String.valueOf(jumlah));
                updateTotalHarga();
            }
        });

        // Aksi saat tombol "-" diklik
        holder.kurangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kurangi jumlah jika tidak kurang dari 1
                int jumlah = product.getJumlah();
                if (jumlah > 1) {
                    jumlah--;
                    product.setJumlah(jumlah);

                    // Update tampilan jumlah dan total harga
                    holder.jumlahRoti.setText(String.valueOf(jumlah));
                    updateTotalHarga();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaProduk, hargaProduk, jumlahRoti;
        ImageView fotoProduk;
        ImageButton tambahButton, kurangButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaProduk = itemView.findViewById(R.id.nama_produk_keranjang);
            hargaProduk = itemView.findViewById(R.id.harga_produk_keranjang);
            fotoProduk = itemView.findViewById(R.id.foto_produk_keranjang);
            tambahButton = itemView.findViewById(R.id.tambah_button_keranjang);
            kurangButton = itemView.findViewById(R.id.kurang_button_keranjang);
            jumlahRoti = itemView.findViewById(R.id.jumlah_roti_keranjang);
        }
    }

    private void updateTotalHarga() {
        int totalHarga = 0;
        for (Product product : productList) {
            totalHarga += product.getHarga_jual() * product.getJumlah();
        }
        // Anda perlu mengirimkan totalHarga ke tampilan Anda
        // Misalnya, jika Anda memiliki TextView dengan id textTotalValue
        // maka Anda dapat mengaksesnya dan mengubah nilainya seperti berikut:
        // TextView textTotalValue = findViewById(R.id.text_total_value);
        // textTotalValue.setText("Rp " + totalHarga);
        // Jika Anda menggunakan AlertDialog, Toast, atau tampilan lain,
        // pastikan Anda menyesuaikan implementasinya dengan tampilan yang digunakan di aplikasi Anda.
    }
}