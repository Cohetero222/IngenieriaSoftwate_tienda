package vista;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import javax.swing.*;
class FormularioVentaTest {

    private FormularioVenta Formulario;

    @BeforeEach
    void setUp() {
        Formulario = new FormularioVenta();
        //Formulario.camposTotales = new ArrayList<>();
        //Formulario.txtTotalGeneral = new JTextField();
        JTextField T1 =   new JTextField();    
    }   

    @Test
    @DisplayName("Test de creación de FormularioVenta")
    void testCrearFormularioVenta() {
        // Dado un JFrame padre y una lista de IDs de productos
        JFrame parent = new JFrame();
        ArrayList<Integer> idsProductos = new ArrayList<>();
        idsProductos.add(1);
        idsProductos.add(2);

        // Cuando se crea el FormularioVenta
        FormularioVenta formularioVenta = new FormularioVenta(parent, idsProductos);

        // Entonces el formulario no debe ser nulo
        assertNotNull(formularioVenta);
    }
}