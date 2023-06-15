//package com.android.simanika;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.simanika.Configuration.CircleTransform;
//import com.android.simanika.Services.ApiClient;
//import com.android.simanika.Services.HTTP.GlobalResponse;
//import com.android.simanika.Services.HTTP.UserResponse;
//import com.squareup.picasso.Picasso;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class EditProfileActivity extends AppCompatActivity {
//
//    private ImageView btnback, profileImage;
//    private File fotoprofile = null;
//    private Button btnedit, btneditprofile;
//    private TextView profilName, profilBio;
//    private EditText nama, nim, angkatan, email, telp;
//
//    private Context context;
//
//    // Konstanta untuk kode permintaan (request code)
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private static final int CAMERA_REQUEST = 2;
//
//    private Uri imageUri;
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_profile);
//
//        nama = findViewById(R.id.edit_nama);
//        nim = findViewById(R.id.edit_nim);
//        angkatan = findViewById(R.id.edit_angkatan);
//        email = findViewById(R.id.edit_email);
//        telp = findViewById(R.id.edit_notelp);
//        profileImage = findViewById(R.id.profile_foto);
//        profilName = findViewById(R.id.profile_name);
//        profilBio = findViewById(R.id.profile_bio);
//
//        btnback = findViewById(R.id.kembali);
//        btnedit = findViewById(R.id.editpicture);
//        btneditprofile = findViewById(R.id.edit_profile);
//
//        btnback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btneditprofile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpdateProfile();
//            }
//        });
//        getUser();
//    }
//
//    public void chooseImage(View view) {
//        final CharSequence[] options = {"Pilih dari Galeri", "Ambil Foto"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Pilih Foto");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        // Memilih foto dari galeri
//                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
//                        break;
//                    case 1:
//                        // Mengambil foto dengan kamera
//                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                        break;
//                }
//            }
//        });
//        builder.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
//                // Mendapatkan URI dari gambar yang dipilih dari galeri
//                Uri selectedImageUri = data.getData();
//                try {
//                    // Mengubah URI menjadi Bitmap
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                    // Menampilkan gambar pada ImageView
//                    profileImage.setImageBitmap(bitmap);
//                    fotoprofile = bitmapToFile(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else if (requestCode == CAMERA_REQUEST && data != null) {
//                // Mendapatkan foto yang diambil menggunakan kamera
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                // Menampilkan foto pada ImageView
//                profileImage.setImageBitmap(photo);
//                fotoprofile = bitmapToFile(photo);
//            }
//        }
//    }
//
//    public File bitmapToFile(Bitmap bitmap) {
//        try {
//            File file = new File(getCacheDir(), "temp_image.jpg");
//            file.createNewFile();
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            byte[] bitmapData = byteArrayOutputStream.toByteArray();
//
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(bitmapData);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//
//            return file;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private void UpdateProfile() {
//        MultipartBody.Part fotoPart = null;
//        if (fotoprofile != null) {
//            File file = new File(fotoprofile.getPath());
//            RequestBody fotoRequestBody = RequestBody.create(file, MediaType.parse("image/jpeg"));
//            fotoPart = MultipartBody.Part.createFormData("foto", file.getName(), fotoRequestBody);
//        }
//
//        Call<GlobalResponse> globalResponseCall = null;
//        if (fotoPart == null) {
//            globalResponseCall = ApiClient.getUserService(EditProfileActivity.this)
//                    .userUpdateProfile(
//                            RequestBody.create(nama.getText().toString(), MediaType.parse("text/plain")),
//                            RequestBody.create(telp.getText().toString(), MediaType.parse("text/plain")),
//                            RequestBody.create(angkatan.getText().toString(), MediaType.parse("text/plain"))
//                    );
//        } else {
//            globalResponseCall = ApiClient.getUserService(EditProfileActivity.this)
//                    .userUpdateProfile(
//                            RequestBody.create(nama.getText().toString(), MediaType.parse("text/plain")),
//                            RequestBody.create(telp.getText().toString(), MediaType.parse("text/plain")),
//                            RequestBody.create(angkatan.getText().toString(), MediaType.parse("text/plain")),
//                            fotoPart
//                    );
//        }
//
//        globalResponseCall.enqueue(new Callback<GlobalResponse>() {
//            @Override
//            public void onResponse(Call<GlobalResponse> call, Response<GlobalResponse> response) {
//                if (response.isSuccessful()) {
//                    GlobalResponse globalResponse = response.body();
//
//                    if (!globalResponse.isError()) {
//                        Toast.makeText(EditProfileActivity.this, globalResponse.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        // Tampilkan dialog sukses
//                        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
//                        builder.setTitle("Pembaruan Berhasil");
//                        builder.setMessage("Profil berhasil diperbarui.");
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Tindakan setelah tombol OK ditekan, jika diperlukan
//                                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        builder.show();
//                    }
//                } else {
//                    Toast.makeText(EditProfileActivity.this, "Update Profile Gagal.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<GlobalResponse> call, Throwable t) {
////                progressDialog.dismiss();
//                Toast.makeText(EditProfileActivity.this, "Throwable "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void getUser(){
////        ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
////        progressDialog.setMessage("Loading..."); // Set message untuk dialog
////        progressDialog.setCancelable(false); // Set apakah dialog bisa di-cancel atau tidak
////
////        progressDialog.show(); // Menampilkan dialog
//
//        Call<UserResponse> userResponseCall = ApiClient.getUserService(EditProfileActivity.this).userDetail();
//        userResponseCall.enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
////                progressDialog.dismiss();
//
//                if (response.isSuccessful()){
//                    UserResponse userResponse = response.body();
//                    nama.setText(userResponse.getNama());
//                    nim.setText(userResponse.getNim());
//                    angkatan.setText(userResponse.getAngkatan());
//                    email.setText(userResponse.getEmail());
//                    telp.setText(userResponse.getTelp());
//                    profilName.setText(userResponse.getNama());
//                    if (userResponse.getDetail_user().getDivisi() == null) {
//                        profilBio.setText(userResponse.getDetail_user().getJabatan().getNama());
//                    } else {
//                        profilBio.setText(userResponse.getDetail_user().getDivisi().getNama()+" - " + userResponse.getDetail_user().getJabatan().getNama());
//                    }
//                    Picasso.get().load(ApiClient.getBaseUrl()+userResponse.getDetail_user().getFoto())
//                            .transform(new CircleTransform())
//                            .into((ImageView) findViewById(R.id.profile_foto));
//                } else {
//                    Toast.makeText(EditProfileActivity.this, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//
//}