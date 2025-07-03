package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    MutableLiveData<List<String>> listaCoches = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<Boolean> searchOn = new MutableLiveData<>(null);

    MutableLiveData<String> error = new MutableLiveData<>(null);

    private List<Car> allCoches;

    public MutableLiveData<Boolean> getSearchOn() {
        return searchOn;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<List<String>> getListaCoches() {
        return listaCoches;
    }

    public List<String> getListaDias() {
        List<String> listaDias = new ArrayList<>();
        Fecha hoy = Fecha.fechaActual();
        for (int i = 0; i <= 7; i++) {
            listaDias.add(hoy.toString());
            hoy.sumarDias(1);
        }
        return listaDias;
    }

    public String[] getListaHoras() {
        String[] listaHoras = new String[2];
        listaHoras[0] = Hora.horaActual().toString();
        Hora lag = Hora.horaActual();
        lag.sumarMinutos(60*3);
        listaHoras[1] = lag.toString();
        return listaHoras;
    }

    public void cargarCoches() {
        DataRepository.getInstance().cargarCoches(new CallbackList<Car>() {
            @Override
            public void onSuccess(List<Car> lista) {
                List<String> carNames = new ArrayList<>();
                for (Car car : lista) {
                    String carInfo = car.getType().toString()+": "+car.getMatricula();
                    carNames.add(carInfo);
                }
                listaCoches.setValue(carNames);
                if(carNames.size() <= 0) {
                    List<String> listaVacia = new ArrayList<>();
                    listaVacia.add("*No hay coches registrados");
                    listaCoches.setValue(listaVacia);
                    allCoches = null;
                }else{
                    allCoches = lista;
                    carNames.add(0, "*Selecciona un coche ▼");
                }
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
                List<String> listaVacia = new ArrayList<>();
                listaVacia.add("No hay coches registrados");
                listaCoches.setValue(listaVacia);
                allCoches = null;
            }
        });
    }

    public void resetSearchOn() {
        searchOn.setValue(null);
    }

    public Car getCoche(int i) {
        return allCoches.get(i);
    }
}
