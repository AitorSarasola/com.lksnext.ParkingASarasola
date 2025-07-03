package com.lksnext.parkingplantilla.domain;

public class Fecha implements Comparable<Fecha> {
    public int año;
    public int mes;
    public int dia;

    // Constructor con formato YYYY-MM-DD
    public Fecha(String fechaStr) {
        fechaStr = fechaStr.trim();

        // Detectar separador: / o - o espacio
        String separador = null;
        if (fechaStr.contains("/")) separador = "/";
        else if (fechaStr.contains("-")) separador = "-";
        else if (fechaStr.contains(" ")) separador = " ";
        else throw new IllegalArgumentException("Separador de fecha inválido");

        String[] partes = fechaStr.split(java.util.regex.Pattern.quote(separador));
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato de fecha inválido, se esperan 3 partes");
        }

        try {
            // Asumimos orden DD MM YYYY o DD MM YY
            this.dia = Integer.parseInt(partes[0]);
            this.mes = Integer.parseInt(partes[1]);

            // Año puede ser 2 o 4 dígitos
            String añoStr = partes[2];
            if (añoStr.length() == 2) {
                this.año = 2000 + Integer.parseInt(añoStr);
            } else if (añoStr.length() == 4) {
                this.año = Integer.parseInt(añoStr);
            } else {
                throw new IllegalArgumentException("Formato de año inválido");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No se pudieron convertir día, mes o año");
        }

        if (!esFechaValida()) {
            throw new IllegalArgumentException("Fecha inválida");
        }
    }

    // Validar fecha básica (no usa librerías externas)
    private boolean esFechaValida() {
        if (año < 1) return false;
        if (mes < 1 || mes > 12) return false;
        if (dia < 1 || dia > diasEnMes(mes, año)) return false;
        return true;
    }

    private int diasEnMes(int mes, int año) {
        switch (mes) {
            case 2: return esBisiesto(año) ? 29 : 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }

    private boolean esBisiesto(int año) {
        return (año % 4 == 0 && año % 100 != 0) || (año % 400 == 0);
    }

    // Comparar fechas
    @Override
    public int compareTo(Fecha otra) {
        if (this.año != otra.año) {
            return Integer.compare(this.año, otra.año);
        }
        if (this.mes != otra.mes) {
            return Integer.compare(this.mes, otra.mes);
        }
        return Integer.compare(this.dia, otra.dia);
    }

    // Sumar días
    public void sumarDias(int diasASumar) {
        int totalDias = this.dia + diasASumar;
        int m = this.mes;
        int a = this.año;

        while (true) {
            int diasMes = diasEnMes(m, a);
            if (totalDias <= diasMes) {
                this.dia = totalDias;
                this.mes = m;
                this.año = a;
                break;
            } else {
                totalDias -= diasMes;
                m++;
                if (m > 12) {
                    m = 1;
                    a++;
                }
            }
        }
    }

    // Restar días
    public void restarDias(int diasARestar) {
        int totalDias = this.dia - diasARestar;
        int m = this.mes;
        int a = this.año;

        while (totalDias < 1) {
            m--;
            if (m < 1) {
                m = 12;
                a--;
                if (a < 1) {
                    throw new IllegalArgumentException("Fecha resultante inválida (antes del año 1)");
                }
            }
            totalDias += diasEnMes(m, a);
        }

        this.dia = totalDias;
        this.mes = m;
        this.año = a;
    }

    public int diferenciaEnDias(Fecha otra){
        int dias_, mes_, año_;
        dias_ = this.dia - otra.dia;
        mes_ = this.mes - otra.mes;
        año_ = this.año - otra.año;
        return año_ * 365 + mes_ * 30 + dias_; // Aproximación simple
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", dia, mes, año);
    }

    public String ToStringForFirestore() {
        return String.format("%04d-%02d-%02d", año, mes, dia);
    }

    public static String invertirFormatoFecha(String input) {
        String[] partes = input.split("-");
        if (partes.length != 3) {
            throw new IllegalArgumentException("El formato debe ser 'YYYY-MM-DD'");
        }
        return partes[2] + "-" + partes[1] + "-" + partes[0];
    }

    public static Fecha fechaActual() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return new Fecha(String.format("%02d-%02d-%04d", cal.get(java.util.Calendar.DAY_OF_MONTH),
                cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.YEAR)));
    }
}

