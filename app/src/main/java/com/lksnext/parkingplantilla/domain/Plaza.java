package com.lksnext.parkingplantilla.domain;

public class Plaza {

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
    private String id;
    private Car.Type tipo;
    private Car.Label etiqueta;
    private boolean isParaDiscapacitados;
    private boolean isElectrico;

    public Plaza(String id, Car.Type tipo, Car.Label etiqueta, boolean isParaDiscapacitados, boolean isElectrico){
        this.id = id;
        this.tipo=tipo;
        this.etiqueta = etiqueta;
        this.isParaDiscapacitados = isParaDiscapacitados;
        this.isElectrico = isElectrico;
    }

    public Plaza(String id, String tipo, String etiqueta, boolean isParaDiscapacitados, boolean isElectrico){
        this.id = id;
        if(tipo.toUpperCase().equals("MOTO"))
            this.tipo = Car.Type.MOTO;
        else
            this.tipo = Car.Type.COCHE;

        switch (etiqueta){
            case "C": this.etiqueta = Car.Label.C;
                break;
            case "B": this.etiqueta = Car.Label.B;
                break;
            case "ECO": this.etiqueta = Car.Label.ECO;
                break;
            default: this.etiqueta = Car.Label.CERO_EMISIONES;
        }
        this.isParaDiscapacitados = isParaDiscapacitados;
        this.isElectrico = isElectrico;
    }

    public String getId() {
        return id;
    }

    public Car.Type getType() {
        return tipo;
    }

    public Car.Label getEtiqueta() {
        return etiqueta;
    }

    public boolean isElectrico() {
        return isElectrico;
    }

    public boolean isParaDiscapacitados() {
        return isParaDiscapacitados;
    }
}
