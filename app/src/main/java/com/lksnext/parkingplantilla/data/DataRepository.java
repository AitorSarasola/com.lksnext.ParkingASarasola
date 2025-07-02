package com.lksnext.parkingplantilla.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class DataRepository {

    private static DataRepository instance;
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
        String email_lag = deleteLastSpace(email);
        //Realizar petición
        if (email_lag.isEmpty() && pass.isEmpty()){
            callback.onFailure("El email y la contraseña no pueden estar vacíos.");
        }else if (email_lag.isEmpty()){
            callback.onFailure("El email no puede estar vacío.");
        }else if(pass.isEmpty()){
            callback.onFailure("La contraseña no puede estar vacía.");
        }else if(! isValidEmail(email_lag) ){
            callback.onFailure("El email no es válido.");
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email_lag,pass).addOnCompleteListener(task ->{
                if(task.isSuccessful()){
                    callback.onSuccess();
                }else{
                    callback.onFailure();
                }
            });
        }
    }

    public void register(String user, String email, String pass1, String pass2, Callback callback){
        String user_lag = deleteLastSpace(user);
        String email_lag = deleteLastSpace(email);
        //Realizar petición
        if (email_lag.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || user_lag.isEmpty()){
            callback.onFailure("Debes rellenar todos los campos.");
        }else if(user_lag.length() < 3 || user_lag.length() > USERNAME_MAX_LENGTH){
            callback.onFailure("El nombre de usuario debe tener entre 3 y "+USERNAME_MAX_LENGTH+" caracteres.");
        }else if(!isValidUser(user_lag)){
            callback.onFailure("El nombre de usuario solo puede contener letras, números, barra bajas y espacios.");
        }else if(! isValidEmail(email_lag) ){
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

            auth.createUserWithEmailAndPassword(email_lag, pass1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String id = auth.getCurrentUser().getUid();
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("name",user_lag);
                    firestore.collection("users").document(id).set(userMap)
                            .addOnSuccessListener(aVoid ->{
                                Log.d("Firestore", "Datos guardados correctamente");
                                callback.onSuccess();
                            }).addOnFailureListener(e -> {
                                Log.e("Firestore","Error: ",e);
                                callback.onFailure();
                            });
                } else {
                    callback.onFailure();
                }
            });
        }
    }

    public static void changePassword(String email, Callback callback){
        String email_lag = deleteLastSpace(email);
        if(email_lag.isEmpty()){
            callback.onFailure("El email no puede estar vacío.");
            callback.onFailure("El email no es válido.");
        }else if(!isValidEmail(email_lag)){
        }else{
                if(task.isSuccessful()){
                    Log.d("CHANGEPASS", "BIEN");
            FirebaseAuth.getInstance().sendPasswordResetEmail(email_lag).addOnCompleteListener(task -> {
                    callback.onSuccess();
                }else{
                    Log.d("CHANGEPASS", "MAL");
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
            String matricula_lag = standarizeLicensePlate(matricula);
            FirebaseAuth auth = FirebaseAuth.getInstance();

            CollectionReference cochesRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("Coches");
            // Comprobar si la matrícula ya existe
            cochesRef.whereEqualTo("Matricula", matricula_lag).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    callback.onFailure();
                } else {
                    // Si no existe, guardar el coche
                    HashMap<String, Object> coche = new HashMap<>();
                    coche.put("Matricula", matricula_lag);
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidUser(String user) {
        String userRegex = "^[a-zA-Z0-9_ ]{3,"+USERNAME_MAX_LENGTH+"}$";
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
    private static boolean isValidLicensePlate(String plate) {
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
}
