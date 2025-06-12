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
                throw new IllegalArgumentException("The email and the password can not be empty");
            }else if (email.isEmpty()){
                throw new IllegalArgumentException("The email can not be empty");
            }else if(pass.isEmpty()){
                throw new IllegalArgumentException("The password can not be empty");
            }else if(! isValidEmail(email) ){
                throw new IllegalArgumentException("The email is not valid");
            }
            callback.onSuccess();
            return "";
        } catch (Exception e){
            callback.onFailure();
            return e.getMessage();
        }
    }

    public String register(String user, String email, String pass1, String pass2, Callback callback){
        try {
            //Realizar petición
            if (email.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || user.isEmpty()){
                throw new IllegalArgumentException("You must fill all the fields");
            }else if(user.length() < 3 || user.length() > 20){
                throw new IllegalArgumentException("The username must be between 3 and 20 characters long");
            }else if(!isValidUser(user)){
                throw new IllegalArgumentException("The username can only contain letters, numbers, and underscores");
            }else if (!pass1.equals(pass2)){
                throw new IllegalArgumentException("The passwords do not match");
            }else if(pass1.length() < 6){
                throw new IllegalArgumentException("The password must be at least 6 characters long");
            }else if(! isValidEmail(email) ){
                throw new IllegalArgumentException("The email is not valid");
            }
            callback.onSuccess();
            return "";
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
}
