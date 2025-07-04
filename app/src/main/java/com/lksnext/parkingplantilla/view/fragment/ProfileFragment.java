package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentProfileBinding;
import com.lksnext.parkingplantilla.view.activity.AddCarActivity;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;

    private ProfileViewModel profileViewModel;
    public ProfileFragment() {
        // Constructor vacío necesario
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerViewCoches;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.CPMensaje.setText("");
        binding.singoutText.setText("");

        profileViewModel.cargarCoches();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get()
                .addOnSuccessListener(documentSnapshot ->  {
                    if(documentSnapshot.exists()){
                        String name = documentSnapshot.getString("name");
                        binding.helloName.setText("TU PERFIL\n"+name);
                    }
                });

        profileViewModel.getListaCoches().observe(getViewLifecycleOwner(), listaCoches->{
            if (listaCoches == null || listaCoches.isEmpty()){
                binding.listaVaciaM.setText("No Hay Coches\nDisponibles");
            }else{
                binding.listaVaciaM.setText("");
            }

            if (listaCoches != null) {
                CarItemAdapter cocheAdapter = new CarItemAdapter(listaCoches, car ->
                        profileViewModel.deleteCar(car));
                recyclerView.setAdapter(cocheAdapter);
            }
        });

        binding.refreshButton.setOnClickListener(v->
            profileViewModel.cargarCoches()
        );

        binding.changePassword.setOnClickListener(v ->
            profileViewModel.changeCurrentUserPass()
        );

        binding.logoutButton.setOnClickListener(v -> {
            if(profileViewModel.isLogout().getValue().equals(Boolean.TRUE)){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }else{
                profileViewModel.logout();
            }
        });

        profileViewModel.isLogout().observe(getActivity(), logout->{
            if(logout != null && logout){
                binding.singoutText.setText("CERRAR\nSESIÓN");
            }else{
                binding.singoutText.setText("");
            }
        });

        binding.addCar.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), AddCarActivity.class);
            startActivity(intent);
        });

        profileViewModel.isSent().observe(getActivity(), sent -> {
            if (sent != null) {
                if (sent) {
                    binding.CPMensaje.setText("Mensaje Enviado. ");
                    binding.CPMensaje.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_blue));
                } else {
                    binding.CPMensaje.setText(profileViewModel.getError().getValue());
                    binding.CPMensaje.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                }
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                    binding.CPMensaje.setText(""), 2000);
            }else{
                binding.CPMensaje.setText("");
            }
        });


        return root;
    }

}
