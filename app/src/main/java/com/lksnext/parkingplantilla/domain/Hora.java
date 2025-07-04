package com.lksnext.parkingplantilla.domain;

public class Hora implements Comparable<Hora>{

    int horas;
    int minutos;

    public Hora() {

    }

    public Hora(int horas, int minutos) {
        this.horas = horas;
        this.minutos = minutos;
    }

    public Hora(String horaStr) {
        horaStr = horaStr.trim();

        // separa usando regex con delimitadores :, espacio o -
        String[] partes = horaStr.split("[:\\s-/]");

        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de hora inválido");
        }

        try {
            horas = Integer.parseInt(partes[0]);
            minutos = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No se pudieron convertir horas o minutos");
        }
    }

    public long getHoras() {
        return horas;
    }

    public void setHorasO(int horas) {
        this.horas = horas;
    }

    public long getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int compareTo(Hora otra) {
        if (this.horas != otra.horas) {
            return Integer.compare(this.horas, otra.horas);
        }
        return Integer.compare(this.minutos, otra.minutos);
    }

    public void sumarMinutos(int minutosASumar) {
        int totalMinutos = this.horas * 60 + this.minutos + minutosASumar;
        this.horas = (totalMinutos / 60) % 24; // para que no pase de 24h
        this.minutos = totalMinutos % 60;
    }

    public void restarMinutos(int minutosARestar) {
        int totalMinutos = this.horas * 60 + this.minutos - minutosARestar;
        if (totalMinutos < 0) {
            totalMinutos = (24 * 60 + totalMinutos) % (24 * 60); // para valores negativos
        }
        this.horas = (totalMinutos / 60) % 24;
        this.minutos = totalMinutos % 60;
    }

    public int diferenciaEnMinutos(Hora otra) {
        int minutosEsta = this.horas * 60 + this.minutos;
        int minutosOtra = otra.horas * 60 + otra.minutos;
        return minutosOtra - minutosEsta;
    }

    public static Hora horaActual() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        return new Hora(calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE));
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", horas, minutos);
    }
}
