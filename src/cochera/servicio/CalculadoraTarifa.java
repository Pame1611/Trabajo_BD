package cochera.servicio;

import cochera.dto.ResultadoTarifa;
import cochera.modelo.Alquiler;
import cochera.util.Dinero;
import java.time.LocalDateTime;

/**
 * Arma el detalle del cobro de un alquiler para mostrarlo en pantalla y en el ticket.
 *
 * La regla de cobro vive en el modelo: {@link Alquiler#calcularMonto(LocalDateTime)}
 * = minutos de permanencia x tarifa por minuto (tarifa por hora / 60).
 * Aquí solo se llama a ese método y se agregan los datos para mostrar.
 */
public class CalculadoraTarifa {

    /** Monto por minutos: minutos x tarifa por hora / 60, redondeado a céntimos. */
    public static double montoPorMinutos(long minutos, double precioHora) {
        return Dinero.redondear(Math.max(0, minutos) * precioHora / 60.0);
    }

    /** Calcula con la tarifa vigente del tipo de vehículo del alquiler. */
    public ResultadoTarifa calcular(Alquiler alquiler, LocalDateTime ahora) {
        double total = alquiler.calcularMonto(ahora);
        return new ResultadoTarifa(alquiler.minutosHasta(ahora), alquiler.getTarifa().getPrecioHora(),
                total, 0, total);
    }
}
