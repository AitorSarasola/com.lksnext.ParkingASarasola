package com.lksnext.parkingplantilla.domain;

import android.widget.Switch;

public class Car {
    public enum Type {
        Coche,
        Moto
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
    private boolean isParaDiscapacitados;
    private boolean isElectrico;

    public Car(String matricula, Type tipo, Label etiqueta, boolean isParaDiscapacitados, boolean isElectrico){
        this.matricula = matricula;
        this.tipo=tipo;
        this.etiqueta = etiqueta;
        this.isParaDiscapacitados = isParaDiscapacitados;
        this.isElectrico = isElectrico;
    }

    public Car(String matricula, String tipo, String etiqueta, boolean isParaDiscapacitados, boolean isElectrico){
        this.matricula = matricula;
        switch(tipo){
            case "Moto": this.tipo = Type.Moto;
            break;
            default: this.tipo = Type.Coche;
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
        this.isParaDiscapacitados = isParaDiscapacitados;
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

    public boolean isParaDiscapacitados() {
        return isParaDiscapacitados;
    }
}
