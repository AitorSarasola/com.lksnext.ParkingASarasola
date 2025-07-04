package com.lksnext.parkingplantilla.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    MutableLiveData<List<Car>> listaCoches = new MutableLiveData<>();
    MutableLiveData<Boolean> logout = new MutableLiveData<>(false);
    MutableLiveData<Boolean> sent = new MutableLiveData<>(null);

    MutableLiveData<String> error = new MutableLiveData<>(null);

    public MutableLiveData<List<Car>> getListaCoches(){
        return listaCoches;
    }
    public MutableLiveData<Boolean> isLogout() {
        return logout;
    }
    public MutableLiveData<Boolean> isSent() {
        return sent;
    }
    public MutableLiveData<String> getError() {
        return error;
    }

    public void cargarCoches() {
        DataRepository.getInstance().cargarCoches(new CallbackList<Car>() {
            @Override
            public void onSuccess(List<Car> lista) {
                listaCoches.setValue(lista);
            }

            @Override
            public void onFailure(String errorM) {
                listaCoches.setValue(new ArrayList<Car>());
            }
        });
    }


    public void deleteCar(Car carToDelete) {
        DataRepository.getInstance().deleteCar(carToDelete, new Callback() {
            @Override
            public void onSuccess() {
                cargarCoches();
            }

            @Override
            public void onFailure(String errorM) {
                error.setValue(errorM);
            }

            @Override
            public void onFailure() {
                error.setValue("Error al eliminar el coche, intenta de nuevo.");
            }
        });
    }

    public void changeCurrentUserPass() {
        sent.setValue(null);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            String email = user.getEmail();
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
                    error.setValue("Error, espera un poco y reintentalo. ");
                    sent.setValue(Boolean.FALSE);
                }
            });
        }else{
            error.setValue("Error, no hay usuario conectado.");
            sent.setValue(Boolean.FALSE);
        }
    }

    public void logout() {
        logout.setValue(true);
        new Handler(Looper.getMainLooper()).postDelayed(() ->
            logout.setValue(false), 2000);
    }

}
