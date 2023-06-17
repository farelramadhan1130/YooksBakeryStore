package com.android.yooksbakerystore;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int id_produk;
    private int id_kategori;
    private int id_supplier;
    private int id_toko;
    private int harga_jual;
    private int biaya_produksi;
    private int harga_coret;
    private int stok;
    private String nama;
    private String deskripsi_produk;
    private String foto_produk;
    private int jumlah; // Tambahkan atribut jumlah

    public Product(int id_produk, int id_kategori, int id_supplier, int id_toko, int harga_jual, int harga_coret, int stok, String nama, String deskripsi_produk, String foto_produk) {
        this.id_produk = id_produk;
        this.id_kategori = id_kategori;
        this.id_supplier = id_supplier;
        this.id_toko = id_toko;
        this.nama = nama;
        this.deskripsi_produk = deskripsi_produk;
        this.harga_jual = harga_jual;
        this.harga_coret = harga_coret;
        this.stok = stok;
        this.foto_produk = foto_produk;
        this.jumlah = 1; // Set jumlah awal ke 1
    }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) {this.jumlah = jumlah;}
    public int getId_produk() {
        return id_produk;
    }
    public int getId_kategori() {
        return id_kategori;
    }
    public int getId_supplier() {
        return id_supplier;
    }
    public int getId_toko() {
        return id_toko;
    }
    public String getNama() {
        return nama;
    }
    public String getDeskripsi_produk() {
        return deskripsi_produk;
    }
    public String getFoto_produk() {
        return foto_produk;
    }
    public int getHarga_jual() { return harga_jual; }
    public int getHarga_produksi() { return biaya_produksi; }
    public int getHarga_coret() {
        return harga_coret;
    }
    public int getStok() {
        return stok;
    }

    // Implementasi Parcelable
    protected Product(Parcel in) {
        nama = in.readString();
        harga_jual = in.readInt();
        jumlah = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeInt(harga_jual);
        dest.writeInt(jumlah);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}


