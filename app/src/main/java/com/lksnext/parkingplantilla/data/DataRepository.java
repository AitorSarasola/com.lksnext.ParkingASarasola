package com.lksnext.parkingplantilla.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackBool;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class DataRepository {

    private static DataRepository instance;
    private static final int USERNAME_MAX_LENGTH = 20; // Longitud máxima del nombre de usuario
    private DataRepository(){

    }

    //Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    //Petición del login.
    public void login(String email, String pass, Callback callback){
        String emailLag = deleteLastSpace(email);
        //Realizar petición
        if (emailLag.isEmpty() && pass.isEmpty()){
            callback.onFailure("El email y la contraseña no pueden estar vacíos.");
        }else if (emailLag.isEmpty()){
            callback.onFailure("El email no puede estar vacío.");
        }else if(pass.isEmpty()){
            callback.onFailure("La contraseña no puede estar vacía.");
        }else if(! isValidEmail(emailLag) ){
            callback.onFailure("El email no es válido.");
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLag,pass).addOnCompleteListener(task ->{
                if(task.isSuccessful()){
                    callback.onSuccess();
                }else{
                    callback.onFailure();
                }
            });
        }
    }

    public void register(String user, String email, String pass1, String pass2, Callback callback){
        String userLag = deleteLastSpace(user);
        String emailLag = deleteLastSpace(email);
        //Realizar petición
        if (emailLag.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || userLag.isEmpty()){
            callback.onFailure("Debes rellenar todos los campos.");
        }else if(userLag.length() < 3 || userLag.length() > USERNAME_MAX_LENGTH){
            callback.onFailure("El nombre de usuario debe tener entre 3 y "+USERNAME_MAX_LENGTH+" caracteres.");
        }else if(!isValidUser(userLag)){
            callback.onFailure("El nombre de usuario solo puede contener letras, números, barra bajas y espacios.");
        }else if(! isValidEmail(emailLag) ){
            callback.onFailure("El email no es válido.");
        }else if (!pass1.equals(pass2)){
            callback.onFailure("Las contraseñas no coinciden.");
        }else if(pass1.length() < 6) {
            callback.onFailure("La contraseña debe tener al menos 6 caracteres.");
        }else if(! isValidPassword(pass1) ) {
            callback.onFailure("La contraseña contiene caracteres no válidos.");
        }else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            auth.createUserWithEmailAndPassword(emailLag, pass1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String id = auth.getCurrentUser().getUid();
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("name",userLag);
                    firestore.collection("users").document(id).set(userMap)
                            .addOnSuccessListener(aVoid ->{
                                callback.onSuccess();
                            }).addOnFailureListener(e -> {
                                callback.onFailure();
                            });
                } else {
                    callback.onFailure();
                }
            });
        }
    }

    public static void changePassword(String email, Callback callback){
        String emailLag = deleteLastSpace(email);
        if(emailLag.isEmpty()){
            callback.onFailure("El email no puede estar vacío.");
        }else if(!isValidEmail(emailLag)){
            callback.onFailure("El email no es válido.");
        }else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailLag).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    callback.onSuccess();
                }else{
                    callback.onFailure();
                }
            });
        }
    }

    public static void addCar(String matricula0, String type, String label, boolean disabled, boolean electric, Callback callback){
        String matricula = deleteLastSpace(matricula0);
        matricula = matricula.toUpperCase();
        if(matricula.isEmpty() || type.equals("*Selecciona un tipo de vehículo ▼") || label.equals("*Selecciona una etiqueta ▼")){
            callback.onFailure("Debes rellenar todos los campos");
        }else if(!isValidLicensePlate(matricula)){
            callback.onFailure("La matrícula no es válida");}
        else{
            String matriculaLag = standarizeLicensePlate(matricula);
            FirebaseAuth auth = FirebaseAuth.getInstance();

            CollectionReference cochesRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("Coches");
            // Comprobar si la matrícula ya existe
            cochesRef.whereEqualTo("Matricula", matriculaLag).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    callback.onFailure();
                } else {
                    // Si no existe, guardar el coche
                    HashMap<String, Object> coche = new HashMap<>();
                    coche.put("Matricula", matriculaLag);
                    coche.put("Type", type);
                    coche.put("Label", label);
                    coche.put("isParaDiscapacitados", disabled);
                    coche.put("isElectrico", electric);

                    cochesRef.add(coche)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure("Error al guardar el coche. Intentelo de nuevo más tarde."));
                }
            });
        }
    }

    public static boolean checkLogged(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidUser(String user) {
        String userRegex = "^[a-zA-Z0-9_ ]{2,"+(USERNAME_MAX_LENGTH-1)+"}[a-zA-Z0-9_]$";
        Pattern pattern = Pattern.compile(userRegex);
        Matcher matcher = pattern.matcher(user);
        return matcher.matches();
    }

    private boolean isValidPassword(String pass) {
        String userRegex = "^[a-zA-Z0-9!\\ \"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]{3,20}$";
        Pattern pattern = Pattern.compile(userRegex);
        Matcher matcher = pattern.matcher(pass);
        return matcher.matches();
    }
    public static boolean isValidLicensePlate(String plate) {
        String licensePlateRegex = "^\\d{4}[ -]?[A-PR-Z]{3}$";
        Pattern pattern = Pattern.compile(licensePlateRegex);
        Matcher matcher = pattern.matcher(plate);
        return matcher.matches();
    }

    private static String standarizeLicensePlate(String plate) {
        // Eliminar espacios y guiones
        plate = plate.replaceAll("[ -]", "");
        // Formatear a XXXX-YYY
        return plate.substring(0, 4) + "-" + plate.substring(4);
    }

    public static String deleteLastSpace(String s) {
        if (s != null && !s.isEmpty() && s.charAt(s.length() - 1) == ' ') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }
    public void cargarCoches(CallbackList<Car> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("Coches")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Car> L = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String matricula = doc.getString("Matricula");
                        String type = doc.getString("Type");
                        Boolean isParaDiscapacitados = doc.getBoolean("isParaDiscapacitados");
                        Boolean isElectrico = doc.getBoolean("isElectrico");
                        String label = doc.getString("Label");

                        Car car = new Car(matricula, type, label,Boolean.TRUE.equals(isParaDiscapacitados), Boolean.TRUE.equals(isElectrico));
                        L.add(car);
                    }
                    callback.onSuccess(L);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Error, no se han encontrado coches.");
                });
    }

    public void deleteCar(Car carToDelete, Callback callback) {
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
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Error al eliminar el coche."));
                        break;  // Asumimos que solo hay uno con esa matrícula
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Error al eliminar el coche."));
    }

    public static void searchParkingSpacces(String matricula, Car.Type tipo, List<String> etiquetas, int prefElectrico, int prefAccesivilidad, Fecha fecha, Hora iniTime, Hora endTime, CallbackList<Plaza> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("ParkingSpace")
                .whereEqualTo("Type", tipo.toString())
                .whereIn("Label", etiquetas);

        if (prefElectrico != 2)
            query = query.whereEqualTo("isElectrico", prefElectrico == 1);

        if (prefAccesivilidad != 2)
            query = query.whereEqualTo("isParaDiscapacitados", prefAccesivilidad == 1);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Plaza> plazas = new ArrayList<>();
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();

                    if (docs.isEmpty()) {
                        callback.onSuccess(plazas); // No hay plazas
                        return;
                    }

                    AtomicInteger pendientes = new AtomicInteger(docs.size());

                    for (DocumentSnapshot doc : docs) {
                        String id = doc.getId();
                        String unekLabel = doc.getString("Label").toUpperCase().replace(' ','_');
                        boolean isElec = doc.getBoolean("isElectrico");
                        boolean isDis = doc.getBoolean("isParaDiscapacitados");
                        Plaza plaza = new Plaza(id, tipo, Car.Label.valueOf(unekLabel), isElec, isDis);
                        isPlazaAvailable(plaza, fecha, iniTime, endTime, disponible -> {
                            if (disponible) {
                                plazas.add(plaza);
                            }
                            if (pendientes.decrementAndGet() == 0) {
                                callback.onSuccess(plazas); // Cuando terminen todas
                            }
                        });
                    }
                    callback.onSuccess(plazas);
                })
                .addOnFailureListener(e -> callback.onFailure("Error al buscar plazas."));
    }

    //NOTA para Rervas que crucen medianoche: Coger el dia anterior y el siguiente y dependiendo de si cruca o no hacer los calculos corespondientes.
    public static void isPlazaAvailable(Plaza plaza, Fecha fecha, Hora iniTime, Hora endTime, CallbackBool callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("ISPLAZAAVAIL","FECHA: "+fecha.toStringForFirestore()+" INI: "+iniTime.toString()+" END: "+endTime.toString());

        db.collection("Bookings")
                .whereEqualTo("isCancelled",false)
                .whereEqualTo("parkingSpace", plaza.getId())
                .whereEqualTo("day", fecha.toStringForFirestore())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean disponible = true;
                    Log.d("ISPLAZAAVAIL","F");
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String iniTimeLag = doc.getString("iniTime");
                        String endTimeLag = doc.getString("endTime");
                        Log.d("ISPLAZAAVAIL","INI: "+iniTimeLag+" END: "+endTimeLag);
                        // Si hay solapamiento, la plaza no está disponible
                        if (iniTime.toString().compareTo(endTimeLag) < 0 && iniTimeLag.compareTo(endTime.toString()) < 0) {
                            Log.d("ISPLAZAAVAIL","Plaza no disponible por solapamiento."+(iniTime.toString().compareTo(endTimeLag) < 0)+" - "+(endTime.toString().compareTo(iniTimeLag) > 0));
                            disponible = false;
                            break;
                        }
                    }
                    callback.onResult(disponible);
                })
                .addOnFailureListener(e -> {
                    Log.e("ISPLAZAAVAIL", "Error al comprobar disponibilidad de la plaza.", e);
                    callback.onResult(false); // Por seguridad, asumimos no disponible
                });
    }


}
