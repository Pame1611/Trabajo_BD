package cochera.dto;

import cochera.modelo.Alquiler;
import java.time.LocalDateTime;

/**
 * Ticket EN_CURSO encontrado en el check-out con su tarifa calculada al momento
 * (TicketEncontrado en app/actions/checkout.ts).
 */
public class CotizacionSalida {

    private final Alquiler ticket;
    private final ResultadoTarifa tarifa;
    private final LocalDateTime calculadoEn;

    public CotizacionSalida(Alquiler ticket, ResultadoTarifa tarifa, LocalDateTime calculadoEn) {
        this.ticket = ticket;
        this.tarifa = tarifa;
        this.calculadoEn = calculadoEn;
    }

    public Alquiler getTicket() {
        return ticket;
    }

    public ResultadoTarifa getTarifa() {
        return tarifa;
    }

    public LocalDateTime getCalculadoEn() {
        return calculadoEn;
    }
}
