package cochera.datos;

import cochera.excepcion.ValidacionException;
import cochera.modelo.Alquiler;
import cochera.modelo.AlquilerMensual;
import cochera.modelo.Cliente;
import cochera.modelo.Marca;
import cochera.modelo.Modelo;
import cochera.modelo.Pago;
import cochera.modelo.Tarifa;
import cochera.modelo.TipoAlquiler;
import cochera.modelo.TurnoCaja;
import cochera.modelo.Usuario;
import cochera.modelo.Venta;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base de datos EN MEMORIA.
 *
 * Cada lista equivale a una tabla de sql/schema.sql (empleado, tipo_vehiculo, turno_caja,
 * cliente, alquiler, pago, venta). Las reglas de llave primaria y llave foránea se
 * validan aquí antes de insertar o eliminar, igual que lo haría el motor de base de datos.
 *
 * Los bloques synchronized sobre {@code candado} garantizan códigos de ticket únicos aunque
 * dos cajeros registren entradas al mismo tiempo. Las listas son CopyOnWriteArrayList para
 * que una consulta nunca falle mientras otro cajero está insertando.
 */
public class BaseDatos {

    private final List<Usuario> usuarios = new CopyOnWriteArrayList<>();
    private final List<Tarifa> tarifas = new CopyOnWriteArrayList<>();
    private final List<Marca> marcas = new CopyOnWriteArrayList<>();
    private final List<Modelo> modelos = new CopyOnWriteArrayList<>();
    private final List<TurnoCaja> turnos = new CopyOnWriteArrayList<>();
    private final List<Cliente> clientes = new CopyOnWriteArrayList<>();
    private final List<Alquiler> tickets = new CopyOnWriteArrayList<>();
    private final List<Pago> pagos = new CopyOnWriteArrayList<>();
    private final List<Venta> ventas = new CopyOnWriteArrayList<>();
    private final Object candado = new Object();

    // ───────────────────────────── EMPLEADOS (usuarios) ─────────────────────────────

    /** Llave primaria: el DNI no se puede repetir. */
    public void insertarUsuario(Usuario usuario) throws ValidacionException {
        synchronized (candado) {
            if (buscarUsuarioPorDni(usuario.getDni()) != null) {
                throw new ValidacionException("Ya existe un empleado con el DNI " + usuario.getDni() + ".");
            }
            usuarios.add(usuario);
        }
    }

