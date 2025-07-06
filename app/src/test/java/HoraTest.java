import org.junit.Test;
import static org.junit.Assert.*;

import com.lksnext.parkingplantilla.domain.Hora;

public class HoraTest {

    @Test
    public void testConstructorEntero() {
        Hora h = new Hora(10, 45);
        assertEquals(10, h.getHoras());
        assertEquals(45, h.getMinutos());
    }

    @Test
    public void testConstructorConStringValido() {
        Hora h = new Hora("08:30");
        assertEquals(8, h.getHoras());
        assertEquals(30, h.getMinutos());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorStringInvalido() {
        new Hora("hora-mala");
    }

    @Test
    public void testToString() {
        Hora h = new Hora(5, 7);
        assertEquals("05:07", h.toString());
    }

    @Test
    public void testSumarMinutos() {
        Hora h = new Hora(23, 50);
        h.sumarMinutos(20);
        assertEquals("00:10", h.toString());
    }

    @Test
    public void testRestarMinutos() {
        Hora h = new Hora(0, 10);
        h.restarMinutos(20);
        assertEquals("23:50", h.toString());
    }

    @Test
    public void testDiferenciaEnMinutos() {
        Hora h1 = new Hora(10, 0);
        Hora h2 = new Hora(11, 30);
        assertEquals(90, h1.diferenciaEnMinutos(h2));
    }

    @Test
    public void testEqualsYCompareTo() {
        Hora h1 = new Hora(10, 30);
        Hora h2 = new Hora(10, 30);
        Hora h3 = new Hora(11, 0);

        assertEquals(h1, h2);
        assertNotEquals(h1, h3);
        assertEquals(0, h1.compareTo(h2));
        assertTrue(h1.compareTo(h3) < 0);
        assertTrue(h3.compareTo(h1) > 0);
    }

    @Test
    public void testHoraActual() {
        Hora actual = Hora.horaActual();
        assertNotNull(actual);
        assertTrue(actual.getHoras() >= 0 && actual.getHoras() < 24);
        assertTrue(actual.getMinutos() >= 0 && actual.getMinutos() < 60);
    }

    @Test
    public void testFormasDeSeparar() {
        Hora h1 = new Hora("14 30");
        Hora h2 = new Hora("14-30");
        Hora h3 = new Hora("14/30");
        Hora h4 = new Hora("14:30");

        assertEquals(h1, h2);
        assertEquals(h2, h3);
        assertEquals(h3, h4);
    }
}

