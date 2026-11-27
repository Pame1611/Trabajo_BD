package cochera.modelo;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Venta (tabla venta): une al CLIENTE que compra con el alquiler cobrado y su pago.
 *
 *   cliente 1 ──── N venta      (venta.cliente_dni es llave foránea de cliente.dni)
 *
 * No existe una venta sin cliente: el constructor lo exige. Un alquiler tiene un solo
 * vehículo, por eso no hace falta una tabla de detalle de venta.
 */
public class Venta {

    private final String id;
    private final Cliente cliente;
    private final Alquiler alquiler;
    private final Pago pago;

    public Venta(Cliente cliente, Alquiler alquiler, Pago pago) {
        if (cliente == null) {
            throw new IllegalArgumentException("No se puede registrar una venta sin cliente.");
        }
        if (alquiler == null || pago == null) {
            throw new IllegalArgumentException("La venta necesita el alquiler y su pago.");
        }
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.alquiler = alquiler;
        this.pago = pago;
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Alquiler getAlquiler() {
        return alquiler;
    }

    public Pago getPago() {
        return pago;
    }

    public LocalDateTime getFecha() {
        return pago.getCreadoEn();
    }

    public double getMonto() {
        return pago.getMontoTotal();
    }
}
