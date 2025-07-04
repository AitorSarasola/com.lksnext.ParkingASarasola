package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivitySearchResultsBinding;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.view.fragment.CarItemAdapter;
import com.lksnext.parkingplantilla.view.fragment.ParkingSpaceAdapter;
import com.lksnext.parkingplantilla.viewmodel.SearchResultsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private ActivitySearchResultsBinding binding;
    private SearchResultsViewModel searchResultsViewModel;
    private RecyclerView recyclerView;

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

        Car.Type tipoVehiculo = (Car.Type) getIntent().getSerializableExtra("tipoVehiculo");

        if (tipoVehiculo != null && tipoVehiculo == Car.Type.MOTO){
            binding.imageVehicle.setImageResource(R.drawable.ic_motorbike);
        }

        Car.Label etiqueta = (Car.Label) getIntent().getSerializableExtra("etiqueta");
        int prefElec = getIntent().getIntExtra("prefElec",2);
        int prefAccesibilidad = getIntent().getIntExtra("prefAccesibilidad",2);

        Fecha fecha = (Fecha) new Fecha((String) getIntent().getStringExtra("fecha"));
        String iniHora = getIntent().getStringExtra("inicioH");
        Hora inicioH = new Hora(iniHora);
        String finHora = getIntent().getStringExtra("finH");
        Hora finH = new Hora(finHora);

        //Inicializamos la descripción de la búsqueda
        inicializarDescripcion(tipoVehiculo, etiqueta, prefElec, prefAccesibilidad, fecha, inicioH, finH);

        List<Plaza> plazas = (ArrayList<Plaza>) getIntent().getSerializableExtra("listaPlazas");
        searchResultsViewModel.setListaPlazas(plazas);

        searchResultsViewModel.getListaPlazas().observe(this, plazasList -> {
            if (plazasList == null) {
                recyclerView.setAdapter(null);
                binding.listaVaciaM.setText("Car-gando...");
            }else if(plazasList.isEmpty())
                binding.listaVaciaM.setText("No Hay Plazas Disponibles Con Estos Parámetros.");
            else {
                binding.listaVaciaM.setText("");
                ParkingSpaceAdapter plazaAdapter = new ParkingSpaceAdapter(plazasList, plaza ->
                    searchResultsViewModel.book(plaza, fecha, inicioH, finH, matricula));
                binding.recyclerViewPlazas.setAdapter(plazaAdapter);
            }
        });

        searchResultsViewModel.getError().observe(this, errorMessage -> {;
            if (errorMessage != null) {
                binding.Mensaje.setText(errorMessage);
            } else {
                binding.Mensaje.setText("");
            }
        });

        binding.refreshButton.setOnClickListener(v->{
            searchResultsViewModel.setListaPlazas(null);
            searchResultsViewModel.buscarPlazas(binding.inMatricula.getText().toString(), tipoVehiculo, etiqueta,
                    prefElec, prefAccesibilidad, fecha, inicioH, finH);
        });

        binding.btnReturn.setOnClickListener(v->{
            Intent intent = new Intent(SearchResultsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void inicializarDescripcion(Car.Type tipoVehiculo, Car.Label etiqueta, int prefElec, int prefAccesibilidad, Fecha fecha, Hora inicioH, Hora finH) {
        String descripcion = "Plaza para " + tipoVehiculo.toString().toLowerCase() + " - ";
        if (etiqueta == Car.Label.CERO_EMISIONES)
            descripcion += "Etiqueta Máxima\nCero Emisiones\n";
        else
            descripcion += "Etiqueta Máxima: " + etiqueta.toString() + "\n";

        descripcion += (prefElec == 0 ? "Sin Cargador Eléctrico." :
                prefElec == 1 ? "Con Cargador Eléctrico." : "Sin Preferencia por Cargador Eléctrico.") + "\n";

        descripcion += (prefAccesibilidad == 0 ? "No Apto Para Discapacitados." :
                prefAccesibilidad == 1 ? "Apto Para Discapacitados." : "Sin Preferencia De Accesibilidad.") + "\n\n";

        descripcion += fecha.toString() + ":   " + inicioH.toString() + " - " + finH.toString();
        binding.txtDescription.setText(descripcion);
    }

    }
