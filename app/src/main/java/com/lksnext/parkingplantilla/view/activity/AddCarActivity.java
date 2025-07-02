package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.ActivityAddCarBinding;
import com.lksnext.parkingplantilla.viewmodel.AddCarViewModel;
import com.lksnext.parkingplantilla.R;

import java.util.ArrayList;

public class AddCarActivity extends AppCompatActivity{
    private ActivityAddCarBinding binding;
    private AddCarViewModel addCarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de registro
        binding = ActivityAddCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addCarViewModel = new ViewModelProvider(this).get(AddCarViewModel.class);

        binding.errorM.setText("");
        addCarViewModel.resetUltimoCocheGuardado();

        //Spiner Tipos De Vehículos Añadir
        ArrayList<String> tiposVehiculo = new ArrayList<>();
        tiposVehiculo.add("*Selecciona un tipo de vehículo ▼");
        tiposVehiculo.add("Coche");
        tiposVehiculo.add("Moto");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, // Contexto
                android.R.layout.simple_spinner_item, // Layout para cada ítem
                tiposVehiculo
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoVehiculo.setAdapter(adapter);

        //Spiner Tipos De Etiquetas Añadir
        ArrayList<String> tiposLabel = new ArrayList<>();
        tiposLabel.add("*Selecciona una etiqueta ▼");
        tiposLabel.add("Cero Emisiones");
        tiposLabel.add("ECO");
        tiposLabel.add("B");
        tiposLabel.add("C");
        adapter = new ArrayAdapter<>(
                this, // Contexto
                android.R.layout.simple_spinner_item, // Layout para cada ítem
                tiposLabel
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLabel.setAdapter(adapter);

        binding.buttonRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(AddCarActivity.this, MainActivity.class);
            intent.putExtra("navigateToProfile", true);
            startActivity(intent);
        });

        binding.buttonGuardar.setOnClickListener(v->{
            String matricula = binding.editTextMatricula.getText().toString();
            String tipo = binding.spinnerTipoVehiculo.getSelectedItem().toString();
            String label = binding.spinnerLabel.getSelectedItem().toString();
            boolean isParaDiscapacitados = binding.checkboxIsDisabled.isChecked();
            boolean isElectrico = binding.checkboxIsElectric.isChecked();

            addCarViewModel.addCar(matricula, tipo, label, isParaDiscapacitados, isElectrico);
        });

        addCarViewModel.getUltimoCocheGuardado().observe(this, ultimoCocheGuardado -> {
            int color;
            if(ultimoCocheGuardado != null){
                if(ultimoCocheGuardado){
                    color = ContextCompat.getColor(this, R.color.light_blue);
                    binding.errorM.setTextColor(color);
                    binding.errorM.setText("Vehículo añadido correctamente");
                    //Reset valores
                    binding.spinnerTipoVehiculo.setSelection(0);
                    binding.spinnerLabel.setSelection(0);
                    binding.editTextMatricula.setText("");
                    binding.checkboxIsDisabled.setChecked(false);
                    binding.checkboxIsElectric.setChecked(false);
                }else{
                    color = ContextCompat.getColor(this, R.color.red);
                    binding.errorM.setTextColor(color);
                    binding.errorM.setText(addCarViewModel.getError().getValue());
                }
            }else{
                binding.errorM.setText("");
            }
        });
    }
}
