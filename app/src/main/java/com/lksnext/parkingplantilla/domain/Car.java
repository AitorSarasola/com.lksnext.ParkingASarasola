package com.lksnext.parkingplantilla.domain;

import com.google.firebase.firestore.MemoryLruGcSettings;

public class Car {
    public enum Type {
        COCHE,
        MOTO
    }

    public enum Label {
        CERO_EMISIONES,
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
        if(tipo=="Moto")
            this.tipo = Type.MOTO;
        else
            this.tipo = Type.COCHE;

        switch (etiqueta){
            case "C": this.etiqueta = Label.C;
            break;
            case "B": this.etiqueta = Label.B;
            break;
            case "ECO": this.etiqueta = Label.ECO;
            break;
            default: this.etiqueta = Label.CERO_EMISIONES;
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
