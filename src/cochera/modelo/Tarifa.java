package cochera.modelo;

import cochera.util.Dinero;

/**
 * Fila del tarifario (tabla tipo_vehiculo): tipo de vehículo y su tarifa por hora.
 * El cobro se hace por minuto: tarifa por hora / 60 (ver Alquiler.calcularMonto()).
 */
public class Tarifa {

    private final int id;
    private final String tipoVehiculo;
    private double precioHora;

    public Tarifa(int id, String tipoVehiculo, double precioHora) {
        this.id = id;
        this.tipoVehiculo = tipoVehiculo;
        actualizarPrecio(precioHora);
    }

    /** Cambia la tarifa por hora. No se aceptan precios negativos. */
    public final void actualizarPrecio(double nuevoPrecioHora) {
        if (nuevoPrecioHora < 0) {
            throw new IllegalArgumentException("Los precios no pueden ser negativos.");
        }
        this.precioHora = Dinero.redondear(nuevoPrecioHora);
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
