package com.lksnext.parkingplantilla.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Car;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    MutableLiveData<ArrayList<Car>> listaCoches = new MutableLiveData<>();
    MutableLiveData<Boolean> logout = new MutableLiveData<>(false);
    MutableLiveData<Boolean> sent = new MutableLiveData<>(null);

    MutableLiveData<String> error = new MutableLiveData<>(null);

    public MutableLiveData<ArrayList<Car>> getListaCoches(){
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("Coches")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Car> L = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String matricula = doc.getString("Matricula");
                        String type = doc.getString("Type");
                        Boolean isParaDiscapacitados = doc.getBoolean("isParaDiscapacitados");
                        Boolean isElectrico = doc.getBoolean("isElectrico");
                        String label = doc.getString("Label");

                        Car car = new Car(matricula, type, label,Boolean.TRUE.equals(isParaDiscapacitados), Boolean.TRUE.equals(isElectrico));
                        L.add(car);
                    }
                    listaCoches.setValue(L);
                })
                .addOnFailureListener(e -> {
                    listaCoches.setValue(new ArrayList<>());
                });
    }


    public void deleteCar(Car carToDelete) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Referencia a la subcolección "Coches" del usuario
        CollectionReference cochesRef = db.collection("users").document(userId).collection("Coches");

        // Buscar el documento que tenga la misma matrícula
        cochesRef.whereEqualTo("Matricula", carToDelete.getMatricula())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        // Eliminar el documento encontrado
                        doc.getReference().delete()
                                .addOnSuccessListener(aVoid -> cargarCoches())
                                .addOnFailureListener(e -> Log.d("ProfileViewModel", "Error al eliminar el coche: " + e.getMessage()));
                        break;  // Asumimos que solo hay uno con esa matrícula
                    }
                })
                .addOnFailureListener(e -> {
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
            logout.setValue(null), 2000);
    }

}
