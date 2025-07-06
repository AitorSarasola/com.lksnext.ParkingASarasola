package com.lksnext.parkingplantilla.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackBool;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
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

    private void buscarReservasActuales(){
        error.setValue("");
        listaReservas.setValue(new ArrayList<>());
        listaReservas.setValue(null);
        Fecha hoy = Fecha.fechaActual();
        DataRepository.getBookingsForUserBetweenDates(hoy, null, new CallbackList<Reserva>() {
            @Override
            public void onSuccess(List<Reserva> lista) {
                listaReservas.setValue(lista);
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
                listaReservas.setValue(new ArrayList<>());
            }
        });
    }

    private void buscarUltimasReservas(){
        listaReservas.setValue(new ArrayList<>());
        listaReservas.setValue(null);

        Fecha hoy = Fecha.fechaActual();
        hoy.restarDias(1);
        Fecha hace30dias = Fecha.fechaActual();
        hace30dias.restarDias(30);
        DataRepository.getBookingsForUserBetweenDates(hace30dias, hoy, new CallbackList<Reserva>() {
            @Override
            public void onSuccess(List<Reserva> lista) {
                lista.sort((r1, r2) ->
                        r2.getDay().toStringForFirestore().compareTo(r1.getDay().toStringForFirestore())
                );
                listaReservas.setValue(lista);
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue("Error al cargar las reservas.");
                listaReservas.setValue(new ArrayList<>());
            }
        });
    }

    public void cancelarReserva(Reserva reserva, Context context){
        DataRepository.cancelBookingById(reserva.getReservaId(), context, result -> {
            if(result){
                buscarReservas(1);
            }else{
                buscarReservas(0);
                setError("No se ha podido cancelar la reserva.");
            }
        });
    }

    public void add15Min(Reserva reserva, Context context, CallbackBool callback){
        int index = listaReservas.getValue().indexOf(reserva);
        if (index < 0) {
            callback.onResult(false);
            setError("Reserva no encontrada.");
        }
        Reserva reservaLag = listaReservas.getValue().get(index);
        Hora fin = reservaLag.getEndTime();
        //Cambiar para reservas pasada medianoche.
        fin.sumarMinutos(15);
        if(fin.compareTo(new Hora(0,15)) < 0){
            callback.onResult(false);
            setError("La reserva no puede cruzar medianoche.");
        }

        DataRepository.add15minToBookingByID(reserva, context, new Callback() {
            @Override
            public void onSuccess() {
                callback.onResult(true);
            }

            @Override
            public void onFailure() {
                buscarReservas(0);
                error.setValue("No se ha podido alargar la reserva.");
                callback.onResult(false);
            }

            @Override
            public void onFailure(String errorM) {
                buscarReservas(0);
                error.setValue(errorM);
                callback.onResult(false);
            }
        });

    }

    public void buscarReservas(int on){
        if(on != 0)
            setError("");
        if(reservasActuales)
            buscarReservasActuales();
        else
            buscarUltimasReservas();
    }
}
