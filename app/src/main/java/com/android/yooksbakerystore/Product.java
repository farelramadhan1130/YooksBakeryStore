package com.android.yooksbakerystore;

public class Product {
    private int id_produk;
    private int harga_jual;
    private int harga_coret;
    private int stok;
    private String nama;
    private String deskripsi_produk;
    private String foto_produk;

    public Product(
            int id_produk, int harga_jual, int harga_coret, int stok, String nama, String deskripsi_produk, String foto_produk) {
        this.id_produk = id_produk;
        this.nama = nama;
        this.deskripsi_produk = deskripsi_produk;
        this.harga_jual = harga_jual;
        this.harga_coret = harga_coret;
        this.stok = stok;
        this.foto_produk = foto_produk;
    }

    public Product(int id_produk, String nama, int harga_jual) {
    }

    public int getId_produk() {
        return id_produk;
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

    public int getHarga_jual() {
        return harga_jual;
    }

    public int getHarga_coret() {
        return harga_coret;
    }

    public int getStok() {
        return stok;
    }
}

