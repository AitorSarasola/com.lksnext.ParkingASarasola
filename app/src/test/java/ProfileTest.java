import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

public class ProfileTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testLiveDataValue() throws InterruptedException {
        // 1. Crear instancia de tu ViewModel
        ProfileViewModel vm = new ProfileViewModel();


        Boolean result = LiveDataTestUtil.getValue(vm.getLogout());

        assertEquals(Boolean.FALSE,result);

        //No se puede hacer pq logout llama a un Handler
        /*vm.logout();

        result = LiveDataTestUtil.getValue(vm.getLogout());

        assertEquals(result,Boolean.TRUE);*/


    }
}
