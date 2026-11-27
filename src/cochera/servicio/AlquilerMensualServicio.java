package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.CocheraException;
import cochera.modelo.AlquilerMensual;
import cochera.util.Dinero;
import cochera.util.Dni;
import cochera.util.Reloj;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta de alquileres por mes (RP-03): lista por cliente y resumen con el 10% de descuento
 * aplicado a cada vehículo. El registro se hace en la entrada eligiendo "Por mes".
 */
public class AlquilerMensualServicio {

    /** Totales de una lista de alquileres mensuales. */
    public static class Resumen {
        private final int vehiculos;
        private final double subtotal;
        private final double descuento;
        private final double total;

        Resumen(int vehiculos, double subtotal, double descuento, double total) {
            this.vehiculos = vehiculos;
            this.subtotal = Dinero.redondear(subtotal);
            this.descuento = Dinero.redondear(descuento);
            this.total = Dinero.redondear(total);
        }

        public int getVehiculos() {
            return vehiculos;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public double getDescuento() {
            return descuento;
        }

        public double getTotal() {
            return total;
        }
    }

    private final BaseDatos bd;
    private final Reloj reloj;
    private final AutenticacionServicio auth;

    public AlquilerMensualServicio(BaseDatos bd, Reloj reloj, AutenticacionServicio auth) {
        this.bd = bd;
        this.reloj = reloj;
        this.auth = auth;
    }

    /** Todos los alquileres mensuales, o solo los del cliente con ese DNI (vacío = todos). */
    public List<AlquilerMensual> listar(String dni) throws CocheraException {
        auth.requerirSesion();
        String filtro = Dni.normalizar(dni);
        if (!filtro.isEmpty()) {
            Dni.validar(filtro, "del cliente");
        }
        List<AlquilerMensual> lista = new ArrayList<>();
        for (AlquilerMensual a : bd.listarAlquileresMensuales()) {
            if (filtro.isEmpty() || (a.getCliente() != null && a.getCliente().getDni().equals(filtro))) {
                lista.add(a);
            }
        }
        return lista;
    }

    /** Momento con el que se calcula el monto: la salida si ya se cobró, o ahora si sigue vigente. */
    public LocalDateTime momentoDeCalculo(AlquilerMensual a) {
        return a.getHoraSalida() != null ? a.getHoraSalida() : reloj.ahora();
    }

    /** Suma subtotal, descuento y total; cada vehículo tiene su propio 10% de descuento. */
    public Resumen resumir(List<AlquilerMensual> lista) {
        double subtotal = 0;
        double descuento = 0;
        double total = 0;
        for (AlquilerMensual a : lista) {
            LocalDateTime t = momentoDeCalculo(a);
            subtotal += a.calcularSubtotal(t);
            descuento += a.calcularDescuento(t);
            total += a.calcularMonto(t);
        }
        return new Resumen(lista.size(), subtotal, descuento, total);
    }
}
