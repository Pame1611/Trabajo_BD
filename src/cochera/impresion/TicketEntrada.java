package cochera.impresion;

import cochera.modelo.Alquiler;
import cochera.modelo.AlquilerMensual;
import cochera.util.Dinero;
import cochera.util.FechaUtil;

/**
 * Ticket de ENTRADA que se entrega al conductor en el check-in
 * (construirTicketEntrada() de lib/escpos.js).
 */
public class TicketEntrada extends TicketImprimible {

    private final Alquiler ticket;

    public TicketEntrada(Alquiler ticket) {
        this.ticket = ticket;
    }

    @Override
    public String getTitulo() {
        return "Ticket de ingreso " + ticket.getCodigoBarras();
    }

    @Override
    protected String encabezado() {
        return FormatoTermico.derecha("N.: " + ticket.getCodigoBarras())
                + FormatoTermico.centro("TICKET-INGRESO")
                + FormatoTermico.centro("REPRESENTACIONES E INVERSIONES\nLOS ANGELES SAC\n"
                        + "AV. JOSE GABRIEL CONDORCANQUI\nNRO. 1413 LA ESPERANZA-TRUJILLO")
                + "\n";
    }

    @Override
    protected String cuerpo() {
        return FormatoTermico.centro(ticket.getPlacaFormateada())
                + "\n"
                + FormatoTermico.fila("FECHA", FechaUtil.fecha(ticket.getHoraEntrada()))
                + FormatoTermico.fila("INGRESO", FechaUtil.hora(ticket.getHoraEntrada()))
                + "\n"
                + FormatoTermico.fila("DNI CLIENTE", ticket.getCliente() == null ? "-" : ticket.getCliente().getDni())
                + FormatoTermico.fila("VEHICULO", ticket.getDescripcionVehiculo())
                + "\n"
                + tarifa()
                + "\n"
                + FormatoTermico.codigoBarras(ticket.getCodigoBarras());
    }

    /** Por hora: precio por hora. Por mes: fechas, tarifa mensual, descuento del 10% y total del mes. */
    private String tarifa() {
        if (ticket instanceof AlquilerMensual) {
            AlquilerMensual m = (AlquilerMensual) ticket;
            double subtotal = m.calcularSubtotal(m.getFechaInicio());
            return FormatoTermico.centro("ALQUILER MENSUAL")
                    + FormatoTermico.fila("INICIO", FechaUtil.fecha(m.getFechaInicio()))
                    + FormatoTermico.fila("FIN", FechaUtil.fecha(m.getFechaFin()))
                    + FormatoTermico.fila(ticket.getTarifa().getTipoVehiculo(), "S/." + Dinero.decimal(subtotal))
                    + FormatoTermico.fila("DSCTO 10%", "-S/." + Dinero.decimal(m.calcularDescuento(m.getFechaInicio())))
                    + FormatoTermico.fila("TOTAL DEL MES", "S/." + Dinero.decimal(m.calcularMonto(m.getFechaInicio())));
        }
        return FormatoTermico.centro(ticket.getTarifa().getTipoVehiculo() + " S/."
                + Dinero.decimal(ticket.getTarifa().getPrecioHora()))
                + FormatoTermico.centro("POR HORA (SE COBRA POR MINUTO)");
    }

    @Override
    protected String pie() {
        if (ticket instanceof AlquilerMensual) {
            return "Condiciones:\n"
                    + "- No perder el ticket\n"
                    + "* Alquiler por 30 dias con 10%\n"
                    + "de descuento por vehiculo.\n";
        }
        return "Condiciones:\n"
                + "- No perder el ticket\n"
                + "* Se cobra por minuto segun la\n"
                + "tarifa por hora del vehiculo.\n";
    }
}
