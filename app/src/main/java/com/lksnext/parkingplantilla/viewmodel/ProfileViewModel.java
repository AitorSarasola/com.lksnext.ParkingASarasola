package com.lksnext.parkingplantilla.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.domain.Car;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    MutableLiveData<ArrayList<Car>> listaCoches = new MutableLiveData<ArrayList<Car>>();

    public MutableLiveData<ArrayList<Car>> getListaCoches(){
        return listaCoches;
    }

    public void cargarCoches() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("FirebaseCoches","Start");

        db.collection("users")
                .document(userId)
                .collection("Coches")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Car> L = new ArrayList<>();
                    Log.d("FirebaseCoches","OnSuccessListener");
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String matricula = doc.getString("Matricula");
                        Log.d("FirebaseCoches","Matricula: "+matricula);
                        String type = doc.getString("Type");
                        Boolean isParaDiscapacitados = doc.getBoolean("isParaDiscapacitados");
                        Boolean isElectrico = doc.getBoolean("isElectrico");
                        String label = doc.getString("Label");

                        Car car = new Car(matricula, type, label,isParaDiscapacitados != null ? isParaDiscapacitados : false ,isElectrico != null ? isElectrico : false);
                        L.add(car);
                    }
                    listaCoches.setValue(L);
                    Log.d("FirestoreCoches", "Coches cargados correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreCoches", "Error al cargar coches: " + e.getMessage());
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
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Coche eliminado correctamente");
                                    cargarCoches();  // Recargar la lista
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error eliminando coche: " + e.getMessage());
                                });
                        break;  // Asumimos que solo hay uno con esa matrícula
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error buscando coche: " + e.getMessage());
                });
    }

}
