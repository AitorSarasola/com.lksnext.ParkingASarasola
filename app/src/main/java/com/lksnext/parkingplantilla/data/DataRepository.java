package com.lksnext.parkingplantilla.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.firestore.FirebaseFirestore;

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
        email = deleteLastSpace(email);
        //Realizar petición
        if (email.isEmpty() && pass.isEmpty()){
            callback.onFailure("El email y la contraseña no pueden estar vacíos.");
        }else if (email.isEmpty()){
            callback.onFailure("El email no puede estar vacío.");
        }else if(pass.isEmpty()){
            callback.onFailure("La contraseña no puede estar vacía.");
        }else if(! isValidEmail(email) ){
            callback.onFailure("El email no es válido.");
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(task ->{
                if(task.isSuccessful()){
                    callback.onSuccess();
                }else{
                    callback.onFailure();
                }
            });
        }
    }

    public void register(String user, String email, String pass1, String pass2, Callback callback){
        String user_ = deleteLastSpace(user);
        String email_ = deleteLastSpace(email);
        //Realizar petición
        if (email_.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || user_.isEmpty()){
            callback.onFailure("Debes rellenar todos los campos.");
        }else if(user_.length() < 3 || user_.length() > 20){
            callback.onFailure("El nombre de usuario debe tener entre 3 y 20 caracteres.");
        }else if(!isValidUser(user_)){
            callback.onFailure("El nombre de usuario solo puede contener letras, números y barras bajas.");
        }else if(! isValidEmail(email_) ){
            callback.onFailure("El email_ no es válido.");
        }else if (!pass1.equals(pass2)){
            callback.onFailure("Las contraseñas no coinciden.");
        }else if(pass1.length() < 6) {
            callback.onFailure("La contraseña debe tener al menos 6 caracteres.");
        }else if(! isValidPassword(pass1) ) {
            callback.onFailure("La contraseña contiene caracteres no válidos.");
        }else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            auth.createUserWithEmailAndPassword(email_, pass1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String id = auth.getCurrentUser().getUid();
                    HashMap<String, Object> userMap = new HashMap<String, Object>();
                    userMap.put("name",user_);
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidUser(String user) {
        String userRegex = "^[a-zA-Z0-9_]{3,20}$";
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

    public String deleteLastSpace(String s) {
        if (s != null && !s.isEmpty() && s.charAt(s.length() - 1) == ' ') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }
}
