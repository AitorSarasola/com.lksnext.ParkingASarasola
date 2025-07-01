package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
public class AddCarViewModel extends ViewModel {
    static MutableLiveData<Boolean> ultimoCocheGuardado = new MutableLiveData<>(null);
    static MutableLiveData<String> error = new MutableLiveData<>(null);
    public MutableLiveData<Boolean> getUltimoCocheGuardado(){
        return ultimoCocheGuardado;
    }

    public void resetUltimoCocheGuardado(){
        ultimoCocheGuardado.setValue(null);
    }

    public MutableLiveData<String> getError(){
        return error;
    }

    public void addCar(String matricula, String type, String label, boolean isParaDiscapacitados, boolean isElectrico){
        ultimoCocheGuardado.setValue(null);
        DataRepository.getInstance().addCar(matricula,type,label,isParaDiscapacitados,isElectrico, new Callback(){
            @Override
            public void onSuccess() {
                //TODO
                ultimoCocheGuardado.setValue(Boolean.TRUE);
            }
            @Override
            public void onFailure(String errorM) {
                //TODO
                error.setValue(errorM);
                ultimoCocheGuardado.setValue(Boolean.FALSE);
            }
            @Override
            public void onFailure(){
                error.setValue("Error, el vehículo ya existe.");
                ultimoCocheGuardado.setValue(Boolean.FALSE);
            }
        });
    }
}
