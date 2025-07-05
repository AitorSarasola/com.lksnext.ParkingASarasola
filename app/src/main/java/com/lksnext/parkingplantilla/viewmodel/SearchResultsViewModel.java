package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;

import java.util.List;

public class SearchResultsViewModel extends ViewModel {
    MutableLiveData<List<Plaza>> listaPlazas = new MutableLiveData<>();
    MutableLiveData<String> error = new MutableLiveData<>(null);

    public MutableLiveData<List<Plaza>> getListaPlazas() {
        return listaPlazas;
    }
    public MutableLiveData<String> getError() {
        return error;
    }
    public void setListaPlazas(List<Plaza> plazas) {
        listaPlazas.setValue(plazas);
    }
    public void setError(String errorMessage) {
        error.setValue(errorMessage);
    }

    public void book(Plaza plaza, Fecha fecha, Hora inicioH, Hora finH, String matricula) {
        error.setValue("");
        if (!DataRepository.isValidLicensePlate(matricula)){
            error.setValue("La Matricula No Es Válida.");
            return;
        }
        // Implementación de la lógica para reservar una plaza
        // Aquí se puede llamar a un repositorio o servicio para realizar la reserva
        // y actualizar el estado de la UI según sea necesario.
    }

    public void buscarPlazas(String matricula, Car.Type tipo, Car.Label etiqueta, int prefElectrico, int prefAccesivilidad, Fecha fecha, Hora iniH, Hora finH) {
        error.setValue("");
        listaPlazas.setValue(null);

        if(!DataRepository.isValidLicensePlate(matricula)){
            error.setValue("Matrícula no válida.");
            return;
        }

        List<String> etiquetas = Car.getValidLabels(etiqueta);

        int timeInMinutes = iniH.diferenciaEnMinutos(finH);
        if(timeInMinutes<0){
            //timeInMinutes = 24*60 + timeInMinutes; // IMPLEMENTAR SI SE QUIERE PERMITIR RESERVAS QUE CRUCEN LA MEDIA NOCHE
            error.setValue("La hora de inicio debe ser anterior a la hora de fin.");
            return;
        }
        if(timeInMinutes < 5){
            error.setValue("La reserva debe durar al menos 5 minutos.");
            return;
        }

        if(timeInMinutes > 60*9){
            error.setValue("La reserva no puede superar las 9 horas.");
            return;
        }

        DataRepository.searchParkingSpacces(tipo, etiquetas, prefElectrico, prefAccesivilidad, fecha, iniH, finH, new CallbackList<Plaza>() {
            @Override
            public void onSuccess(List<Plaza> lista) {
                listaPlazas.setValue(lista);
                error.setValue("");
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
            }
        });
    }
}
