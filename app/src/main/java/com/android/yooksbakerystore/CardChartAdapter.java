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
import com.google.android.material.button.MaterialButton;

import java.util.Iterator;
import java.util.List;

public class CardChartAdapter extends RecyclerView.Adapter<CardChartAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;
    private UpdateTotalHargaListener updateTotalHargaListener;

    public CardChartAdapter(List<Product> productList, Context context, UpdateTotalHargaListener updateTotalHargaListener) {
        this.productList = productList;
        this.context = context;
        this.updateTotalHargaListener = updateTotalHargaListener;
        updateTotalHarga();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.namaProduk.setText(product.getNama());
        holder.hargaProduk.setText("Rp " + product.getHarga_jual());
        holder.jumlahRoti.setText(String.valueOf(product.getJumlah()));

        String imageUrl = "http://192.168.43.220:8000/asset/image/image-admin/produk/" + product.getFoto_produk();
//        Glide.with(context)
//                .load(imageUrl)
//                .into(holder.fotoProduk);

        holder.tambahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int jumlah = product.getJumlah();
                jumlah++;
                product.setJumlah(jumlah);

                holder.jumlahRoti.setText(String.valueOf(jumlah));
                updateTotalHarga();
            }
        });

        holder.kurangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int jumlah = product.getJumlah();
                if (jumlah > 1) {
                    jumlah--;
                    product.setJumlah(jumlah);

                    holder.jumlahRoti.setText(String.valueOf(jumlah));
                    updateTotalHarga();
                } else if (jumlah-1 < 1) {
                    Iterator<Product> iterator = productList.iterator();
                    while (iterator.hasNext()) {
                        Product products = iterator.next();
                        if (products.getId_produk() == product.getId_produk()) {
                            iterator.remove();
                        }
                    }

                    notifyDataSetChanged();
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
        MaterialButton tambahButton, kurangButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaProduk = itemView.findViewById(R.id.nama_produk_keranjang);
            hargaProduk = itemView.findViewById(R.id.harga_produk_keranjang);
//            fotoProduk = itemView.findViewById(R.id.foto_produk_keranjang);
            tambahButton = itemView.findViewById(R.id.tambah_button_keranjang);
            kurangButton = itemView.findViewById(R.id.kurang_button_keranjang);
            jumlahRoti = itemView.findViewById(R.id.jumlah_roti_keranjang);
        }
    }

    public interface UpdateTotalHargaListener {
        void onUpdateTotalHarga();
    }

    private void updateTotalHarga() {
        int totalHarga = 0;
        for (Product product : productList) {
            totalHarga += product.getHarga_jual() * product.getJumlah();
        }

        // Panggil metode onUpdateTotalHarga pada updateTotalHargaListener
        if (updateTotalHargaListener != null) {
            updateTotalHargaListener.onUpdateTotalHarga();
        }
    }
}

