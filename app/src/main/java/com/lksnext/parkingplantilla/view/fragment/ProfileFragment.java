package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentProfileBinding;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.CarAdapter;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;
    private CarAdapter cocheAdapter;
    private ArrayList<Car> listaCoches;

    public ProfileFragment() {
        // Constructor vacío necesario
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerViewCoches;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaCoches = new ArrayList<>();
        listaCoches.add(new Car("1234ABC", Car.Type.Coche, Car.Label.B, false));
        listaCoches.add(new Car("5678DEF", Car.Type.Moto, Car.Label.ECO, false));
        listaCoches.add(new Car("9999XYZ", Car.Type.Coche_Para_Discapacitados, Car.Label.Cero_Emisiones, true));

        cocheAdapter = new CarAdapter(listaCoches);
        recyclerView.setAdapter(cocheAdapter);

        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return root;
    }
}
