package cochera.impresion;

import cochera.modelo.Alquiler;
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
                + "\n"
                + FormatoTermico.centro(ticket.getTarifa().getTipoVehiculo() + " S/."
                        + Dinero.decimal(ticket.getTarifa().getPrecioHora()))
                + FormatoTermico.centro("POR HORA (SE COBRA POR MINUTO)")
                + "\n"
                + FormatoTermico.codigoBarras(ticket.getCodigoBarras());
    }

    @Override
    protected String pie() {
        return "Condiciones:\n"
                + "- No perder el ticket\n"
                + "* Se cobra por minuto segun la\n"
                + "tarifa por hora del vehiculo.\n";
    }
}
