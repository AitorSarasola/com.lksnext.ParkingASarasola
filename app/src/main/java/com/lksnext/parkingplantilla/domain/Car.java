package com.lksnext.parkingplantilla.domain;

import kotlin.time.ExperimentalTime;

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
