package com.android.yooksbakerystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Paint;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Optional;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private static List<Product> productList;
    private String baseUrl = "http://192.168.1.4:8000/asset/image/image-admin/produk/"; // URL Tempat Penyimpanan Foto Produk
    private AddProductToChartListener addProductToChartListener; // Tambahkan variabel ini

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public ProductAdapter(Context context, List<Product> productList, AddProductToChartListener listener) {
        this.context = context;
        this.productList = productList;
        this.addProductToChartListener = listener;
    }

    // Metode setter untuk AddProductToChartListener
    public void setAddProductToChartListener(AddProductToChartListener listener) {
        addProductToChartListener = listener;
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
        holder.idKategori = product.getId_kategori();
        holder.idSupplier = product.getId_supplier();
        holder.idToko = product.getId_toko();
        holder.namaRotiTextView.setText(product.getNama());
        holder.deskripsiRotiTextView.setText(product.getDeskripsi_produk());
        holder.hargaRotiTextView.setText(String.valueOf(product.getHarga_jual()));
        holder.stockRotiTextView.setText(String.valueOf(product.getStok()));
        holder.diskonRotiTextView.setText(String.valueOf(product.getHarga_coret()));
        holder.diskonRotiTextView.setPaintFlags(holder.diskonRotiTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String imageUrl = baseUrl + product.getFoto_produk(); // Menggabungkan base URL dengan path foto produk

        Glide.with(context)
                .load(imageUrl)
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
                int idKategori = holder.idKategori;
                int idSupplier = holder.idSupplier;
                int idToko = holder.idToko;
                // Implementasikan aksi tambahan sesuai kebutuhan Anda

                // Tambahkan produk ke card_chart.xml
                if (addProductToChartListener != null) {
                    // Dapatkan objek Product berdasarkan ID produk yang diklik
                    Product addedProduct = getProductById(idProduk);
                    if (addedProduct != null) {
                        // Panggil metode onAddProductToChart di AddProductToChartListener
                        Toast.makeText(context, "Berhasil ditambahkan.", Toast.LENGTH_SHORT).show();
                        addProductToChartListener.onAddProductToChart(addedProduct);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ...

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoRotiImageView;
        TextView namaRotiTextView;
        TextView hargaRotiTextView;
        TextView diskonRotiTextView;
        TextView stockRotiTextView;
        TextView deskripsiRotiTextView;
        Button keranjangButton;
        int idProduk;
        int idKategori;
        int idSupplier;
        int idToko;
        AddProductToChartListener addProductToChartListener; // Jadikan variabel ini static

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoRotiImageView = itemView.findViewById(R.id.foto_roti);
            namaRotiTextView = itemView.findViewById(R.id.nama_roti);
            hargaRotiTextView = itemView.findViewById(R.id.harga_roti);
            diskonRotiTextView = itemView.findViewById(R.id.diskon_roti);
            stockRotiTextView = itemView.findViewById(R.id.stock);
            deskripsiRotiTextView = itemView.findViewById(R.id.deskripsi_roti);
            keranjangButton = itemView.findViewById(R.id.keranjang);

            keranjangButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addProductToChartListener != null) {
                        // Dapatkan objek Product berdasarkan ID produk yang diklik
                        Product addedProduct = getProductById(idProduk);
                        if (addedProduct != null) {
                            // Panggil metode onAddProductToChart di AddProductToChartListener
                            addProductToChartListener.onAddProductToChart(addedProduct);
                        }
                    }
                }
            });
        }
    }

    // Metode untuk mencari produk berdasarkan ID
    private static Product getProductById(int idProduk) {
        for (Product product : productList) {
            if (product.getId_produk() == idProduk) {
                return product;
            }
        }
        return null;
    }
}

