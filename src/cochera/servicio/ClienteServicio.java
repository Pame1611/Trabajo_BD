package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.CocheraException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Cliente;
import cochera.util.Dni;
import cochera.util.Reloj;
import cochera.util.Texto;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD de clientes (crear, consultar, actualizar y eliminar).
 * Cualquier empleado con sesión puede registrar clientes al hacer una entrada;
 * actualizar y eliminar desde la pantalla Clientes es solo del administrador.
 */
public class ClienteServicio {

    private final BaseDatos bd;
    private final Reloj reloj;
    private final AutenticacionServicio auth;

    public ClienteServicio(BaseDatos bd, Reloj reloj, AutenticacionServicio auth) {
        this.bd = bd;
        this.reloj = reloj;
        this.auth = auth;
    }

    /** Consulta por DNI (null si no existe). */
    public Cliente buscar(String dni) throws CocheraException {
        auth.requerirSesion();
        return bd.buscarCliente(Dni.normalizar(dni));
    }

    /** Lista todos los clientes, o los que coinciden con el texto en DNI o nombre. */
    public List<Cliente> listar(String filtro) throws CocheraException {
        auth.requerirSesion();
        String f = filtro == null ? "" : filtro.trim().toLowerCase();
        List<Cliente> lista = new ArrayList<>();
        for (Cliente c : bd.listarClientes()) {
            if (f.isEmpty() || c.getDni().contains(f) || c.getNombreCompleto().toLowerCase().contains(f)) {
                lista.add(c);
            }
        }
        return lista;
    }

    /** Crear: el DNI debe tener 8 dígitos y no estar repetido. */
    public Cliente registrar(String dni, String nombre, String telefono) throws CocheraException {
        auth.requerirSesion();
        String dniNorm = Dni.validar(dni, "del cliente");
        if (Texto.vacio(nombre)) {
            throw new ValidacionException("Ingresa el nombre del cliente.");
        }
        Cliente nuevo = new Cliente(dniNorm, nombre, telefono, reloj.ahora());
        bd.insertarCliente(nuevo);
        return nuevo;
    }

    /**
     * Usado en la entrada de vehículos: devuelve el cliente con ese DNI y, si todavía
     * no existe, lo registra (el nombre es opcional para no demorar la atención).
     */
    public Cliente obtenerORegistrar(String dni, String nombre) throws CocheraException {
        auth.requerirSesion();
        String dniNorm = Dni.validar(dni, "del cliente");
        Cliente existente = bd.buscarCliente(dniNorm);
        if (existente != null) {
            return existente;
        }
        Cliente nuevo = new Cliente(dniNorm, nombre, "", reloj.ahora());
        bd.insertarCliente(nuevo);
        return nuevo;
    }

    /** Actualizar: el DNI es la llave y no cambia; se corrigen nombre y teléfono. */
    public Cliente actualizar(String dni, String nombre, String telefono) throws CocheraException {
        auth.requerirAdmin();
        Cliente c = bd.buscarCliente(Dni.validar(dni, "del cliente"));
        if (c == null) {
            throw new ValidacionException("No existe un cliente con el DNI " + Dni.normalizar(dni) + ".");
        }
        if (Texto.vacio(nombre)) {
            throw new ValidacionException("Ingresa el nombre del cliente.");
        }
        c.actualizar(nombre, telefono);
        return c;
    }

    /** Eliminar: se rechaza si el cliente ya tiene ventas o alquileres (llave foránea). */
    public void eliminar(String dni) throws CocheraException {
        auth.requerirAdmin();
        Cliente c = bd.buscarCliente(Dni.validar(dni, "del cliente"));
        if (c == null) {
            throw new ValidacionException("No existe un cliente con el DNI " + Dni.normalizar(dni) + ".");
        }
        bd.eliminarCliente(c);
    }
}
