package com.lksnext.parkingplantilla.view.fragment;

import com.lksnext.parkingplantilla.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.databinding.FragmentBookingsBinding;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.viewmodel.BookingsViewModel;

public class BookingsFragment extends Fragment {
    private FragmentBookingsBinding binding;
    private RecyclerView recyclerView;
    private BookingsViewModel bookingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerViewBookings;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingsViewModel = new ViewModelProvider(this).get(BookingsViewModel.class);

        bookingsViewModel.setError("");

        bookingsViewModel.buscarReservas(1);

        binding.btnReservasActuales.setOnClickListener(view -> {
            bookingsViewModel.setError("");
            bookingsViewModel.setReservasActuales(true);
            int orange = ContextCompat.getColor(requireContext(), R.color.orange);
            int white = ContextCompat.getColor(requireContext(), R.color.design_default_color_background);

            // Establecer fondos correctamente
            binding.btnUltimos30Dias.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.item_frame_small));
            binding.btnReservasActuales.setBackgroundColor(orange);

            // Establecer colores de texto
            binding.btnReservasActuales.setTextColor(white);
            binding.btnUltimos30Dias.setTextColor(orange);

            bookingsViewModel.buscarReservas(1);
        });

        binding.btnUltimos30Dias.setOnClickListener(view -> {
            bookingsViewModel.setError("");
            bookingsViewModel.setReservasActuales(false);
            int orange = ContextCompat.getColor(requireContext(), R.color.orange);
            int white = ContextCompat.getColor(requireContext(), R.color.design_default_color_background);

            // Establecer fondos correctamente
            binding.btnReservasActuales.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.item_frame_small));
            binding.btnUltimos30Dias.setBackgroundColor(orange);

            // Establecer colores de texto
            binding.btnUltimos30Dias.setTextColor(white);
            binding.btnReservasActuales.setTextColor(orange);

            bookingsViewModel.buscarReservas(1);
        });

        bookingsViewModel.getListaReservas().observe(getViewLifecycleOwner(), listaReservas->{
            if (listaReservas == null)
                binding.listaVaciaM.setText("Car-gando...");
            else if(listaReservas.isEmpty())
                binding.listaVaciaM.setText("No Hay Reservas\nDisponibles");
            else{
                binding.listaVaciaM.setText("");
            }

            if (listaReservas != null) {
            BookingAdapter adapter = new BookingAdapter(listaReservas, new BookingAdapter.OnBookEditListener() {
                @Override
                public void onCancel(Reserva reserva) { bookingsViewModel.cancelarReserva(reserva, getActivity());}

                @Override
                public void onAdd15Min(Reserva reserva) {
                    bookingsViewModel.add15Min(reserva, getActivity(), task ->{
                        if(task){
                            bookingsViewModel.buscarReservas(0);
                            binding.txtMensaje.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue));
                            binding.txtMensaje.setText("Se ha postpuesto 15 minutos la hora final de la reserva.");
                        }
                    });
                }
            });
            recyclerView.setAdapter(adapter);

            }
        });

        bookingsViewModel.getError().observe(getViewLifecycleOwner(), e->{
            if(e == null || e.equals("")){
                binding.txtMensaje.setText("");
            }else{
                binding.txtMensaje.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                binding.txtMensaje.setText(e);
            }
        });

        return root;
    }
}
