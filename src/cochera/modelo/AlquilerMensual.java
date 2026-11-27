package cochera.modelo;

import cochera.util.Dinero;
import java.time.LocalDateTime;

/**
 * Alquiler por MES con 10% de descuento por vehículo (RP-03).
 *
 *            Alquiler                calcularMonto() = minutos x tarifa por hora / 60
 *               ▲
 *        AlquilerMensual             calcularMonto() = tarifa mensual x meses x 0.90   (REDEFINIDO)
 *
 * HERENCIA: reutiliza todo lo del alquiler (placa, cliente, modelo, estado, salida).
 * POLIMORFISMO: quien cobra solo llama a alquiler.calcularMonto(); si el objeto es un
 * AlquilerMensual se ejecuta esta versión con el descuento, sin preguntar el tipo.
 *
 * Cada alquiler mensual es UN vehículo, por eso el descuento se aplica por vehículo:
 * un cliente con 2 autos de S/ 200.00 paga 2 x 180.00 = S/ 360.00.
 */
public class AlquilerMensual extends Alquiler {

    public static final double DESCUENTO = 0.10;
    public static final int DIAS_POR_MES = 30;

    private final LocalDateTime fechaFin;

    public AlquilerMensual(String placa, Tarifa tarifa, LocalDateTime fechaInicio, String codigoBarras,
                           Cliente cliente, Usuario empleado, Modelo modelo) {
        super(placa, tarifa, fechaInicio, codigoBarras, cliente, empleado, modelo);
        this.fechaFin = fechaInicio.plusDays(DIAS_POR_MES);
    }

    /** Meses cobrados: cada periodo de 30 días iniciado cuenta como un mes (mínimo 1). */
    public int mesesHasta(LocalDateTime hasta) {
        long minutosPorMes = DIAS_POR_MES * 24L * 60L;
        long minutos = minutosHasta(hasta);
        return (int) Math.max(1, (minutos + minutosPorMes - 1) / minutosPorMes);
    }

    /** Subtotal sin descuento: tarifa mensual del tipo x meses. */
    @Override
    public double calcularSubtotal(LocalDateTime hasta) {
        return Dinero.redondear(getTarifa().getTarifaMensual() * mesesHasta(hasta));
    }

    /** REDEFINE CalcularMonto(): subtotal con 10% de descuento. Ej.: S/ 200.00 -> S/ 180.00. */
    @Override
    public double calcularMonto(LocalDateTime hasta) {
        return Dinero.redondear(calcularSubtotal(hasta) * (1 - DESCUENTO));
    }

    @Override
    public TipoAlquiler getTipoAlquiler() {
        return TipoAlquiler.MENSUAL;
    }

    public LocalDateTime getFechaInicio() {
        return getHoraEntrada();
    }

    /** Fin del mes contratado: fecha de inicio + 30 días. */
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
}
