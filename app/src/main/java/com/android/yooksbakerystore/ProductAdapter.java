package com.android.yooksbakerystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_roti, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.idProduk = product.getId_produk();
        holder.namaRotiTextView.setText(product.getNama());
        holder.deskripsiRotiTextView.setText(product.getDeskripsi_produk());
        holder.hargaRotiTextView.setText(String.valueOf(product.getHarga_jual()));
        holder.diskonRotiTextView.setText(String.valueOf(product.getHarga_coret()));
        holder.stockRotiTextView.setText(String.valueOf(product.getStok()));

        Glide.with(context)
                .load(product.getFoto_produk())
                .into(holder.fotoRotiImageView);

        // Aksi saat tombol "Keranjang" diklik
        holder.keranjangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aksi yang ingin dilakukan ketika tombol "Keranjang" diklik
                // Misalnya, tambahkan produk ke keranjang
                // Anda dapat mengakses holder.idProduk untuk mendapatkan ID produk yang diklik
                // Implementasikan logika tambahan sesuai kebutuhan Anda
                int idProduk = holder.idProduk;
                // Implementasikan aksi tambahan sesuai kebutuhan Anda
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoRotiImageView;
        TextView namaRotiTextView;
        TextView hargaRotiTextView;
        TextView diskonRotiTextView;
        TextView stockRotiTextView;
        TextView deskripsiRotiTextView;
        Button keranjangButton;
        int idProduk;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoRotiImageView = itemView.findViewById(R.id.foto_roti);
            namaRotiTextView = itemView.findViewById(R.id.nama_roti);
            hargaRotiTextView = itemView.findViewById(R.id.harga_roti);
            diskonRotiTextView = itemView.findViewById(R.id.diskon_roti);
            stockRotiTextView = itemView.findViewById(R.id.stock);
            deskripsiRotiTextView = itemView.findViewById(R.id.deskripsi_roti);
            keranjangButton = itemView.findViewById(R.id.keranjang);

        }
    }
}
