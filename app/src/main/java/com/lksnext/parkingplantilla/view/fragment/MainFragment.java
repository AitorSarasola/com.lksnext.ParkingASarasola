package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.FragmentMainBinding;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private MainViewModel mainViewModel;

    public MainFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.message.setText("");

        // Inicializar fecha y hora
        this.inicializarFecha();
        this.inicializarHora();

        // Cargar coches
        mainViewModel.cargarCoches();

        // Spiner Tipos De Vehículos Añadir
        ArrayList<String> lagList = new ArrayList<>();
        lagList.add("*Tipo ▼");
        lagList.add("Coche");
        lagList.add("Moto");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, lagList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoVehiculo.setAdapter(adapter);

        //Spiner Tipos De Etiquetas Añadir
        lagList = new ArrayList<>();
        lagList.add("*Etiqueta Máxima ▼");
        lagList.add("Cero Emisiones");
        lagList.add("ECO");
        lagList.add("B");
        lagList.add("C");
        adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, lagList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEtiquetaMedioambiental.setAdapter(adapter);

        // Spiner Cargador Eléctrico o No
        lagList = new ArrayList<>();
        lagList.add("*Preferencia de Cargador ▼");
        lagList.add("Sin Cargador Eléctrico");
        lagList.add("Con Cargador Eléctrico");
        lagList.add("No Me Importa");
        adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, lagList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCargadorElec.setAdapter(adapter);

        // Spiner Apto Para Discapacidad
        lagList = new ArrayList<>();
        lagList.add("*Nivel de Accesibilidad ▼");
        lagList.add("No Apto Para Discapacidad");
        lagList.add("Apto Para Discapacidad");
        lagList.add("No Me Importa");
        adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, lagList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDiscapacidad.setAdapter(adapter);

        // Botón para volver a cargar los coches
        binding.refreshCarsButton.setOnClickListener(v-> mainViewModel.cargarCoches());

        binding.refreshButton.setOnClickListener(v->{
            binding.message.setText("");
            inicializarHora();
            inicializarFecha();
            binding.spinnerCargadorElec.setSelection(0);
            binding.spinnerDiscapacidad.setSelection(0);
            binding.spinnerEtiquetaMedioambiental.setSelection(0);
            binding.spinnerTipoVehiculo.setSelection(0);
            binding.Matricula.setText("");
            mainViewModel.resetSearchOn();
        });

        binding.btnAplicar.setOnClickListener(view -> {
            if(binding.spinnerCoches.getSelectedItemPosition() <= 0){
                mainViewModel.setError("Error No hay ningún coche seleccionado para poder aplicar sus caracteristicas.");
                return;
            }
            binding.message.setText("");
            Car car = mainViewModel.getCoche(binding.spinnerCoches.getSelectedItemPosition() - 1);
            if (car.getType() == Car.Type.COCHE)
                binding.spinnerTipoVehiculo.setSelection(1);
            else
                binding.spinnerTipoVehiculo.setSelection(2);

            if(car.getEtiqueta() == Car.Label.C)
                binding.spinnerEtiquetaMedioambiental.setSelection(4);
            else if(car.getEtiqueta() == Car.Label.B)
                binding.spinnerEtiquetaMedioambiental.setSelection(3);
            else if(car.getEtiqueta() == Car.Label.ECO)
                binding.spinnerEtiquetaMedioambiental.setSelection(2);
            else
                binding.spinnerEtiquetaMedioambiental.setSelection(1);

            if(car.isElectrico())
                binding.spinnerCargadorElec.setSelection(2);
            else
                binding.spinnerCargadorElec.setSelection(1);

            if(car.isParaDiscapacitados())
                binding.spinnerDiscapacidad.setSelection(2);
            else
                binding.spinnerDiscapacidad.setSelection(1);

            binding.Matricula.setText(car.getMatricula());
        });

        mainViewModel.getListaCoches().observe(getViewLifecycleOwner(), listaCoches -> {
            inicializarCoches(listaCoches);
        });
        
        mainViewModel.getSearchOn().observe(getViewLifecycleOwner(), searchOn -> {
            if (searchOn == null) { // No hay búsqueda en curso
                 binding.btnBuscar.setText("Buscar");
                binding.btnBuscar.setAlpha(1f);
                binding.btnBuscar.setEnabled(true);
            } else if(searchOn){ // Hay una búsqueda en curso
                binding.message.setText("");
                binding.btnBuscar.setText("Buscando...");
                binding.btnBuscar.setAlpha(0.5f);
                binding.btnBuscar.setEnabled(false);
            }else{ // Búsqueda finalizada
                binding.btnBuscar.setText("Buscar");
                binding.btnBuscar.setAlpha(1f);
                binding.btnBuscar.setEnabled(true);
                //Lista null -> error
                if(mainViewModel.getListaPlazas().getValue() == null || mainViewModel.getListaPlazas().getValue().size() <= 0){
                    binding.message.setText(mainViewModel.getError().getValue());
                    mainViewModel.resetSearchOn();
                }else{ //Lista con plazas -> ir a resultados
                    Log.d("BUSCARPLAZAS","Hay Plazas!!!");
                    binding.message.setText("BUSQUEDA: "+mainViewModel.getListaPlazas().getValue().get(0).getId());
                    //Ir a la pantalla de resultados + Pasar la lista de plazas
                    //       + Mátricula del coche + Fecha y hora + Descripción de la busqueda
                }
            }
        });
        
        binding.btnBuscar.setOnClickListener(view -> {
            if(binding.spinnerTipoVehiculo.getSelectedItemPosition() == 0 || binding.spinnerEtiquetaMedioambiental.getSelectedItemPosition() == 0
                    || binding.spinnerCargadorElec.getSelectedItemPosition() == 0
                    || binding.spinnerDiscapacidad.getSelectedItemPosition() == 0){
                mainViewModel.setError("Error, debes rellenar todos los campos.");
                return;
            }

            mainViewModel.buscarPlazas(binding.Matricula.getText().toString(),

                    binding.spinnerTipoVehiculo.getSelectedItemPosition()-1,
                    binding.spinnerEtiquetaMedioambiental.getSelectedItemPosition()-1,
                    binding.spinnerCargadorElec.getSelectedItemPosition()-1,
                    binding.spinnerDiscapacidad.getSelectedItemPosition()-1,

                    binding.inDate.getSelectedItemPosition(),
                    binding.inStartTime.getText().toString(),
                    binding.inEndTime.getText().toString()
            );
        });

        /*mainViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if(error != null && !error.isEmpty()){
                binding.message.setText(error);
                binding.message.setVisibility(View.VISIBLE);
            }else{
                binding.message.setVisibility(View.GONE);
            }
        });*/

        return root;
    }

    private void inicializarFecha() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, mainViewModel.createNewListaDias());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.inDate.setAdapter(adapter);
    }

    private void inicializarHora() {
        String[] listaHoras = mainViewModel.getListaHoras();
        binding.inStartTime.setText(listaHoras[0]); // Hora inicial por defecto
        binding.inEndTime.setText(listaHoras[1]); // Hora final por defecto
    }

    private void inicializarCoches(List<String> lista){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCoches.setAdapter(adapter);
    }

}
