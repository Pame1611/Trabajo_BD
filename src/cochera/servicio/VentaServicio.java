package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.CocheraException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Alquiler;
import cochera.modelo.Pago;
import cochera.modelo.Venta;
import cochera.util.Dinero;
import cochera.util.Dni;
import java.util.List;

/**
 * Ventas por cliente (relación cliente 1 ── N venta).
 * Cada cobro registrado en la salida genera una venta vinculada al cliente del alquiler.
 */
public class VentaServicio {

    private final BaseDatos bd;
    private final AutenticacionServicio auth;

    public VentaServicio(BaseDatos bd, AutenticacionServicio auth) {
        this.bd = bd;
        this.auth = auth;
    }

    /** Registra la venta del alquiler cobrado. No hay venta sin cliente. */
    public Venta registrar(Alquiler alquiler, Pago pago) throws CocheraException {
        if (alquiler.getCliente() == null) {
            throw new ValidacionException("No se puede registrar una venta sin cliente.");
        }
        Venta venta = new Venta(alquiler.getCliente(), alquiler, pago);
        bd.insertarVenta(venta);
        return venta;
    }

    /** Historial de ventas de un cliente (JOIN cliente-venta), de la más reciente a la más antigua. */
    public List<Venta> ventasDeCliente(String dni) throws CocheraException {
        auth.requerirSesion();
        return bd.ventasDeCliente(Dni.validar(dni, "del cliente"));
    }

    /** Suma de las ventas de la lista. */
    public static double total(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) {
            total += v.getMonto();
        }
        return Dinero.redondear(total);
    }
}
