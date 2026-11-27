package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.CocheraException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Alquiler;
import cochera.modelo.Cliente;
import cochera.modelo.Modelo;
import cochera.modelo.Tarifa;
import cochera.modelo.TipoAlquiler;
import cochera.modelo.TurnoCaja;
import cochera.util.Placa;
import cochera.util.Reloj;
import java.util.List;

/**
 * Registro de ENTRADA de vehículos: crea el alquiler con el DNI del cliente y del empleado.
 */
public class CheckinServicio {

    private final BaseDatos bd;
    private final Reloj reloj;
    private final TurnoServicio turnos;
    private final ClienteServicio clientes;
    private final VehiculoServicio vehiculos;

    public CheckinServicio(BaseDatos bd, Reloj reloj, TurnoServicio turnos, ClienteServicio clientes,
                           VehiculoServicio vehiculos) {
        this.bd = bd;
        this.reloj = reloj;
        this.turnos = turnos;
        this.clientes = clientes;
        this.vehiculos = vehiculos;
    }

    public List<Tarifa> obtenerTarifario() {
        return bd.listarTarifas();
    }

    /**
     * Flujo:
     *  1. Verifica turno ABIERTO (el empleado del turno queda en el alquiler).
     *  2. Valida el DNI del cliente y lo registra si todavía no existe.
     *  3. Normaliza la placa y valida el modelo y el tipo de vehículo. Sin modelo se usa
     *     "Desconocido"; si el modelo tiene tipo y no se eligió otro, se usa el del modelo.
     *  4. Rechaza duplicados (misma placa ya EN_CURSO).
     *  5. Genera el correlativo "TK-AAAA-NNNNNN" e inserta, en una sola operación atómica.
     *
     * @param nombreCliente solo se usa si el cliente es nuevo (puede ir vacío).
     * @param idModelo 0 = Desconocido.
     * @param idTarifa 0 = el tipo del modelo.
     */
    public Alquiler registrarEntrada(String dniCliente, String nombreCliente, String placa, int idModelo,
                                     int idTarifa) throws CocheraException {
        return registrarEntrada(dniCliente, nombreCliente, placa, idModelo, idTarifa, TipoAlquiler.POR_HORA);
    }

    /**
     * Registra la entrada por hora o por mes. El alquiler por mes guarda la fecha de inicio y la
     * de fin (30 días) y cobra la tarifa mensual del tipo con 10% de descuento.
     */
    public Alquiler registrarEntrada(String dniCliente, String nombreCliente, String placa, int idModelo,
                                     int idTarifa, TipoAlquiler tipo) throws CocheraException {
        TurnoCaja turno = turnos.requerirTurnoAbierto(
                "No tienes un turno abierto. Abre tu turno desde el Inicio antes de registrar entradas.");

        String placaNorm = Placa.normalizar(placa);
        if (placaNorm.isEmpty()) {
            throw new ValidacionException("La placa no puede estar vacía.");
        }
        if (placaNorm.length() < 3) {
            throw new ValidacionException("La placa ingresada es demasiado corta.");
        }
        Modelo modelo = idModelo <= 0 ? vehiculos.modeloDesconocido() : bd.buscarModelo(idModelo);
        if (modelo == null) {
            throw new ValidacionException("Modelo de vehículo no válido.");
        }
        if (idTarifa <= 0 && modelo.getTipoVehiculo() != null) {
            idTarifa = modelo.getTipoVehiculo().getId();
        }
        if (idTarifa <= 0) {
            throw new ValidacionException("Selecciona un tipo de vehículo.");
        }
        Tarifa tarifa = bd.buscarTarifa(idTarifa);
        if (tarifa == null) {
            throw new ValidacionException("Tipo de vehículo no válido.");
        }

        if (tipo == TipoAlquiler.MENSUAL && !tarifa.ofreceMensual()) {
            throw new ValidacionException("El tipo " + tarifa.getTipoVehiculo() + " no tiene tarifa mensual.");
        }

        Alquiler duplicado = bd.buscarTicketEnCursoPorPlaca(placaNorm);
        if (duplicado != null) {
            throw new ValidacionException("Esta placa ya tiene un alquiler activo (" + duplicado.getCodigoBarras()
                    + "). Realiza la salida primero.");
        }

        Cliente cliente = clientes.obtenerORegistrar(dniCliente, nombreCliente);
        return bd.registrarTicket(placaNorm, tarifa, reloj.ahora(), cliente, turno.getEmpleado(), modelo,
                tipo == null ? TipoAlquiler.POR_HORA : tipo);
    }
}
