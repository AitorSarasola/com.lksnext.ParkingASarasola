package com.lksnext.parkingplantilla.viewmodel;

import android.health.connect.datatypes.RespiratoryRateRecord;
import android.view.View;

import androidx.appcompat.view.menu.MenuView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackBool;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;

import java.util.ArrayList;
import java.util.List;

public class BookingsViewModel extends ViewModel {
    MutableLiveData<List<Reserva>> listaReservas = new MutableLiveData<>(null);
    MutableLiveData<String> error = new MutableLiveData<>(null);
    private boolean reservasActuales = true;

    public void setReservasActuales(boolean val){
        this.reservasActuales = val;
    }

    public MutableLiveData<List<Reserva>> getListaReservas(){
        return listaReservas;
    }

    public MutableLiveData<String> getError(){
        return error;
    }

    public void setError(String error){
        this.error.setValue(error);
    }

    public void buscarReservasActuales(){
        error.setValue("");
        listaReservas.setValue(new ArrayList<>());

        Fecha hoy = Fecha.fechaActual();
        DataRepository.getBookingsForUserBetweenDates(hoy, null, new CallbackList<Reserva>() {
            @Override
            public void onSuccess(List<Reserva> lista) {
                listaReservas.setValue(lista);
                setError("");
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
            }
        });
    }

    public void buscarUltimasReservas(){
        error.setValue("");
        listaReservas.setValue(new ArrayList<>());

        Fecha hoy = Fecha.fechaActual();
        hoy.restarDias(1);
        Fecha hace30dias = Fecha.fechaActual();
        hace30dias.restarDias(30);
        DataRepository.getBookingsForUserBetweenDates(hace30dias, hoy, new CallbackList<Reserva>() {
            @Override
            public void onSuccess(List<Reserva> lista) {
                listaReservas.setValue(lista);
                setError("");
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue("Error al cargar las reservas.");
            }
        });
    }

    public void cancelarReserva(Reserva reserva){
        DataRepository.cancelBookingById(reserva.getReservaId(), new CallbackBool() {
            @Override
            public void onResult(boolean result) {
                if(result){
                    if(reservasActuales)
                        buscarReservasActuales();
                    else
                        buscarUltimasReservas();
                    setError("");
                }else{
                    setError("No se ha podido cancelar la reserva.");
                }
            }
        });
    }

    public void añadir15Min(Reserva reserva, CallbackBool callback){
        //TODO
    }
}
