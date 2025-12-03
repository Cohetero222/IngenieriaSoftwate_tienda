package vista;
/* 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




public class FormularioVentaTest {
    @Mock 
    private ProductoDAO productoDAO;

    @
    @Mock 
    private VentaDAO ventaDAO;

    private Producto producto;
    // ========== SUBCLASE SOLO PARA TEST ==========
    // No modifica tu FormularioVenta original
    class FormularioVentaTestable extends FormularioVenta {

    
        public void setCamposTotales(List<JTextField> lista) {
            this.camposTotales = lista; // acceso porque está en misma clase/paquete
        }

        public void setTxtTotalGeneral(JTextField txt) {
            this.txtTotalGeneral = txt;
        }
    }

    private FormularioVentaTestable formulario;

    private JTextField txt1;
    private JTextField txt2;
    private JTextField txt3;
    private JTextField txtTotalGeneral;

    @BeforeEach
    public void setUp() {
        formulario = new FormularioVentaTestable();

        // ===== Crear mocks reales de JTextField =====
        txt1 = mock(JTextField.class);
        txt2 = mock(JTextField.class);
        txt3 = mock(JTextField.class);
        txtTotalGeneral = mock(JTextField.class);

        // ===== configurar valores simulados =====
        when(txt1.getText()).thenReturn("10.5");
        when(txt2.getText()).thenReturn("20.5");
        when(txt3.getText()).thenReturn("30");

        // lista que usa el formulario
        List<JTextField> lista = new ArrayList<>();
        lista.add(txt1);
        lista.add(txt2);
        lista.add(txt3);

        formulario.setCamposTotales(lista);
        formulario.setTxtTotalGeneral(txtTotalGeneral);
    }

    @Test
    public void testActualizarTotalGeneral() {

        // "When": se ejecuta el método REAL sin tocarlo
      //  double total = formulario.actualizarTotalGeneral();

        // Verificación numérica
        assertEquals(61.0, total, 0.001);

        // Verificación del JTextField general
        verify(txtTotalGeneral).setText("61.00");
    }
}
*/