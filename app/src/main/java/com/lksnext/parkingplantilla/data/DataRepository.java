package com.lksnext.parkingplantilla.data;

import com.lksnext.parkingplantilla.domain.Callback;
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
    public String login(String email, String pass, Callback callback){
        try {
            //Realizar petición
            if (email.isEmpty() && pass.isEmpty()){
                throw new IllegalArgumentException("El email y la contraseña no pueden estar vacíos.");
            }else if (email.isEmpty()){
                throw new IllegalArgumentException("El email no puede estar vacío.");
            }else if(pass.isEmpty()){
                throw new IllegalArgumentException("La contraseña no puede estar vacía.");
            }else if(! isValidEmail(email) ){
                throw new IllegalArgumentException("El email no es válido.");
            }
            callback.onSuccess();
            return null;
        } catch (Exception e){
            callback.onFailure();
            return e.getMessage();
        }
    }

    public String register(String user, String email, String pass1, String pass2, Callback callback){
        try {
            //Realizar petición
            if (email.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || user.isEmpty()){
                throw new IllegalArgumentException("Debes rellenar todos los campos.");
            }else if(user.length() < 3 || user.length() > 20){
                throw new IllegalArgumentException("El nombre de usuario debe tener entre 3 y 20 caracteres.");
            }else if(!isValidUser(user)){
                throw new IllegalArgumentException("El nombre de usuario solo puede contener letras, números y barras bajas.");
            }else if(! isValidEmail(email) ){
                throw new IllegalArgumentException("El email no es válido.");
            }else if (!pass1.equals(pass2)){
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }else if(pass1.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
            }else if(! isValidPassword(pass1) ) {
                throw new IllegalArgumentException("La contraseña contiene caracteres no válidos.");
            }
            callback.onSuccess();
            return null;
        } catch (Exception e){
            callback.onFailure();
            return e.getMessage();
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
}
