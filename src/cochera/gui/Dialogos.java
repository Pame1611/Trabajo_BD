package cochera.gui;

import cochera.dto.ResultadoTarifa;
import cochera.modelo.Alquiler;
import cochera.modelo.MetodoPago;
import cochera.util.Dinero;
import cochera.util.FechaUtil;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Mensajes y preguntas estándar (JOptionPane) con textos en español.
 */
public final class Dialogos {

    private static final String TITULO = "Cochera Los Ángeles";

    private Dialogos() {
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String pregunta) {
        Object[] opciones = {"Sí", "No"};
        int r = JOptionPane.showOptionDialog(padre, pregunta, TITULO, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        return r == 0;
    }

    public static String pedirTexto(Component padre, String pregunta) {
        String r = JOptionPane.showInputDialog(padre, pregunta, TITULO, JOptionPane.QUESTION_MESSAGE);
        return r == null ? null : r.trim();
    }

    /** Muestra una lista para elegir una opción; devuelve null si se cancela. */
    public static <T> T elegir(Component padre, String pregunta, java.util.List<T> opciones) {
        Object[] arreglo = opciones.toArray();
        Object r = JOptionPane.showInputDialog(padre, pregunta, TITULO, JOptionPane.QUESTION_MESSAGE, null,
                arreglo, arreglo.length > 0 ? arreglo[0] : null);
        @SuppressWarnings("unchecked")
        T elegido = (T) r;
        return elegido;
    }

    /** Pide un método de pago; devuelve null si se cancela. */
    public static MetodoPago elegirMetodoPago(Component padre, MetodoPago actual) {
        Object r = JOptionPane.showInputDialog(padre, "Método actual: " + actual + "\nNuevo método de pago:",
                TITULO, JOptionPane.QUESTION_MESSAGE, null, MetodoPago.values(), actual);
        return (MetodoPago) r;
    }

    /** Texto con el detalle de estadía y lo que debe pagar un vehículo. */
    public static String textoCotizacion(Alquiler t, ResultadoTarifa r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket: ").append(t.getCodigoBarras()).append('\n');
        sb.append("Placa: ").append(t.getPlacaFormateada()).append("  (").append(t.getTarifa().getTipoVehiculo()).append(")\n");
        sb.append("Ingreso: ").append(FechaUtil.fechaHora(t.getHoraEntrada())).append('\n');
        sb.append("Tiempo: ").append(FechaUtil.formatoMinutos(r.getMinutos())).append('\n');
        sb.append("Cliente: ").append(t.getCliente() == null ? "-" : t.getCliente().toString()).append('\n');
        if (r.esMensual()) {
            sb.append("Alquiler mensual: ").append(r.getMeses()).append(" mes(es) x ")
                    .append(Dinero.soles(t.getTarifa().getTarifaMensual())).append('\n');
        } else {
            sb.append("Tarifa: ").append(Dinero.soles(r.getPrecioHora())).append(" por hora (")
                    .append(r.getMinutos()).append(" min x ").append(Dinero.soles(r.getPrecioHora())).append(" / 60)\n");
        }
        if (r.getDescuento() > 0) {
            sb.append("Subtotal: ").append(Dinero.soles(r.getSubtotal())).append('\n');
            sb.append("Descuento: -").append(Dinero.soles(r.getDescuento())).append('\n');
        }
        sb.append("\nTOTAL A COBRAR: ").append(Dinero.soles(r.getTarifaTotal()));
        return sb.toString();
    }
}
