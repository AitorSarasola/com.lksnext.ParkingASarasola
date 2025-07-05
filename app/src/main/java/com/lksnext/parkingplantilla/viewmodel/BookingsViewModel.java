package com.lksnext.parkingplantilla.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.R;
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
        Log.d("54B9E0","77777777");
        error.setValue("");
        listaReservas.setValue(new ArrayList<>());
        Log.d("54B9E0","8888888");
        Fecha hoy = Fecha.fechaActual();
        DataRepository.getBookingsForUserBetweenDates(hoy, null, new CallbackList<Reserva>() {
            @Override
            public void onSuccess(List<Reserva> lista) {
                Log.d("54B9E0","9999999999");
                listaReservas.setValue(lista);
            }

            @Override
            public void onFailure(String errorM) {
                Log.d("54B9E0","101010010010101");
                error.setValue(errorM);
            }
        });
    }

    private void buscarUltimasReservas(){
        listaReservas.setValue(new ArrayList<>());

        Fecha hoy = Fecha.fechaActual();
        hoy.restarDias(1);
        Fecha hace30dias = Fecha.fechaActual();
        hace30dias.restarDias(30);
        DataRepository.getBookingsForUserBetweenDates(hace30dias, hoy, new CallbackList<Reserva>() {
            @Override
            public void onSuccess(List<Reserva> lista) {
                listaReservas.setValue(lista);
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
                    buscarReservas(1);
                }else{
                    setError("No se ha podido cancelar la reserva.");
                }
            }
        });
    }

    public void añadir15Min(Reserva reserva, CallbackBool callback){
        int index = listaReservas.getValue().indexOf(reserva);
        if (index < 0) {
            callback.onResult(false);
            setError("Reserva no encontrada.");
        }
        Reserva reservaLag = listaReservas.getValue().get(index);
        Fecha fecha = reservaLag.getDay();
        Hora ini = reservaLag.getIniTime();
        Hora fin = reservaLag.getEndTime();
        //Cambiar para reservas pasada medianoche.
        fin.sumarMinutos(15);
        if(fin.compareTo(new Hora(0,15)) < 0){
            callback.onResult(false);
            setError("La reserva no puede cruzar medianoche.");
        }

        DataRepository.add15minToBookingByID(reserva, new Callback() {
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
        //Log.d("ERRORADD15", "11111");
        if(on != 0)
            setError("");
        if(reservasActuales)
            buscarReservasActuales();
        else
            buscarUltimasReservas();
    }
}
