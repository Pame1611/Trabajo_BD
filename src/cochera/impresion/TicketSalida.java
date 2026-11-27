package cochera.impresion;

import cochera.modelo.Alquiler;
import cochera.modelo.AlquilerMensual;
import cochera.modelo.Pago;
import cochera.util.Dinero;
import cochera.util.FechaUtil;
import java.time.LocalDateTime;

/**
 * Ticket de SALIDA (comprobante interno de cobro) — construirTicketSalida() de lib/escpos.js.
 * También sirve para la reimpresión desde "Mis cobros".
 */
public class TicketSalida extends TicketImprimible {

    private final Pago pago;

    public TicketSalida(Pago pago) {
        this.pago = pago;
    }

    @Override
    public String getTitulo() {
        return "Ticket de salida " + pago.getTicket().getCodigoBarras();
    }


    private LocalDateTime horaSalida() {
        Alquiler t = pago.getTicket();
        return t.getHoraSalida() != null ? t.getHoraSalida() : pago.getCreadoEn();
    }

    @Override
    protected String encabezado() {
        return FormatoTermico.centro("TICKET DE SALIDA")
                + FormatoTermico.centro("- INTERNO -")
                + FormatoTermico.centro("COCHERA LOS ANGELES")
                + FormatoTermico.separador();
    }

    @Override
    protected String cuerpo() {
        Alquiler t = pago.getTicket();
        long minutos = FechaUtil.minutosEntre(t.getHoraEntrada(), horaSalida());
        return FormatoTermico.fila("Correlativo:", t.getCodigoBarras())
                + FormatoTermico.fila("Placa:", t.getPlacaFormateada())
                + FormatoTermico.fila("DNI Cliente:", t.getCliente() == null ? "-" : t.getCliente().getDni())
                + FormatoTermico.fila("Vehiculo:", t.getTarifa().getTipoVehiculo())
                + FormatoTermico.fila("Modelo:", t.getDescripcionVehiculo())
                + FormatoTermico.fila("Hora Entrada:", FechaUtil.hora(t.getHoraEntrada()))
                + FormatoTermico.fila("Hora Salida:", FechaUtil.hora(horaSalida()))
                + FormatoTermico.fila("Tiempo Total:", FechaUtil.formatoMinutos(minutos))
                + FormatoTermico.separador()
                + detalleTarifa(t, minutos)
                + FormatoTermico.fila("Subtotal:", Dinero.soles(getSubtotal()))
                + FormatoTermico.fila(t instanceof AlquilerMensual ? "Desc. 10%:" : "Descuento:", "-" + Dinero.soles(getDescuento()))
                + FormatoTermico.centro("TOTAL: " + Dinero.soles(pago.getMontoTotal()))
                + FormatoTermico.separador();
    }

    private static String detalleTarifa(Alquiler t, long minutos) {
        if (t instanceof AlquilerMensual) {
            AlquilerMensual m = (AlquilerMensual) t;
            return FormatoTermico.fila("Modalidad:", "Por mes")
                    + FormatoTermico.fila("Inicio:", FechaUtil.fecha(m.getFechaInicio()))
                    + FormatoTermico.fila("Fin:", FechaUtil.fecha(m.getFechaFin()))
                    + FormatoTermico.fila("Tarifa mensual:", Dinero.soles(t.getTarifa().getTarifaMensual()));
        }
        return FormatoTermico.fila("Precio x hora:", Dinero.soles(t.getTarifa().getPrecioHora()))
                + FormatoTermico.fila("Minutos:", String.valueOf(minutos));
    }

    /** Subtotal antes del descuento (en el alquiler por hora es igual al total). */
    public double getSubtotal() {
        return Math.max(pago.getMontoTotal(), pago.getTicket().calcularSubtotal(horaSalida()));
    }

    /** Descuento = subtotal - total cobrado (10% por vehículo en el alquiler mensual). */
    public double getDescuento() {
        return Math.max(0, cochera.util.Dinero.redondear(getSubtotal() - pago.getMontoTotal()));
    }

    @Override
    protected String pie() {
        return FormatoTermico.fila("Metodo Pago:", pago.getMetodoPago().getEtiqueta())
                + FormatoTermico.centro("!Gracias por su preferencia!");
    }
}
