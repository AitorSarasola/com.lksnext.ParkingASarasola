package com.lksnext.parkingplantilla.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class LoginViewModel extends ViewModel {

    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de inicio de sesión
    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    static MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<Boolean> isLogged(){
        return logged;
    }
    public LiveData<String> getError() { return error; }
    public void failedLogin(String e){
        logged.setValue(false);
        error.setValue(e);
    }

    public void loginDone(){
        logged.setValue(true);
    }
    public void checkLogged(){
        if(DataRepository.checkLogged())
            logged.setValue(true);
    }

    public void loginUser(String email, String password) {
        //Clase para comprobar si los datos de inicio de sesión son correctos o no
        DataRepository.getInstance().login(email, password, new Callback() {
            //En caso de que el login sea correcto, que se hace
            @Override
            public void onSuccess() {
                //TODO
                logged.setValue(Boolean.TRUE);
            }

            //En caso de que el login sea incorrecto, que se hace
            @Override
            public void onFailure(String errorM) {
                //TODO
                error.setValue(errorM);
                logged.setValue(Boolean.FALSE);
            }
            @Override
            public void onFailure(){
                error.setValue("Ha habido un error, email o contraseña incorrectos.");
                logged.setValue(Boolean.FALSE);
            }
        });

    }
}