    public Usuario buscarUsuarioPorDni(String dni) {
        if (dni == null) {
            return null;
        }
        String buscado = dni.trim();
        for (Usuario u : usuarios) {
            if (u.getDni().equals(buscado)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    // ───────────────────────────── TARIFARIO (tipo_vehiculo) ─────────────────────────────

    /** UNIQUE(nombre): no se repite un tipo de vehículo. */
    public void insertarTarifa(Tarifa tarifa) throws ValidacionException {
        synchronized (candado) {
            for (Tarifa t : tarifas) {
                if (t.getTipoVehiculo().equalsIgnoreCase(tarifa.getTipoVehiculo())) {
                    throw new ValidacionException("Ya existe el tipo de vehículo " + t.getTipoVehiculo() + ".");
                }
            }
            tarifas.add(tarifa);
            tarifas.sort(Comparator.comparingInt(Tarifa::getId));
        }
    }

    /** Siguiente id para un tipo de vehículo nuevo (IDENTITY). */
    public int siguienteIdTarifa() {
        int maximo = 0;
        for (Tarifa t : tarifas) {
            maximo = Math.max(maximo, t.getId());
        }
        return maximo + 1;
    }

    public List<Tarifa> listarTarifas() {
        return Collections.unmodifiableList(tarifas);
    }

    public Tarifa buscarTarifa(int id) {
        for (Tarifa t : tarifas) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    // ───────────────────────────── MARCA y MODELO ─────────────────────────────

    /** UNIQUE(nombre) en la tabla marca. */
    public void insertarMarca(Marca marca) throws ValidacionException {
        synchronized (candado) {
            if (buscarMarcaPorNombre(marca.getNombre()) != null) {
                throw new ValidacionException("Ya existe la marca " + marca.getNombre() + ".");
            }
            marcas.add(marca);
        }
    }

    public Marca buscarMarca(int id) {
        for (Marca m : marcas) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public Marca buscarMarcaPorNombre(String nombre) {
        for (Marca m : marcas) {
            if (m.getNombre().equalsIgnoreCase(nombre.trim())) {
                return m;
            }
        }
        return null;
    }

    /** Marcas en orden alfabético, con "Desconocido" primero. */
    public List<Marca> listarMarcas() {
        List<Marca> lista = new ArrayList<>(marcas);
        lista.sort((a, b) -> a.esDesconocida() ? -1 : b.esDesconocida() ? 1
                : a.getNombre().compareToIgnoreCase(b.getNombre()));
        return lista;
    }

    public int siguienteIdMarca() {
        int maximo = 0;
        for (Marca m : marcas) {
            maximo = Math.max(maximo, m.getId());
        }
        return maximo + 1;
    }

    /** UNIQUE(marca_id, nombre) y la marca debe existir (llave foránea). */
    public void insertarModelo(Modelo modelo) throws ValidacionException {
        synchronized (candado) {
            if (buscarMarca(modelo.getMarca().getId()) == null) {
                throw new ValidacionException("La marca " + modelo.getMarca() + " no existe.");
            }
            for (Modelo m : modelos) {
                if (m.getMarca() == modelo.getMarca() && m.getNombre().equalsIgnoreCase(modelo.getNombre())) {
                    throw new ValidacionException("La marca " + modelo.getMarca() + " ya tiene el modelo " + m.getNombre() + ".");
                }
            }
            modelos.add(modelo);
        }
    }

    public Modelo buscarModelo(int id) {
        for (Modelo m : modelos) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    /** Modelos de una marca, con "Desconocido" primero y el resto en orden alfabético. */
    public List<Modelo> modelosDeMarca(Marca marca) {
        List<Modelo> lista = new ArrayList<>();
        for (Modelo m : modelos) {
            if (m.getMarca() == marca) {
                lista.add(m);
            }
        }
        lista.sort((a, b) -> a.esDesconocido() ? -1 : b.esDesconocido() ? 1
                : a.getNombre().compareToIgnoreCase(b.getNombre()));
        return lista;
    }

    public int siguienteIdModelo() {
        int maximo = 0;
        for (Modelo m : modelos) {
            maximo = Math.max(maximo, m.getId());
        }
        return maximo + 1;
    }

    /** Llave foránea alquiler.modelo_id: no se elimina un modelo que ya se usó en un alquiler. */
    public void eliminarModelo(Modelo modelo) throws ValidacionException {
        synchronized (candado) {
            for (Alquiler a : tickets) {
                if (a.getModelo() == modelo) {
                    throw new ValidacionException("No se puede eliminar " + modelo.getDescripcion()
                            + ": ya se usó en el alquiler " + a.getCodigoBarras() + ".");
                }
            }
            modelos.remove(modelo);
        }
    }

    /** Elimina la marca y sus modelos (ON DELETE CASCADE), salvo que algún modelo ya se haya usado. */
    public void eliminarMarca(Marca marca) throws ValidacionException {
        synchronized (candado) {
            for (Alquiler a : tickets) {
                if (a.getModelo() != null && a.getModelo().getMarca() == marca) {
                    throw new ValidacionException("No se puede eliminar la marca " + marca
                            + ": ya se usó en el alquiler " + a.getCodigoBarras() + ".");
                }
            }
            modelos.removeIf(m -> m.getMarca() == marca);
            marcas.remove(marca);
        }
    }

    // ───────────────────────────── TURNO_CAJA ─────────────────────────────

    public void insertarTurno(TurnoCaja turno) {
        turnos.add(turno);
    }

    /** Turno ABIERTO del usuario (el más reciente si hubiera más de uno). */
    public TurnoCaja buscarTurnoAbierto(Usuario usuario) {
        TurnoCaja encontrado = null;
        for (TurnoCaja t : turnos) {
            if (t.estaAbierto() && t.perteneceA(usuario)) {
                encontrado = t;
            }
        }
        return encontrado;
    }

    public List<TurnoCaja> listarTurnos() {
        return Collections.unmodifiableList(turnos);
    }

    // ───────────────────────────── CLIENTE ─────────────────────────────

    /** Llave primaria: el DNI del cliente no se puede repetir. */
    public void insertarCliente(Cliente cliente) throws ValidacionException {
        synchronized (candado) {
            if (buscarCliente(cliente.getDni()) != null) {
                throw new ValidacionException("Ya existe un cliente con el DNI " + cliente.getDni() + ".");
            }
            clientes.add(cliente);
        }
    }

    public Cliente buscarCliente(String dni) {
        if (dni == null) {
            return null;
        }
        String buscado = dni.trim();
        for (Cliente c : clientes) {
            if (c.getDni().equals(buscado)) {
                return c;
            }
        }
        return null;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>(clientes);
        lista.sort(Comparator.comparing(Cliente::getNombreCompleto, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    /**
     * Respeta las llaves foráneas: no se elimina un cliente que ya tiene ventas
     * o alquileres registrados (ON DELETE NO ACTION).
     */
    public void eliminarCliente(Cliente cliente) throws ValidacionException {
        synchronized (candado) {
            int nVentas = ventasDeCliente(cliente.getDni()).size();
            if (nVentas > 0) {
                throw new ValidacionException("No se puede eliminar a " + cliente.getNombreCompleto()
                        + ": tiene " + nVentas + " venta(s) registrada(s).");
            }
            for (Alquiler a : tickets) {
                if (a.getCliente() == cliente) {
                    throw new ValidacionException("No se puede eliminar a " + cliente.getNombreCompleto()
                            + ": tiene alquileres registrados (" + a.getCodigoBarras() + ").");
                }
            }
            clientes.remove(cliente);
        }
    }

    // ───────────────────────────── ALQUILER (antes tickets_cochera) ─────────────────────────────

    /**
     * Respeta la restricción UNIQUE de codigo_barras y vuelve a comprobar, dentro del
     * candado, que la placa no haya entrado un instante antes por otro cajero.
     */
    public void insertarTicket(Alquiler ticket) throws ValidacionException {
        synchronized (candado) {
            if (buscarTicketPorCodigo(ticket.getCodigoBarras()) != null) {
                throw new ValidacionException("Colisión de correlativo: " + ticket.getCodigoBarras());
            }
            if (ticket.estaEnCurso() && buscarTicketEnCursoPorPlaca(ticket.getPlaca()) != null) {
                throw new ValidacionException("Placa duplicada EN_CURSO: " + ticket.getPlaca());
            }
            if (ticket.getCliente() != null && buscarCliente(ticket.getCliente().getDni()) == null) {
                throw new ValidacionException("El cliente " + ticket.getCliente().getDni() + " no está registrado.");
            }
            tickets.add(ticket);
        }
    }

    /**
     * Entrada ATÓMICA: dentro de un solo bloque bajo candado verifica que la placa no esté
     * EN_CURSO, genera el siguiente código "TK-AAAA-NNNNNN" e inserta el alquiler.
     * Así dos cajeros nunca obtienen el mismo número ni registran la misma placa dos veces.
     */
    public Alquiler registrarTicket(String placa, Tarifa tarifa, LocalDateTime horaEntrada,
                                    Cliente cliente, Usuario empleado, Modelo modelo) throws ValidacionException {
        return registrarTicket(placa, tarifa, horaEntrada, cliente, empleado, modelo, TipoAlquiler.POR_HORA);
    }

    /** Igual que el anterior, creando un Alquiler por hora o un AlquilerMensual según la modalidad. */
    public Alquiler registrarTicket(String placa, Tarifa tarifa, LocalDateTime horaEntrada, Cliente cliente,
                                    Usuario empleado, Modelo modelo, TipoAlquiler tipo) throws ValidacionException {
        synchronized (candado) {
            Alquiler duplicado = buscarTicketEnCursoPorPlaca(placa);
            if (duplicado != null) {
                throw new ValidacionException("Esta placa ya tiene un alquiler activo (" + duplicado.getCodigoBarras()
                        + "). Realiza la salida primero.");
            }
            if (cliente == null || buscarCliente(cliente.getDni()) == null) {
                throw new ValidacionException("El alquiler necesita un cliente registrado.");
            }
            if (modelo == null || buscarModelo(modelo.getId()) == null) {
                throw new ValidacionException("El modelo del vehículo no existe.");
            }
            String codigo = generarCorrelativoTicket(horaEntrada.getYear());
            Alquiler ticket = tipo == TipoAlquiler.MENSUAL
                    ? new AlquilerMensual(placa, tarifa, horaEntrada, codigo, cliente, empleado, modelo)
                    : new Alquiler(placa, tarifa, horaEntrada, codigo, cliente, empleado, modelo);
            tickets.add(ticket);
            return ticket;
        }
    }

    public Alquiler buscarTicketPorCodigo(String codigo) {
        for (Alquiler t : tickets) {
            if (t.getCodigoBarras().equalsIgnoreCase(codigo)) {
                return t;
            }
        }
        return null;
    }

    public Alquiler buscarTicketEnCursoPorPlaca(String placa) {
        for (Alquiler t : tickets) {
            if (t.estaEnCurso() && t.getPlaca().equals(placa)) {
                return t;
            }
        }
        return null;
    }

    public List<Alquiler> listarTicketsEnCurso() {
        List<Alquiler> enCurso = new ArrayList<>();
        for (Alquiler t : tickets) {
            if (t.estaEnCurso()) {
                enCurso.add(t);
            }
        }
        return enCurso;
    }

    /** Alquileres EN_CURSO de un cliente (búsqueda por DNI en la salida). */
    public List<Alquiler> listarTicketsEnCursoDeCliente(String dni) {
        List<Alquiler> lista = new ArrayList<>();
        for (Alquiler t : tickets) {
            if (t.estaEnCurso() && t.getCliente() != null && t.getCliente().getDni().equals(dni)) {
                lista.add(t);
            }
        }
        return lista;
    }

    public List<Alquiler> listarTickets() {
        return Collections.unmodifiableList(tickets);
    }

    /** Alquileres por mes (tabla alquiler con tipo_alquiler = 'MENSUAL'), del más reciente al más antiguo. */
    public List<AlquilerMensual> listarAlquileresMensuales() {
        List<AlquilerMensual> lista = new ArrayList<>();
        for (Alquiler t : tickets) {
            if (t instanceof AlquilerMensual) {
                lista.add((AlquilerMensual) t);
            }
        }
        lista.sort(Comparator.comparing(AlquilerMensual::getFechaInicio).reversed());
        return lista;
    }

    /**
     * Genera el código de barras "TK-2026-000001".
     * Lee el MÁXIMO del año bajo candado para que dos entradas no obtengan el mismo número.
     */
    public String generarCorrelativoTicket(int anio) {
        synchronized (candado) {
            String prefijo = "TK-" + anio + "-";
            int maximo = 0;
            for (Alquiler t : tickets) {
                if (t.getCodigoBarras().startsWith(prefijo)) {
                    int n = Integer.parseInt(t.getCodigoBarras().substring(prefijo.length()));
                    maximo = Math.max(maximo, n);
                }
            }
            return String.format("%s%06d", prefijo, maximo + 1);
        }
    }

    // ───────────────────────────── PAGO ─────────────────────────────

    public void insertarPago(Pago pago) {
        synchronized (candado) {
            pagos.add(pago);
        }
    }

    public Pago buscarPagoDeTicket(Alquiler ticket) {
        for (Pago p : pagos) {
            if (p.getTicket() == ticket) {
                return p;
            }
        }
        return null;
    }

    public List<Pago> listarPagos() {
        return Collections.unmodifiableList(pagos);
    }

    public List<Pago> pagosDeTurno(TurnoCaja turno) {
        List<Pago> lista = new ArrayList<>();
        for (Pago p : pagos) {
            if (p.getTurno() == turno) {
                lista.add(p);
            }
        }
        return lista;
    }

    // ───────────────────────────── VENTA ─────────────────────────────

    /** Llave foránea: la venta solo se guarda si su cliente existe en la tabla cliente. */
    public void insertarVenta(Venta venta) throws ValidacionException {
        synchronized (candado) {
            if (buscarCliente(venta.getCliente().getDni()) == null) {
                throw new ValidacionException("No se puede registrar la venta: el cliente "
                        + venta.getCliente().getDni() + " no existe.");
            }
            ventas.add(venta);
        }
    }

    public List<Venta> listarVentas() {
        return Collections.unmodifiableList(ventas);
    }

    /**
     * Equivale a: SELECT v.* FROM venta v JOIN cliente c ON c.dni = v.cliente_dni WHERE c.dni = ?
     * ORDER BY v.fecha DESC.
     */
    public List<Venta> ventasDeCliente(String dni) {
        List<Venta> lista = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getCliente().getDni().equals(dni)) {
                lista.add(v);
            }
        }
        lista.sort(Comparator.comparing(Venta::getFecha).reversed());
        return lista;
    }
}
