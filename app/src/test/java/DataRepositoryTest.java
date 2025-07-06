import org.junit.Test;
import static org.junit.Assert.*;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Hora;

public class DataRepositoryTest {
    @Test
    public void testIsValidEmail() {
        assertTrue(DataRepository.isValidEmail("user@example.com"));
        assertTrue(DataRepository.isValidEmail("user.name+tag+sorting@example.co.uk"));
        assertFalse(DataRepository.isValidEmail("user@.com"));
        assertFalse(DataRepository.isValidEmail("user@example"));
        assertFalse(DataRepository.isValidEmail("user@exa mple.com"));
        assertFalse(DataRepository.isValidEmail(""));
        assertFalse(DataRepository.isValidEmail(null));
    }

    // -- isValidUser --
    @Test
    public void testIsValidUser() {
        DataRepository instance = new DataRepository();
        // Suponiendo USERNAME_MAX_LENGTH = 10, ajusta si es otro valor
        assertTrue(instance.isValidUser("user123"));
        assertTrue(instance.isValidUser("User Name"));
        assertFalse(instance.isValidUser("u")); // menos de 2 caracteres
        assertFalse(instance.isValidUser("user!")); // carácter inválido
        assertFalse(instance.isValidUser(" user ")); // termina en espacio no permitido
    }

    // -- isValidPassword --
    @Test
    public void testIsValidPassword() {
        DataRepository instance = new DataRepository();
        assertTrue(instance.isValidPassword("pass123"));
        assertTrue(instance.isValidPassword("!@#$%^&*()_+"));
        assertFalse(instance.isValidPassword("ab")); // menos de 3 caracteres
        assertFalse(instance.isValidPassword("a".repeat(21))); // más de 20 caracteres
        assertFalse(instance.isValidPassword("pass with space")); // espacio no permitido?
    }

    // -- isValidLicensePlate --
    @Test
    public void testIsValidLicensePlate() {
        assertTrue(DataRepository.isValidLicensePlate("1234-BCD"));
        assertTrue(DataRepository.isValidLicensePlate("1234 BCD"));
        assertTrue(DataRepository.isValidLicensePlate("1234BCD"));
        assertFalse(DataRepository.isValidLicensePlate("123-ABCD"));
        assertFalse(DataRepository.isValidLicensePlate("12345-BCD"));
        assertFalse(DataRepository.isValidLicensePlate("1234-ABC1"));
        assertFalse(DataRepository.isValidLicensePlate("1234-ABT"));
        assertFalse(DataRepository.isValidLicensePlate("1234-ÑPP"));
    }

    // -- standarizeLicensePlate --
    @Test
    public void testStandarizeLicensePlate() {
        assertEquals("1234-BCD", DataRepository.standarizeLicensePlate("1234 BCD"));
        assertEquals("1234-BCD", DataRepository.standarizeLicensePlate("1234-BCD"));
        assertEquals("1234-BCD", DataRepository.standarizeLicensePlate("1234BCD"));
    }

    // -- deleteLastSpace --
    @Test
    public void testDeleteLastSpace() {
        assertEquals("hello", DataRepository.deleteLastSpace("hello "));
        assertEquals("hello world", DataRepository.deleteLastSpace("hello world "));
        assertEquals("hello", DataRepository.deleteLastSpace("hello"));
        assertEquals("", DataRepository.deleteLastSpace(" "));
        assertNull(DataRepository.deleteLastSpace(null));
    }

    // -- validHours --
    @Test
    public void testValidHours() {
        Hora start = new Hora(10, 0); // ejemplo de constructor: 10:00
        Hora endValid = new Hora(10, 10);
        Hora endTooShort = new Hora(10, 3);
        Hora endTooLong = new Hora(20, 0);
        Hora endBefore = new Hora(9, 0);

        assertNull(DataRepository.validHours(start, endValid));
        assertEquals("La reserva debe durar al menos 5 minutos.", DataRepository.validHours(start, endTooShort));
        assertEquals("La reserva no puede superar las 8 horas.", DataRepository.validHours(start, endTooLong));
        assertEquals("La hora de inicio debe ser anterior a la hora de fin.", DataRepository.validHours(start, endBefore));
    }
}
