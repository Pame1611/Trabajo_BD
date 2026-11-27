package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.dto.CotizacionSalida;
import cochera.dto.ResultadoCobro;
import cochera.dto.ResultadoTarifa;
import cochera.excepcion.CocheraException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Alquiler;
import cochera.modelo.MetodoPago;
import cochera.modelo.Pago;
import cochera.modelo.TurnoCaja;
import cochera.util.Dinero;
import cochera.util.Dni;
import cochera.util.Placa;
import cochera.util.Reloj;
import cochera.util.Texto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SALIDA y cobro de vehículos.
 *
 * Regla crítica de traslape de turnos: el cajero A pudo registrar la entrada y el
 * cajero B cobra la salida; el pago queda en el turno de B (quien cobra).
 */
public class CheckoutServicio {

    private static final int MAX_SUGERENCIAS = 5;

    private final BaseDatos bd;
    private final Reloj reloj;
    private final AutenticacionServicio auth;
    private final TurnoServicio turnos;
    private final CalculadoraTarifa calculadora;
    private final VentaServicio ventas;

    public CheckoutServicio(BaseDatos bd, Reloj reloj, AutenticacionServicio auth, TurnoServicio turnos,
                            CalculadoraTarifa calculadora, VentaServicio ventas) {
        this.bd = bd;
        this.reloj = reloj;
        this.auth = auth;
        this.turnos = turnos;
        this.calculadora = calculadora;
        this.ventas = ventas;
    }

    /** ¿El texto escrito en la búsqueda es un DNI (8 dígitos)? */
    public static boolean esDni(String entrada) {
        return Dni.esValido(entrada);
    }

    /**
     * Acepta lo que escanea el lector (código "TK-...") o una placa escrita a mano.
     * Los lectores de código de barras USB funcionan como un teclado.
     * Para buscar por DNI del cliente se usa {@link #alquileresEnCursoDeCliente(String)}.
     */
    public CotizacionSalida buscar(String entrada) throws CocheraException {
        String valor = entrada == null ? "" : entrada.trim().toUpperCase();
        if (valor.startsWith("TK-")) {
            return buscarTicket(valor, null);
        }
        return buscarTicket(null, valor);
    }

    /** Alquileres EN_CURSO del cliente con ese DNI, para que el cajero elija cuál cobrar. */
    public List<Alquiler> alquileresEnCursoDeCliente(String dni) throws CocheraException {
        turnos.requerirTurnoAbierto("No tienes un turno abierto. Abre tu turno desde el Inicio.");
        String dniNorm = Dni.validar(dni, "del cliente");
        if (bd.buscarCliente(dniNorm) == null) {
            throw new ValidacionException("No hay ningún cliente registrado con el DNI " + dniNorm + ".");
        }
        List<Alquiler> lista = bd.listarTicketsEnCursoDeCliente(dniNorm);
        if (lista.isEmpty()) {
            throw new ValidacionException("El cliente con DNI " + dniNorm + " no tiene vehículos dentro.");
        }
        return lista;
    }

    /** Busca un alquiler EN_CURSO (el código tiene prioridad sobre la placa) y calcula el monto. */
    public CotizacionSalida buscarTicket(String codigoBarras, String placa) throws CocheraException {
        turnos.requerirTurnoAbierto("No tienes un turno abierto. Abre tu turno desde el Inicio.");
        if (Texto.vacio(codigoBarras) && Texto.vacio(placa)) {
            throw new ValidacionException("Ingresa una placa, el DNI del cliente o escanea el código de barras.");
        }

        Alquiler ticket;
        if (!Texto.vacio(codigoBarras)) {
            String codigo = codigoBarras.trim().toUpperCase();
            ticket = bd.buscarTicketPorCodigo(codigo);
            if (ticket == null) {
                throw new ValidacionException("No se encontró ningún ticket con código " + codigo + ".");
            }
        } else {
            String placaNorm = Placa.normalizar(placa);
            ticket = bd.buscarTicketEnCursoPorPlaca(placaNorm);
            if (ticket == null) {
                throw new ValidacionException("No hay ningún vehículo EN_CURSO con placa " + Placa.formatear(placaNorm) + ".");
            }
        }
        validarEnCurso(ticket);

        LocalDateTime ahora = reloj.ahora();
        return new CotizacionSalida(ticket, calculadora.calcular(ticket, ahora), ahora);
    }

    /** Autocompletado de placas: hasta 5 placas EN_CURSO que empiezan con el prefijo. */
    public List<String> sugerirPlacas(String prefijo) throws CocheraException {
        auth.requerirSesion();
        String p = Placa.normalizar(prefijo);
        List<String> sugerencias = new ArrayList<>();
        for (Alquiler t : bd.listarTicketsEnCurso()) {
            if (!p.isEmpty() && t.getPlaca().startsWith(p) && sugerencias.size() < MAX_SUGERENCIAS) {
                sugerencias.add(t.getPlacaFormateada());
            }
        }
        return sugerencias;
    }

    public double calcularVuelto(double totalACobrar, double montoRecibido) throws ValidacionException {
        if (montoRecibido < totalACobrar) {
            throw new ValidacionException("El monto recibido (" + Dinero.soles(montoRecibido)
                    + ") es menor al total a cobrar (" + Dinero.soles(totalACobrar) + ").");
        }
        return Dinero.redondear(montoRecibido - totalACobrar);
    }

    /**
     * Registra el pago, la venta del cliente y cierra el alquiler.
     *  1. Re-verifica el turno abierto de quien cobra AHORA.
     *  2. Re-calcula el monto al momento exacto del cobro (calcularMonto()).
     *  3. Inserta el pago en el turno del cobrador, la venta del cliente y marca el alquiler PAGADO.
     *
     * @param montoRecibido solo para Efectivo (null = pago exacto).
     */
    public ResultadoCobro registrarPago(Alquiler ticket, MetodoPago metodo, Double montoRecibido)
            throws CocheraException {
        TurnoCaja turno = turnos.requerirTurnoAbierto("No tienes un turno abierto. No puedes registrar pagos.");
        if (ticket == null) {
            throw new ValidacionException("No se encontró el ticket. Intenta de nuevo.");
        }
        if (metodo == null) {
            throw new ValidacionException("Selecciona un método de pago.");
        }

        // Bloqueo sobre el alquiler: si dos cajeros cobran el mismo auto a la vez, solo uno lo logra.
        synchronized (ticket) {
            validarEnCurso(ticket);
            LocalDateTime horaSalida = reloj.ahora();
            ResultadoTarifa tarifa = calculadora.calcular(ticket, horaSalida);

            double recibido = tarifa.getTarifaTotal();
            double vuelto = 0;
            if (metodo == MetodoPago.EFECTIVO && montoRecibido != null) {
                vuelto = calcularVuelto(tarifa.getTarifaTotal(), montoRecibido);
                recibido = montoRecibido;
            }

            Pago pago = new Pago(ticket, turno, tarifa.getTarifaTotal(), metodo, horaSalida);
            bd.insertarPago(pago);
            ventas.registrar(ticket, pago);
            ticket.registrarSalida(horaSalida);
            return new ResultadoCobro(pago, tarifa, recibido, vuelto);
        }
    }

    private static void validarEnCurso(Alquiler ticket) throws ValidacionException {
        if (!ticket.estaEnCurso()) {
            String estado = ticket.getEstado().name().equals("PAGADO") ? "cobrado" : "anulado";
            throw new ValidacionException("El ticket " + ticket.getCodigoBarras() + " ya fue " + estado + ".");
        }
    }
}
