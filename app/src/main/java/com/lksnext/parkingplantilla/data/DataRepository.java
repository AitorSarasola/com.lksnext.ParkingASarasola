package com.lksnext.parkingplantilla.data;

import static java.lang.Math.abs;

import android.content.Context;
import android.util.Log;

import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackBool;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.lksnext.parkingplantilla.domain.CallbackList;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.NotificationHelper;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class DataRepository {

    private static DataRepository instance;
    private static final int USERNAME_MAX_LENGTH = 20; // Longitud máxima del nombre de usuario

    private static final String BUSCAR_RESERVAS = "Bookings";
    private static final String IS_CANCELLED = "isCancelled";
    private static final String PLAZA = "parkingSpace";
    private static final String HORA_INICIO = "iniTime";
    private static final String HORA_FINAL = "endTime";

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
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure());
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
        String licensePlateRegex = "^\\d{4}[ -]?[[B-DF-HJ-NP-TV-Z]{3}]{3}$";
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
                    List<Car> lista = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String matricula = doc.getString("Matricula");
                        String type = doc.getString("Type");
                        Boolean isParaDiscapacitados = doc.getBoolean("isParaDiscapacitados");
                        Boolean isElectrico = doc.getBoolean("isElectrico");
                        String label = doc.getString("Label");

                        Car car = new Car(matricula, type, label,Boolean.TRUE.equals(isParaDiscapacitados), Boolean.TRUE.equals(isElectrico));
                        lista.add(car);
                    }
                    callback.onSuccess(lista);
                })
                .addOnFailureListener(e -> callback.onFailure("Error, no se han encontrado coches."));
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

    public static void searchParkingSpacces(Car.Type tipo, List<String> etiquetas, int prefElectrico, int prefAccesivilidad, Fecha fecha, Hora HORA_INICIO, Hora endTime, CallbackList<Plaza> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection(PLAZA)
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
                        isPlazaAvailable(plaza, fecha, HORA_INICIO, endTime,null, disponible -> {
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
    public static void isPlazaAvailable(Plaza plaza, Fecha fecha, Hora iniTime, Hora endTime, String bookingId, CallbackBool callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection(BUSCAR_RESERVAS)
                .whereEqualTo(IS_CANCELLED,false)
                .whereEqualTo(PLAZA, plaza.getId())
                .whereEqualTo("day", fecha.toStringForFirestore());

        if(bookingId != null && !bookingId.isEmpty()) {
            query = query.whereNotEqualTo(FieldPath.documentId(), bookingId); // Excluir la reserva actual si se está editando
        }

        query.get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            boolean disponible = true;

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String iniTimeLag = doc.getString(HORA_INICIO);
                String endTimeLag = doc.getString(HORA_FINAL);
                Boolean isCancelled = doc.getBoolean(IS_CANCELLED);

                if(isCancelled)
                    continue;
                // Si hay solapamiento, la plaza no está disponible
                if (iniTime.toString().compareTo(endTimeLag) < 0 && iniTimeLag.compareTo(endTime.toString()) < 0) {
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

    public static void bookParkingSpace(Plaza plaza,String matricula, Fecha fecha, Hora iniH, Hora finH, Context context, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        isPlazaAvailable(plaza, fecha, iniH, finH, null, disponible -> {
            if (!disponible) {
                callback.onFailure("La plaza no está disponible en el horario seleccionado.");
            }else{
                // Crear un nuevo documento de reserva
                HashMap<String, Object> booking = new HashMap<>();
                booking.put(PLAZA, plaza.getId());
                booking.put("car", matricula);
                booking.put("day", fecha.toStringForFirestore());
                booking.put(HORA_INICIO, iniH.toString());
                booking.put(HORA_FINAL, finH.toString());
                booking.put("userId", userId);
                booking.put(IS_CANCELLED, false);

                db.collection(BUSCAR_RESERVAS).add(booking)
                        .addOnSuccessListener(documentReference -> {
                            String id = documentReference.getId();
                            Reserva reserva = new Reserva(id, userId, matricula, plaza, false, fecha, iniH, finH);
                            notificaciones5y15y30minutos(context, reserva);
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> callback.onFailure("Error al reservar la plaza."));
            }
        });
    }

    public static String validHours(Hora iniH, Hora finH) {
        int timeInMinutes = iniH.diferenciaEnMinutos(finH);
        if(timeInMinutes<0)
            //timeInMinutes = 24*60 + timeInMinutes; // IMPLEMENTAR SI SE QUIERE PERMITIR RESERVAS QUE CRUCEN LA MEDIA NOCHE
            return("La hora de inicio debe ser anterior a la hora de fin.");

        if(timeInMinutes < 5)
            return("La reserva debe durar al menos 5 minutos.");

        if(timeInMinutes > 60*9)
            return("La reserva no puede superar las 9 horas.");

        return null;
    }

    public static void getBookingsForUserBetweenDates(Fecha startDate, Fecha endDate, CallbackList<Reserva> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = db.collection(BUSCAR_RESERVAS)
                .whereEqualTo("userId", userId);

        if (startDate != null)
            query = query.whereGreaterThanOrEqualTo("day", startDate.toStringForFirestore());

        if (endDate != null)
            query = query.whereLessThanOrEqualTo("day", endDate.toStringForFirestore());
        query.get().addOnSuccessListener(querySnapshot -> {
            List<Reserva> reservas = new ArrayList<>();
            List<String> plazaIds = new ArrayList<>();

            for (DocumentSnapshot doc : querySnapshot) {
                String id = doc.getId();
                String matricula = doc.getString("car");
                String plazaId = doc.getString(PLAZA);
                plazaIds.add(plazaId);

                boolean isCancelled = doc.getBoolean(IS_CANCELLED);
                String dayLag = doc.getString("day");
                Fecha day = new Fecha(Fecha.invertirFormatoFecha(dayLag));
                Hora iniTime = new Hora(doc.getString(HORA_INICIO));
                Hora endTime = new Hora(doc.getString(HORA_FINAL));

                Reserva reserva = new Reserva(id, userId, matricula, null, isCancelled, day, iniTime, endTime);
                reservas.add(reserva);
            }
            if (reservas.isEmpty()) {
                callback.onSuccess(reservas); // nada que resolver
                return;
            }
            // Ahora resolvemos plazas de forma asíncrona
            final int[] contador = {0}; // control de completados

            for (int i = 0; i < plazaIds.size(); i++) {
                String plazaId = plazaIds.get(i);
                int index = i;

                getPlazaById(plazaId, new CallbackList<Plaza>() {
                    @Override
                    public void onSuccess(List<Plaza> plazas) {
                        if (!plazas.isEmpty()) {
                            reservas.get(index).setPlaza(plazas.get(0));
                        }
                        contador[0]++;
                        if (contador[0] == plazaIds.size()) {
                            // todas las plazas resueltas
                            callback.onSuccess(reservas);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // si falla alguna plaza, igualmente avanzamos
                        contador[0]++;
                        if (contador[0] == plazaIds.size()) {
                            callback.onSuccess(reservas);
                        }
                    }
                });
            }

        }).addOnFailureListener(e -> callback.onFailure("Error al cargar reservas."));
    }


    public static void getPlazaById(String plazaId, CallbackList<Plaza> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(PLAZA)
                .document(plazaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String id = documentSnapshot.getId();
                        String tipo = documentSnapshot.getString("Type");
                        String unekLabel = documentSnapshot.getString("Label").toUpperCase().replace(' ','_');
                        boolean isElec = documentSnapshot.getBoolean("isElectrico");
                        boolean isDis = documentSnapshot.getBoolean("isParaDiscapacitados");
                        Plaza plaza = new Plaza(id, Car.Type.valueOf(tipo), Car.Label.valueOf(unekLabel), isElec, isDis);
                        List<Plaza> plazas = new ArrayList<>();
                        plazas.add(plaza);
                        callback.onSuccess(plazas);
                    } else {
                        callback.onFailure("La Plaza No Existe.");
                    }
                })
                .addOnFailureListener(e->Log.e("GETALLBOOKINGS","Error: "+e));
    }

    public static void cancelBookingById(String bookingId, Context context, CallbackBool callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference bookingRef = db.collection(BUSCAR_RESERVAS).document(bookingId);
        bookingRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onResult(false);
                        return;
                    }

                    String dayStr = documentSnapshot.getString("day"); // YYYY-MM-DD
                    dayStr = Fecha.invertirFormatoFecha(dayStr);
                    String endTimeStr = documentSnapshot.getString(HORA_FINAL); // HH:mm

                    if (dayStr == null || endTimeStr == null) {
                        callback.onResult(false);
                        return;
                    }

                    // Parse fecha
                    Fecha fecha = new Fecha(dayStr);
                    Hora horaFinal = new Hora(endTimeStr);
                    Fecha hoy = Fecha.fechaActual();
                    Hora ahora = Hora.horaActual();
                    //CAMBIAR PARA IMPLEMENTAR RESERVAS QUE PASEN MEDIANOCHE

                    if (!(fecha.compareTo(hoy)<0 ||(fecha.compareTo(hoy)<=0 && horaFinal.compareTo(ahora)<0))) {
                        // la reserva es futura → podemos cancelarla
                        bookingRef.update(IS_CANCELLED, true)
                                .addOnSuccessListener(unused -> {
                                    cancelaNotificaciones5y15y30minutos(context, bookingId);
                                    callback.onResult(true);
                                })
                                .addOnFailureListener(e -> callback.onResult(false));
                    } else {
                        // no se cancela porque ya pasó
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onResult(false);
                    Log.d("CANCELARERRROR","66666");
                });
    }

    public static void add15minToBookingByID(Reserva booking, Context context, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String bookingId = booking.getReservaId();

        DocumentReference bookingRef = db.collection(BUSCAR_RESERVAS).document(bookingId);
        bookingRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onFailure();
                        return;
                    }
                    Boolean isCancelled = documentSnapshot.getBoolean(IS_CANCELLED);
                    if(isCancelled.equals(Boolean.TRUE)){
                        callback.onFailure("La reserva está cancelada.");
                        return;
                    }

                    String dayStr = Fecha.invertirFormatoFecha(documentSnapshot.getString("day")); // YYYY-MM-DD
                    String iniTimeStr = documentSnapshot.getString(HORA_INICIO); // YYYY-MM-DD
                    String endTimeStr = documentSnapshot.getString(HORA_FINAL); // HH:mm

                    if (iniTimeStr == null || endTimeStr == null || dayStr == null) {
                        callback.onFailure();
                        return;
                    }

                    // Parse fecha
                    Fecha fecha = new Fecha(dayStr);
                    Hora horaInicial = new Hora(iniTimeStr);
                    Hora horaFinal = new Hora(endTimeStr);
                    horaFinal.sumarMinutos(15);
                    if(horaFinal.compareTo(new Hora(0,15)) < 0){
                        callback.onFailure("La reserva no puede cruzar medianoche.");
                        return;
                    }
                    if (abs(horaInicial.diferenciaEnMinutos(horaFinal)) > 7*60) {
                        callback.onFailure("La reserva no puede superar 7 horas.");
                        return;
                    }
                    Fecha hoy = Fecha.fechaActual();
                    Hora ahora = Hora.horaActual();
                    if (fecha.compareTo(hoy)<0 || (fecha.compareTo(hoy)<=0 && horaFinal.compareTo(ahora)<0)) {
                        callback.onFailure("La reserva ya ha terminado.");
                        return;
                    }
                    booking.setEndTime(horaFinal);
                    isPlazaAvailable(booking.getPlaza(),fecha, horaInicial, horaFinal,bookingId, task->{
                        if(task){
                            bookingRef.update(HORA_FINAL, horaFinal.toString())
                                    .addOnSuccessListener(unused -> {
                                        booking.setEndTime(horaFinal); booking.setIniTime(horaInicial); booking.setDay(fecha);
                                        retrasarNotificaciones15min(context,booking);
                                        callback.onSuccess();
                                    })
                                    .addOnFailureListener(e -> callback.onFailure());
                        }else{
                            callback.onFailure("No se puede alargar la reserva, hay otra reserva después.");
                        }
                    });
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    public static void notificaciones5y15y30minutos(Context context, Reserva reserva){
        String titulo;
        String texto;
        String id;

        Fecha hoy = Fecha.fechaActual();
        Hora ahora = Hora.horaActual();
        long difMinutos = abs(ahora.diferenciaEnMinutos(reserva.getEndTime())) + 24*60*abs(hoy.diferenciaEnDias(reserva.getDay()));
        long duracion = abs(reserva.getIniTime().diferenciaEnMinutos(reserva.getEndTime()));
        if(difMinutos > 30 && duracion > 30){
            titulo = "Tu reserva termina en 30 minutos.";
            texto = "Tu reserva de la plaza Nº "+reserva.getPlaza().getId()+" con el vehículo "+reserva.getCar()+" termina en 30 minutos.";
            id = reserva.getReservaId()+"30";
            NotificationHelper.scheduleNotification(context, id, titulo, texto, difMinutos-5);
        }

        if(difMinutos > 15 && duracion > 15){
            titulo = "Tu reserva termina en 15 minutos.";
            texto = "Tu reserva de la plaza Nº "+reserva.getPlaza().getId()+" con el vehículo "+reserva.getCar()+" termina en 15 minutos.";
            id = reserva.getReservaId()+"15";
            NotificationHelper.scheduleNotification(context, id, titulo, texto, difMinutos-15);
        }

        if(difMinutos > 5 && duracion > 5){
            titulo = "Tu reserva termina en 5 minutos.";
            texto = "Tu reserva de la plaza Nº "+reserva.getPlaza().getId()+" con el vehículo "+reserva.getCar()+" termina en 5 minutos.";
            id = reserva.getReservaId()+"5";
            NotificationHelper.scheduleNotification(context, id, titulo, texto, difMinutos-5);
        }
    }

    public static void cancelaNotificaciones5y15y30minutos(Context context, String reservaId){
        cancelReservationNotification(context, reservaId+"5");
        cancelReservationNotification(context, reservaId+"15");
        cancelReservationNotification(context,reservaId+"30");
    }

    public static void retrasarNotificaciones15min(Context context, Reserva reserva){
        String reservaId = reserva.getReservaId();
        cancelaNotificaciones5y15y30minutos(context, reservaId);

        notificaciones5y15y30minutos(context,reserva);
    }

    private static void cancelReservationNotification(Context context, String reservaId) {
        WorkManager.getInstance(context).cancelUniqueWork(reservaId);
    }

}
