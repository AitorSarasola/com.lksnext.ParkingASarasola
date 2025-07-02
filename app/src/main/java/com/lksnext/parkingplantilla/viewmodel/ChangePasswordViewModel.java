package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
public class ChangePasswordViewModel extends ViewModel {
    static MutableLiveData<Boolean> sent = new MutableLiveData<>(null);

    static MutableLiveData<String> error = new MutableLiveData<>(null);

    public static LiveData<Boolean> isSent(){
        return sent;
    }

    public static MutableLiveData<String> getError() { return error; }

    public void changePassword(String email){
        sent.setValue(null);
        DataRepository.getInstance().changePassword(email, new Callback() {
            @Override
            public void onSuccess() {
                sent.setValue(Boolean.TRUE);
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
                sent.setValue(Boolean.FALSE);
            }

            @Override
            public void onFailure() {
                error.setValue("Ha habido un error, espera un poco o vuelvelo a intentar con otro email.");
                sent.setValue(Boolean.FALSE);
            }
        });
    }

    public void resetSent() {
        sent.setValue(null);
        error.setValue(null);
    }
}
