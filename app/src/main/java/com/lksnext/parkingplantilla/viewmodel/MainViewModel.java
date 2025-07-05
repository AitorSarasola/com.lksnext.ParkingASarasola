package com.lksnext.parkingplantilla.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    MutableLiveData<List<String>> listaCoches = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<Boolean> searchOn = new MutableLiveData<>(null);

    MutableLiveData<String> error = new MutableLiveData<>(null);
    MutableLiveData<List<Plaza>> listaPlazas = new MutableLiveData<>(new ArrayList<>());
    List<String> unekListaFechas = new ArrayList<>();

    private List<Car> allCoches;

    public String getFechaIndex(int i){
        return unekListaFechas.get(i);
    }
    public MutableLiveData<Boolean> getSearchOn() {
        return searchOn;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<List<String>> getListaCoches() {
        return listaCoches;
    }
    public MutableLiveData<List<Plaza>> getListaPlazas() {
        return listaPlazas;
    }

    public List<String> createNewListaDias() {
        List<String> listaDias = new ArrayList<>();
        unekListaFechas = new ArrayList<>();
        Fecha hoy = Fecha.fechaActual();
        for (int i = 0; i <= 7; i++) {
            listaDias.add(hoy.toString());
            unekListaFechas.add(hoy.toStringForApi());
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
                if(carNames.isEmpty()) {
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
                listaVacia.add("*No hay coches registrados");
                listaCoches.setValue(listaVacia);
                allCoches = null;
            }
        });
    }

    /* prefElectrico 0 -> No, 1 -> Sí, 2 -> No importa*/
    /* prefAccesivilidad 0 -> No, 1 -> Sí, 2 -> No importa*/
    public void buscarPlazas(String matricula, int tipoIndex, int etiquetaIndex, int prefElectrico, int prefAccesivilidad, int fechaIndex, String iniTimeS, String endTimeS, Callback calllback) {
        searchOn.setValue(true);
        error.setValue("");
        listaPlazas.setValue(null);

        if(!DataRepository.isValidLicensePlate(matricula)){
            error.setValue("Matrícula no válida.");
            searchOn.setValue(false);
            return;
        }

        Car.Type tipo = Car.Type.values()[tipoIndex];
        List<String> etiquetas = Car.getValidLabels(Car.Label.values()[etiquetaIndex]);
        Fecha fecha = new Fecha(unekListaFechas.get(fechaIndex));

        Hora iniTime;
        Hora endTime;
        try{
            iniTime = new Hora(iniTimeS);
            endTime = new Hora(endTimeS);
        }catch (Exception e){
            error.setValue("Formato de hora no válido.");
            searchOn.setValue(false);
            return;
        }

        Fecha currentDate = Fecha.fechaActual();
        Hora currentTime = Hora.horaActual();

        Boolean cambioFecha = false;
        // Ajustamos fecha y hora si la hora de inicio ya ha pasado
        if (fecha.compareTo(currentDate) < 0 ||
                (fecha.compareTo(currentDate) == 0 && iniTime.compareTo(currentTime) < 0)) {
            iniTime = currentTime;
            fecha = currentDate;
            cambioFecha = true;
        }

        String lag = DataRepository.validHours(iniTime, endTime);
        if(lag != null) {
            if(cambioFecha) {
                calllback.onFailure();
                if(lag.equals("La hora de inicio debe ser anterior a la hora de fin."))
                    error.setValue("La reserva debe ser anterior a la fecha actual.");
            }else
                error.setValue(lag);
            searchOn.setValue(false);
            return;
        }

        DataRepository.searchParkingSpacces(tipo, etiquetas, prefElectrico, prefAccesivilidad, fecha, iniTime, endTime, new CallbackList<Plaza>() {
            @Override
            public void onSuccess(List<Plaza> lista) {
                if (lista == null || lista.isEmpty()) {
                    error.setValue("No se han encontrado plazas disponibles con esas características.");
                } else {
                    listaPlazas.setValue(lista);
                }
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        searchOn.setValue(false), 1000);
            }

            @Override
            public void onFailure(String errorM) {
                searchOn.setValue(false);
                error.setValue(errorM);
            }
        });
    }

    public void resetSearchOn() {
        searchOn.setValue(null);
        listaPlazas.setValue(new ArrayList<>());
    }

    public Car getCoche(int i) {
        return allCoches.get(i);
    }

    public void setError(String e){
        error.setValue(e);
    }
}
