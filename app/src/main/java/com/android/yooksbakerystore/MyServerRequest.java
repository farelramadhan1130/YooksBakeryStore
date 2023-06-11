    package com.android.yooksbakerystore;

    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.widget.Toast;

    import com.android.volley.AuthFailureError;
    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.HashMap;
    import java.util.Map;

    public class MyServerRequest {
        private static final String TAG = "MyServerRequest";
        private final Context context;
        private final RequestQueue requestQueue;
        private SharedPreferences sharedPreferences;
        private CheckoutListener checkoutListener;

        public MyServerRequest(Context context) {
            this.context = context;
            this.requestQueue = Volley.newRequestQueue(context);
            this.sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }

        // Menambahkan metode checkout ke dalam kelas MyServerRequest
        public interface CheckoutListener {
            void onCheckout(String idToko, String tanggalPenjualan, String tanggalAmbilPenjualan, int totalPenjualan, String metodePembayaran, String bukti);
        }

        // Menambahkan interface ServerCallback
        public interface ServerCallback {
            void onSuccess(String response);
            void onError(String error);
        }

        public void login(String email, String password, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
            // URL endpoint untuk login
            String url = "http://192.168.1.7:8000/api/login";

            // membuat objek RequestQueue untuk mengirim request ke server
            RequestQueue queue = Volley.newRequestQueue(context);

            // membuat objek StringRequest untuk melakukan request POST
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    // response dari server jika login berhasil
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();
                                    // Menyimpan ID user ke dalam SharedPreference
                                    editor.putString("userId", jsonResponse.getString("id_user"));
                                    editor.apply();
                                    // Mengambil ID pengguna dari SharedPreferences
                                    String userId = sharedPreferences.getString("userId", "");

//                                    // Panggil method onCheckout pada checkoutListener
//                                    if (checkoutListener != null) {
//                                        String tanggalPenjualan = getCurrentDate();
//                                        String tanggalAmbilPenjualan = getPickupDate();
//                                        String totalPenjualan = getTotalHarga();
//                                        String metodePembayaran = getMetodePembayaran();
//                                        String bukti = getBukti();
//
//                                        checkoutListener.onCheckout(userId, tanggalPenjualan, tanggalAmbilPenjualan, Integer.parseInt(totalPenjualan), metodePembayaran, bukti);
//                                    }

                                    Intent intent = new Intent(context, SplashScreenActivity.class);
                                    context.startActivity(intent);
                                } else {
                                    // response dari server jika login gagal
                                    Toast.makeText(context, "Login gagal", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // response dari server jika terjadi kesalahan pada request atau response dari server
                            Toast.makeText(context, "Terjadi kesalahan pada server" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };

            // menambahkan request ke dalam queue
            queue.add(stringRequest);
        }

//        // Fungsi bantuan untuk mendapatkan tanggal saat ini
//        private String getCurrentDate() {
//            // Implementasikan logika untuk mendapatkan tanggal saat ini
//            // Contoh: return tanggal saat ini dalam format yang diinginkan (misal: "2023-06-08")
//            return "2023-06-08";
//        }
//
//        // Fungsi bantuan untuk mendapatkan tanggal ambil penjualan
//        private String getPickupDate() {
//            // Implementasikan logika untuk mendapatkan tanggal ambil penjualan
//            // Contoh: return tanggal ambil penjualan dalam format yang diinginkan (misal: "2023-06-10")
//            return "2023-06-10";
//        }
//
//        // Fungsi bantuan untuk mendapatkan total harga
//        private String getTotalHarga() {
//            // Implementasikan logika untuk mendapatkan total harga
//            // Contoh: return total harga dalam format yang diinginkan (misal: "100000")
//            return "100000";
//        }
//
//        // Fungsi bantuan untuk mendapatkan metode pembayaran
//        private String getMetodePembayaran() {
//            // Implementasikan logika untuk mendapatkan metode pembayaran
//            // Contoh: return metode pembayaran yang dipilih (misal: "Transfer Bank")
//            return "Transfer Bank";
//        }
//
//        // Fungsi bantuan untuk mendapatkan bukti pembayaran
//        private String getBukti() {
//            // Implementasikan logika untuk mendapatkan bukti pembayaran
//            // Contoh: return path atau URL gambar bukti pembayaran
//            return "https://example.com/bukti_pembayaran.jpg";
//        }
//
//        // Setter untuk CheckoutListener
//        public void setCheckoutListener(CheckoutListener listener) {
//            this.checkoutListener = listener;
//        }

        public void checkout(int id_user, int id_toko, String nomer_telp, String tanggal_penjualan, String tanggal_ambil_penjualan, String total_penjualan, String  metode_pembayaran, String bukti, String status_pesanan, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
            // URL endpoint untuk checkout
            String url = "http://192.168.1.7:8000/api/checkout";

            // Membuat objek StringRequest untuk melakukan request POST
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Response dari server jika checkout berhasil
                            if (successListener != null) {
                                successListener.onResponse(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Response dari server jika terjadi kesalahan pada request atau response dari server
                            if (errorListener != null) {
                                errorListener.onErrorResponse(error);
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_user", String.valueOf(id_user));
                    params.put("id_toko", String.valueOf(id_toko));
                    params.put("status_pesanan", String.valueOf(status_pesanan));
//                    params.put("telepon_user", nomer_telp);
                    params.put("tanggal_penjualan", tanggal_penjualan);
                    params.put("tanggal_ambil_penjualan", tanggal_ambil_penjualan);
                    params.put("total_penjualan", String.valueOf(total_penjualan));
                    params.put("metode_pembayaran", metode_pembayaran);
                    params.put("bukti", bukti);
                    return params;
                }
            };

            // Menambahkan request ke dalam queue
            requestQueue.add(stringRequest);
        }

        public void simpanNamaFile(String fileName, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
            // Implementasikan logika penyimpanan nama file ke database di sini
            // ...
            // Panggil successListener jika penyimpanan berhasil
            successListener.onResponse("Penyimpanan nama file berhasil");
        }
    }