package cochera.modelo;

import cochera.util.Dinero;
import cochera.util.FechaUtil;
import cochera.util.Placa;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Alquiler de un espacio de la cochera (tabla alquiler). Antes se llamaba TicketCochera.
 *
 * Nace EN_CURSO en la entrada y pasa a PAGADO en la salida (o ANULADO por el admin).
 * Guarda el DNI del cliente, el del empleado que registró la entrada y el modelo del
 * vehículo (de él salen la marca y el tipo); la fecha y hora
 * de salida quedan en null hasta que el vehículo sale.
 *
 * El monto lo calcula {@link #calcularMonto(LocalDateTime)}:
 *   minutos de permanencia x tarifa por minuto del tipo (tarifa por hora / 60).
 */
public class Alquiler {

    private final String id;
    private final String placa;
    private final Tarifa tarifa;
    private final LocalDateTime horaEntrada;
    private final String codigoBarras;
    private final Cliente cliente;
    private final Usuario empleado;
    private final Modelo modelo;
    private LocalDateTime horaSalida;
    private EstadoTicket estado;

    public Alquiler(String placa, Tarifa tarifa, LocalDateTime horaEntrada, String codigoBarras,
                    Cliente cliente, Usuario empleado, Modelo modelo) {
        this.id = UUID.randomUUID().toString();
        this.placa = placa;
        this.tarifa = tarifa;
        this.horaEntrada = horaEntrada;
        this.codigoBarras = codigoBarras;
        this.cliente = cliente;
        this.empleado = empleado;
        this.modelo = modelo;
        this.estado = EstadoTicket.EN_CURSO;
    }

    /** Minutos completos de permanencia desde la entrada hasta {@code hasta} (nunca negativo). */
    public long minutosHasta(LocalDateTime hasta) {
        return Math.max(0, FechaUtil.minutosEntre(horaEntrada, hasta));
    }

    /**
     * CalcularMonto(): lo que paga el alquiler si sale en {@code hasta}.
     * Minutos de permanencia por la tarifa por minuto del tipo de vehículo.
     * Ej.: Camioneta a S/ 5.00 por hora con 90 minutos = 90 x 5 / 60 = S/ 7.50.
     */
    public double calcularMonto(LocalDateTime hasta) {
        return Dinero.redondear(minutosHasta(hasta) * tarifa.getPrecioHora() / 60.0);
    }

    /** Salida: registra la hora y marca el alquiler como PAGADO. */
    public void registrarSalida(LocalDateTime salida) {
        if (!estaEnCurso()) {
            throw new IllegalStateException("El alquiler " + codigoBarras + " no esta EN_CURSO.");
        }
        this.horaSalida = salida;
        this.estado = EstadoTicket.PAGADO;
    }

    /** Anulación administrativa: el pago queda como registro histórico. */
    public void anular() {
        this.estado = EstadoTicket.ANULADO;
    }

    public boolean estaEnCurso() {
        return estado == EstadoTicket.EN_CURSO;
    }

    public String getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getPlacaFormateada() {
        return Placa.formatear(placa);
    }

    public Tarifa getTarifa() {
        return tarifa;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    /** Cliente dueño del alquiler (llave foránea cliente_dni). */
    public Cliente getCliente() {
        return cliente;
    }

    /** Empleado que registró la entrada (llave foránea empleado_dni). */
    public Usuario getEmpleado() {
        return empleado;
    }

    /** Modelo del vehículo (llave foránea modelo_id); el alquiler guarda un solo ModeloID y la placa. */
    public Modelo getModelo() {
        return modelo;
    }

    /** "Toyota Yaris" o "Desconocido". */
    public String getDescripcionVehiculo() {
        return modelo == null ? Marca.DESCONOCIDO : modelo.getDescripcion();
    }
}
