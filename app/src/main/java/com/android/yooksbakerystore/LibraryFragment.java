package com.android.yooksbakerystore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menginisialisasi SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Mendapatkan referensi button
        Button btnLogout = view.findViewById(R.id.btn_logout);

        // Menambahkan onClickListener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengubah nilai isLoggedIn menjadi false pada SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                // Menampilkan notifikasi "Anda Telah Logout!"
                Toast.makeText(getActivity(), "Anda Telah Logout!", Toast.LENGTH_SHORT).show();

                // Mengarahkan ke halaman LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish(); // Menutup activity ini agar pengguna tidak dapat kembali ke halaman library
            }
        });

        return view;
    }
}