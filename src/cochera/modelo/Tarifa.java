package cochera.modelo;

import cochera.util.Dinero;

/**
 * Fila del tarifario (tabla tipo_vehiculo): tipo de vehículo, tarifa por hora y tarifa mensual.
 * El cobro por hora se hace por minuto: tarifa por hora / 60 (ver Alquiler.calcularMonto());
 * el alquiler por mes cobra la tarifa mensual con 10% de descuento (AlquilerMensual).
 */
public class Tarifa {

    private final int id;
    private final String tipoVehiculo;
    private double precioHora;
    private double tarifaMensual;

    public Tarifa(int id, String tipoVehiculo, double precioHora) {
        this(id, tipoVehiculo, precioHora, 0);
    }

    public Tarifa(int id, String tipoVehiculo, double precioHora, double tarifaMensual) {
        this.id = id;
        this.tipoVehiculo = tipoVehiculo;
        actualizarPrecios(precioHora, tarifaMensual);
    }

    /** Cambia la tarifa por hora. No se aceptan precios negativos. */
    public final void actualizarPrecio(double nuevoPrecioHora) {
        actualizarPrecios(nuevoPrecioHora, tarifaMensual);
    }

    /** Cambia la tarifa por hora y la mensual. No se aceptan precios negativos. */
    public final void actualizarPrecios(double nuevoPrecioHora, double nuevaTarifaMensual) {
        if (nuevoPrecioHora < 0 || nuevaTarifaMensual < 0) {
            throw new IllegalArgumentException("Los precios no pueden ser negativos.");
        }
        this.precioHora = Dinero.redondear(nuevoPrecioHora);
        this.tarifaMensual = Dinero.redondear(nuevaTarifaMensual);
    }

    /** Tarifa mensual sin descuento (0 = el tipo no ofrece alquiler mensual). */
    public double getTarifaMensual() {
        return tarifaMensual;
    }

    public boolean ofreceMensual() {
        return tarifaMensual > 0;
    }

    public int getId() {
        return id;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    /** Tarifa por hora (S/). */
    public double getPrecioHora() {
        return precioHora;
    }

    /** Tarifa por minuto = tarifa por hora / 60 (sin redondear, para no perder céntimos). */
    public double getPrecioMinuto() {
        return precioHora / 60.0;
    }

    @Override
    public String toString() {
        return tipoVehiculo + " (" + Dinero.soles(precioHora) + " por hora)";
    }
}
