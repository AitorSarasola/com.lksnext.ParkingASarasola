package com.lksnext.parkingplantilla.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivitySearchResultsBinding;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.view.fragment.ParkingSpaceAdapter;
import com.lksnext.parkingplantilla.viewmodel.SearchResultsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private ActivitySearchResultsBinding binding;
    private SearchResultsViewModel searchResultsViewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de registro
        binding = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchResultsViewModel = new SearchResultsViewModel();

        recyclerView = binding.recyclerViewPlazas;
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        binding.Mensaje.setText("");
        String matricula = getIntent().getStringExtra("matricula");
        binding.inMatricula.setText(matricula);

        Car.Type tipoVehiculo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            tipoVehiculo = getIntent().getSerializableExtra("tipoVehiculo", Car.Type.class);
        else
            tipoVehiculo = (Car.Type) getIntent().getSerializableExtra("tipoVehiculo");

        if (tipoVehiculo != null && tipoVehiculo == Car.Type.MOTO)
            binding.imageVehicle.setImageResource(R.drawable.ic_motorbike);

        Car.Label etiqueta;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            etiqueta = getIntent().getSerializableExtra("etiqueta", Car.Label.class);
        else
            etiqueta = (Car.Label) getIntent().getSerializableExtra("etiqueta");

        int prefElec = getIntent().getIntExtra("prefElec",2);
        int prefAccesibilidad = getIntent().getIntExtra("prefAccesibilidad",2);

        Fecha fecha = new Fecha((String) getIntent().getStringExtra("fecha"));
        searchResultsViewModel.setFecha(fecha);
        String iniHora = getIntent().getStringExtra("inicioH");
        Hora inicioH = new Hora(iniHora);
        searchResultsViewModel.setIniH(inicioH);
        String finHora = getIntent().getStringExtra("finH");
        Hora finH = new Hora(finHora);
        searchResultsViewModel.setFinH(finH);

        //Inicializamos la descripción de la búsqueda
        inicializarDescripcion(tipoVehiculo, etiqueta, prefElec, prefAccesibilidad);

        List<Plaza> plazas;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            plazas = (ArrayList<Plaza>) getIntent().getSerializableExtra("listaPlazas", ArrayList.class);
        else
            plazas = (ArrayList<Plaza>) getIntent().getSerializableExtra("listaPlazas");
        searchResultsViewModel.setListaPlazas(plazas);

        searchResultsViewModel.getListaPlazas().observe(this, plazasList -> {
            if (plazasList == null) {
                recyclerView.setAdapter(null);
                binding.listaVaciaM.setText("Car-gando...");
            }else if(plazasList.isEmpty())
                binding.listaVaciaM.setText("No Hay Plazas Disponibles Con Estos Parámetros.");
            else {
                binding.listaVaciaM.setText("");
                ParkingSpaceAdapter plazaAdapter = new ParkingSpaceAdapter(plazasList, (plaza, itemView) ->
                    searchResultsViewModel.book(plaza, matricula, this, new Callback() {
                        @Override
                        public void onSuccess() {
                            Button btn = itemView.findViewById(R.id.btnBook);
                            TextView txtMensaje = itemView.findViewById(R.id.txtMessage);

                            btn.setVisibility(View.GONE);
                            txtMensaje.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure() {
                            Button btn = itemView.findViewById(R.id.btnBook);
                            btn.setEnabled(false);
                            btn.setAlpha(0.5f);
                        }

                        @Override
                        public void onFailure(String errorM) {
                            Button btn = itemView.findViewById(R.id.btnBook);
                            btn.setEnabled(false);
                            btn.setAlpha(0.5f);
                        }
                    }));
                binding.recyclerViewPlazas.setAdapter(plazaAdapter);
            }
        });

        searchResultsViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                binding.Mensaje.setText(errorMessage);
            } else {
                binding.Mensaje.setText("");
            }
        });

        // Observamos los cambios en fecha, inicioH y finH para actualizar la descripción
        searchResultsViewModel.getFecha().observe(this, fechaValue -> {
            if (fechaValue != null) {
                inicializarDescripcion(tipoVehiculo, etiqueta, prefElec, prefAccesibilidad);
            }
        });
        searchResultsViewModel.getIniH().observe(this, inicioHValue -> {
            if (inicioHValue != null) {
                inicializarDescripcion(tipoVehiculo, etiqueta, prefElec, prefAccesibilidad);
            }
        });
        searchResultsViewModel.getFinH().observe(this, finHValue -> {
            if (finHValue != null) {
                inicializarDescripcion(tipoVehiculo, etiqueta, prefElec, prefAccesibilidad);
            }
        });

        binding.refreshButton.setOnClickListener(v->{
            searchResultsViewModel.setListaPlazas(null);
            searchResultsViewModel.buscarPlazas(binding.inMatricula.getText().toString(), tipoVehiculo, etiqueta,
                    prefElec, prefAccesibilidad);
        });

        binding.btnReturn.setOnClickListener(v->{
            Intent intent = new Intent(SearchResultsActivity.this, MainActivity.class);
            setResult(Activity.RESULT_OK, intent); //startActivity(intent);
            finish();
        });
    }

    public void inicializarDescripcion(Car.Type tipoVehiculo, Car.Label etiqueta, int prefElec, int prefAccesibilidad) {
        String descripcion = "Plaza para " + tipoVehiculo.toString().toLowerCase() + " - ";
        if (etiqueta == Car.Label.CERO_EMISIONES)
            descripcion += "Etiqueta Máxima\nCero Emisiones\n";
        else
            descripcion += "Etiqueta Máxima: " + etiqueta.toString() + "\n";

        if (prefElec == 0) {
            descripcion += "Sin Cargador Eléctrico.\n";
        } else if (prefElec == 1) {
            descripcion += "Con Cargador Eléctrico.\n";
        } else {
            descripcion += "Sin Preferencia por Cargador Eléctrico.\n";
        }

        if (prefAccesibilidad == 0) {
            descripcion += "No Apto Para Discapacitados.\n\n";
        } else if (prefAccesibilidad == 1) {
            descripcion += "Apto Para Discapacitados.\n\n";
        } else {
            descripcion += "Sin Preferencia De Accesibilidad.\n\n";
        }

        Fecha fecha = searchResultsViewModel.getFecha().getValue();
        Hora inicioH = searchResultsViewModel.getIniH().getValue();
        Hora finH = searchResultsViewModel.getFinH().getValue();

        descripcion += fecha.toString() + ":   " + inicioH.toString() + " - " + finH.toString();
        binding.txtDescription.setText(descripcion);
    }

    }
