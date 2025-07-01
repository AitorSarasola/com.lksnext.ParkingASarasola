package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentProfileBinding;
import com.lksnext.parkingplantilla.view.activity.AddCarActivity;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;

    private ProfileViewModel profileViewModel;
    private boolean doubleClick = false;

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
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String name = documentSnapshot.getString("name");
                            binding.helloName.setText("TU PERFIL\n"+name);
                            Log.d("Firestore", "Nombre: " + name);
                        } else {
                            Log.d("Firestore", "Documento no existe");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Firestore", "Error al leer documento", e);
                    }
                });


        profileViewModel.getListaCoches().observe(getViewLifecycleOwner(), listaCoches->{
            if (listaCoches.equals(null) || listaCoches.isEmpty()){
                binding.listaVaciaM.setText("No Hay Coches\nDisponibles");
            }else{
                binding.listaVaciaM.setText("");
            }

            if (listaCoches != null) {
                CarAdapter cocheAdapter = new CarAdapter(listaCoches, car -> {
                    // Aquí avisamos al ViewModel que elimine el coche
                    profileViewModel.deleteCar(car);
                });
                recyclerView.setAdapter(cocheAdapter);
            }
        });

        binding.refreshButton.setOnClickListener(v->{
            profileViewModel.cargarCoches();
        });

        binding.changePassword.setOnClickListener(v ->{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null){
                String email = user.getEmail();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task->{
                            if(task.isSuccessful()){
                                int color = ContextCompat.getColor(getActivity(), R.color.light_blue);
                                binding.CPMensaje.setTextColor(color);
                                binding.CPMensaje.setText("Mensaje Envidado. ");
                            }else{
                                int color = ContextCompat.getColor(getActivity(), R.color.red);
                                binding.CPMensaje.setTextColor(color);
                                binding.CPMensaje.setText("Error, espera un poco y reintentalo. ");
                            }
                        });
            }else
                Log.e("ChangePassword", "UserNotAuth");
        } );

        binding.logoutButton.setOnClickListener(v -> {
            if(doubleClick){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }else{
                binding.singoutText.setText("CERRAR\nSESIÓN");
                doubleClick = true;
            }
        });

        binding.addCar.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), AddCarActivity.class);
            startActivity(intent);
        });

        return root;
    }

}
