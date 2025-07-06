package com.lksnext.parkingplantilla.domain;

public class Fecha implements Comparable<Fecha> {
    private int ano;
    private int mes;
    private int dia;

    // Constructor con formato YYYY-MM-DD
    public Fecha(String fechaStr) {
        fechaStr = fechaStr.toLowerCase();
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
            String anoStr = partes[2];
            if (anoStr.length() == 2) {
                this.ano = 2000 + Integer.parseInt(anoStr);
            } else if (anoStr.length() == 4) {
                this.ano = Integer.parseInt(anoStr);
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
        if (ano < 1) return false;
        if (mes < 1 || mes > 12) return false;
        return !(dia < 1 || dia > diasEnMes(mes, ano));
    }

    private int diasEnMes(int mes, int ano) {
        switch (mes) {
            case 2: return esBisiesto(ano) ? 29 : 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }

    private boolean esBisiesto(int ano) {
        return (ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fecha otra = (Fecha) obj;
        return this.ano == otra.ano &&
                this.mes == otra.mes &&
                this.dia == otra.dia;
    }

    // Comparar fechas
    @Override
    public int compareTo(Fecha otra) {
        if (this.ano != otra.ano) {
            return Integer.compare(this.ano, otra.ano);
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
        int a = this.ano;

        while (true) {
            int diasMes = diasEnMes(m, a);
            if (totalDias <= diasMes) {
                this.dia = totalDias;
                this.mes = m;
                this.ano = a;
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
        int a = this.ano;

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
        this.ano = a;
    }

    public int diferenciaEnDias(Fecha otra) {
        return contarDiasDesdeOrigen() - otra.contarDiasDesdeOrigen();
    }

    // contar días absolutos desde 01/01/0001
    private int contarDiasDesdeOrigen() {
        int dias = 0;
        int anoLag = this.ano - 2000;
        // contar los días de años completos
        for (int y = 1; y < anoLag; y++) {
            dias += esBisiesto(y) ? 366 : 365;
        }
        // contar los días de meses completos de este año
        for (int m = 1; m < this.mes; m++) {
            dias += diasEnMes(m, anoLag);
        }
        // sumar los días transcurridos en el mes actual
        dias += this.dia;
        return dias;
    }

    @Override
    public String toString() {
        String mesName;
        switch (mes){
            case 1: mesName = "Enero"; break;
            case 2: mesName = "Febrero"; break;
            case 3: mesName = "Marzo"; break;
            case 4: mesName = "Abril"; break;
            case 5: mesName = "Mayo"; break;
            case 6: mesName = "Junio"; break;
            case 7: mesName = "Julio"; break;
            case 8: mesName = "Agosto"; break;
            case 9: mesName = "Septiembre"; break;
            case 10: mesName = "Octubre"; break;
            case 11: mesName = "Noviembre"; break;
            default: mesName = "Diciembre";
        }
        return String.format("%02d de %s de %04d", dia, mesName, ano);
    }

    public String toStringForFirestore() {
        return String.format("%04d-%02d-%02d", ano, mes, dia);
    }

    public String toStringForApi() {
        return String.format("%02d-%02d-%04d", dia, mes, ano);
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

