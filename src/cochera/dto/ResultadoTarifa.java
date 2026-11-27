package cochera.dto;

/**
 * Detalle del monto de un alquiler al momento del cálculo: tiempo, precio, subtotal,
 * descuento y total. El total siempre sale de Alquiler.calcularMonto().
 */
public class ResultadoTarifa {

    private final long minutos;
    private final double precioHora;
    private final double subtotal;
    private final double descuento;
    private final double tarifaTotal;

    public ResultadoTarifa(long minutos, double precioHora, double subtotal, double descuento, double tarifaTotal) {
        this.minutos = minutos;
        this.precioHora = precioHora;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.tarifaTotal = tarifaTotal;
    }

    /** Minutos completos de permanencia (redondeado hacia abajo). */
    public long getMinutos() {
        return minutos;
    }

    /** Tarifa por hora del tipo de vehículo. */
    public double getPrecioHora() {
        return precioHora;
    }

    /** Monto antes de descuentos. */
    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    /** Monto a cobrar. */
    public double getTarifaTotal() {
        return tarifaTotal;
    }
}
