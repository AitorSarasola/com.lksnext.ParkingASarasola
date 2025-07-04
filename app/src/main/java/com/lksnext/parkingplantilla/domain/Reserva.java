package com.lksnext.parkingplantilla.domain;

import java.util.ArrayList;
import java.util.List;

public class Reserva {

    private String reservaId;
    private String user;
    private String car;
    private String plaza;
    private boolean isCancelled;
    private Fecha day;
    private Hora iniTime;
    private Hora endTime;

    public Reserva(String reservaId, String user, String car, String plaza, boolean isCancelled, Fecha day, Hora iniTime, Hora endTime) {
        this.reservaId = reservaId;
        this.user = user;
        this.car = car;
        this.plaza = plaza;
        this.isCancelled = isCancelled;
        this.day = day;
        this.iniTime = iniTime;
        this.endTime = endTime;
    }

    public String getUser() {
        return user;
    }

    public String getCar() {
        return car;
    }

    public String getPlaza() {
        return plaza;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Fecha getDay() {
        return day;
    }

    public Hora getIniTime() {
        return iniTime;
    }

    public Hora getEndTime() {
        return endTime;
    }

    public void setEndTime(Hora endTime) {
        this.endTime = endTime;
    }

    public Reserva(String reservaId, String user, String car, String plaza, boolean isCancelled, String day, String iniTime, String endTime) {
        this.reservaId = reservaId;
        this.user = user;
        this.car = car;
        this.plaza = plaza;
        this.isCancelled = isCancelled;
        this.day = new Fecha(Fecha.invertirFormatoFecha(day));
        this.iniTime = new Hora(iniTime);
        this.endTime = new Hora(endTime);
    }
}
