package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class RegisterViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de registro
    // Por ejemplo, un LiveData para el email, contraseña y usuario
    static MutableLiveData<Boolean> signedUp = new MutableLiveData<>(null);
    static MutableLiveData<String> error = new MutableLiveData<>(null);

    public static LiveData<Boolean> isSignedUp(){
        return signedUp;
    }

    public static MutableLiveData<String> getError() { return error; }

    public void registerUser(String user, String email, String password1, String password2) {
        signedUp.setValue(null); // Reiniciar el estado de signedUp
        //Clase para comprobar si los datos de inicio de sesión son correctos o no
        DataRepository.getInstance().register(user, email, password1, password2, new Callback() {
            //En caso de que el login sea correcto, que se hace
            @Override
            public void onSuccess() {
                signedUp.setValue(Boolean.TRUE);
            }

            //En caso de que el login sea incorrecto, que se hace
            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
                signedUp.setValue(Boolean.FALSE);
            }
            @Override
            public void onFailure(){
                error.setValue("Ha habido un error, prueba otro email o contraseña.");
                signedUp.setValue(Boolean.FALSE);
            }
        });
    }
}
