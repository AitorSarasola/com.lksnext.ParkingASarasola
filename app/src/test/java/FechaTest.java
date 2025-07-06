import org.junit.Test;
import static org.junit.Assert.*;

import com.lksnext.parkingplantilla.domain.Fecha;

public class FechaTest {

    @Test
    public void testConstructorConFormatoValido() {
        Fecha fecha = new Fecha("01-01-2020");
        assertEquals("01 de Enero de 2020", fecha.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorConFormatoInvalido() {
        new Fecha("fecha-mala");
    }

    @Test
    public void testSumarDias() {
        Fecha fecha = new Fecha("28-02-2020"); // Año bisiesto
        fecha.sumarDias(1);
        assertEquals("29 de Febrero de 2020", fecha.toString());

        fecha.sumarDias(1);
        assertEquals("01 de Marzo de 2020", fecha.toString());
    }

    @Test
    public void testRestarDias() {
        Fecha fecha = new Fecha("01-03-2020");
        fecha.restarDias(1);
        assertEquals("29 de Febrero de 2020", fecha.toString());

        fecha.restarDias(1);
        assertEquals("28 de Febrero de 2020", fecha.toString());

        fecha = new Fecha("1-1-2025");
        fecha.restarDias(2);
        assertEquals("30 de Diciembre de 2024", fecha.toString());
    }

    @Test
    public void testEquals() {
        Fecha f1 = new Fecha("01-01-2020");
        Fecha f2 = new Fecha("01-01-2020");
        Fecha f3 = new Fecha("02-01-2020");

        assertEquals(f1, f2);
        assertNotEquals(f1, f3);
    }

    @Test
    public void testCompareTo() {
        Fecha f1 = new Fecha("01-01-2020");
        Fecha f2 = new Fecha("02-01-2020");
        Fecha f3 = new Fecha("01-01-2020");

        assertTrue(f1.compareTo(f2) < 0);
        assertTrue(f2.compareTo(f1) > 0);
        assertEquals(f1,f3);
    }

    @Test
    public void testToStringForFirestore() {
        Fecha f = new Fecha("01-02-2020");
        assertEquals("2020-02-01", f.toStringForFirestore());
    }

    @Test
    public void testToStringForApi() {
        Fecha f = new Fecha("01-02-2020");
        assertEquals("01-02-2020", f.toStringForApi());
    }

    @Test
    public void testInvertirFormatoFecha() {
        assertEquals("15-04-2024", Fecha.invertirFormatoFecha("2024-04-15"));
    }

    @Test
    public void testFechaActual() {
        Fecha hoy = Fecha.fechaActual();
        assertNotNull(hoy);
        // Este test no compara con una fecha exacta porque depende del sistema
    }

    @Test
    public void testDiferenciaEnDias() {
        Fecha f1 = new Fecha("01-01-2020");
        Fecha f2 = new Fecha("03-01-2020");
        assertEquals(-2, f1.diferenciaEnDias(f2));

        f1 = new Fecha("28-02-2024");
        f2 = new Fecha("01-03-2024");
        assertEquals(-2, f1.diferenciaEnDias(f2));
    }
}

