package com.lksnext.parkingplantilla.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;

import java.util.List;

public class SearchResultsViewModel extends ViewModel {
    MutableLiveData<List<Plaza>> listaPlazas = new MutableLiveData<>();
    MutableLiveData<String> error = new MutableLiveData<>(null);
    MutableLiveData<Fecha> fecha = new MutableLiveData<>();
    MutableLiveData<Hora> iniH = new MutableLiveData<>(Hora.horaActual());
    MutableLiveData<Hora> finH = new MutableLiveData<>(Hora.horaActual());

    public MutableLiveData<Fecha> getFecha() {
        return fecha;
    }
    public MutableLiveData<Hora> getIniH() {
        return iniH;
    }
    public MutableLiveData<Hora> getFinH() {
        return finH;
    }
    public void setFecha(Fecha fecha) {
        this.fecha.setValue(fecha);
    }
    public void setIniH(Hora iniH) {
        this.iniH.setValue(iniH);
    }
    public void setFinH(Hora finH) {
        this.finH.setValue(finH);
    }

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

    public void book(Plaza plaza, String matricula, Context context, Callback callback) {
        error.setValue("");
        if (!DataRepository.isValidLicensePlate(matricula)){
            error.setValue("La Matricula No Es Válida.");
            return;
        }

        Hora iniHour = iniH.getValue();
        Hora finHour = finH.getValue();
        Fecha currentDate = Fecha.fechaActual();
        Hora currentTime = Hora.horaActual();

        // Ajustamos fecha y hora si la hora de inicio ya ha pasado
        if (fecha.getValue().compareTo(currentDate) < 0 ||
                (fecha.getValue().compareTo(currentDate) == 0 && iniHour.compareTo(currentTime) < 0)) {
            iniHour = currentTime;
            iniH.setValue(currentTime);
            fecha.setValue(currentDate);
        }

        String lag = DataRepository.validHours(iniHour, finHour);
        if(lag != null) {
            error.setValue(lag);
            return;
        }

        DataRepository.bookParkingSpace(plaza, matricula, fecha.getValue(), iniHour, finHour, context, new Callback() {
            @Override
            public void onSuccess() {
                error.setValue("");
                callback.onSuccess();
                // Aquí se puede actualizar la UI o notificar al usuario de que la reserva fue exitosa
            }
            @Override
            public void onFailure() {
                error.setValue("Se ha producido un error al reservar la plaza.");
                callback.onFailure();
            }
            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
                callback.onFailure(errorM);
            }
        });
        // Implementación de la lógica para reservar una plaza
        // Aquí se puede llamar a un repositorio o servicio para realizar la reserva
        // y actualizar el estado de la UI según sea necesario.
    }

    public void buscarPlazas(String matricula, Car.Type tipo, Car.Label etiqueta, int prefElectrico, int prefAccesivilidad) {
        error.setValue("");
        listaPlazas.setValue(null);

        if(!DataRepository.isValidLicensePlate(matricula)){
            error.setValue("Matrícula no válida.");
            return;
        }

        List<String> etiquetas = Car.getValidLabels(etiqueta);

        Hora iniHour = iniH.getValue();
        Hora finHour = finH.getValue();
        Fecha currentDate = Fecha.fechaActual();
        Hora currentTime = Hora.horaActual();

        // Ajustamos fecha y hora si la hora de inicio ya ha pasado
        if (fecha.getValue().compareTo(currentDate) < 0 ||
                (fecha.getValue().compareTo(currentDate) == 0 && iniHour.compareTo(currentTime) < 0)) {
            iniHour = currentTime;
            iniH.setValue(currentTime);
            fecha.setValue(currentDate);
        }
        String lag = DataRepository.validHours(iniHour, finHour);
        if(lag != null) {
            error.setValue(lag);
            return;
        }

        DataRepository.searchParkingSpacces(tipo, etiquetas, prefElectrico, prefAccesivilidad, fecha.getValue(), iniHour, finHour, new CallbackList<Plaza>() {
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
