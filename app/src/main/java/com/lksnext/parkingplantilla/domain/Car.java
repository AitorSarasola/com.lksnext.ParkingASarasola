package com.lksnext.parkingplantilla.domain;

import android.widget.Switch;

public class Car {
    public enum Type {
        Coche,
        Moto,
        Coche_Para_Discapacitados
    }

    public enum Label {
        Cero_Emisiones,
        ECO,
        B,
        C
    }
    private String matricula;
    private Type tipo;
    private Label etiqueta;
    private boolean isElectrico;

    public Car(String matricula, Type tipo, Label etiqueta, boolean isElectrico){
        this.matricula = matricula;
        this.tipo=tipo;
        this.etiqueta = etiqueta;
        this.isElectrico = isElectrico;
    }

    public Car(String matricula, String tipo, String etiqueta, boolean isElectrico){
        this.matricula = matricula;
        switch(tipo){
            case "Coche": this.tipo = Type.Coche;
            break;
            case "Moto": this.tipo = Type.Moto;
            break;
            default: this.tipo = Type.Coche_Para_Discapacitados;
            break;
        }

        switch (etiqueta){
            case "C": this.etiqueta = Label.C;
            break;
            case "B": this.etiqueta = Label.B;
            break;
            case "ECO": this.etiqueta = Label.ECO;
            break;
            default: this.etiqueta = Label.Cero_Emisiones;
        }

        this.isElectrico = isElectrico;
    }

    public String getMatricula() {
        return matricula;
    }

    public Type getType() {
        return tipo;
    }

    public Label getEtiqueta() {
        return etiqueta;
    }

    public boolean isElectrico() {
        return isElectrico;
    }
}
