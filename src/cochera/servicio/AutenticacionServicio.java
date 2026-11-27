package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.AccesoDenegadoException;
import cochera.excepcion.CocheraException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Administrador;
import cochera.modelo.Empleado;
import cochera.modelo.Usuario;
import cochera.util.Dni;
import cochera.util.Reloj;
import java.util.List;

/**
 * Inicio/cierre de sesión con DNI y contraseña, y control de acceso por rol.
 */
public class AutenticacionServicio {

    public static final int LONGITUD_MINIMA_CLAVE = 6;

    private final BaseDatos bd;
    private final Reloj reloj;
    private Usuario usuarioActual;

    public AutenticacionServicio(BaseDatos bd, Reloj reloj) {
        this.bd = bd;
        this.reloj = reloj;
    }

    /**
     * ValidarAcceso: inicia sesión con el DNI y la contraseña del empleado.
     * Rechaza campos vacíos, un DNI mal escrito o datos incorrectos, indicando el motivo.
     */
    public Usuario validarAcceso(String dni, String password) throws CocheraException {
        if (Dni.normalizar(dni).isEmpty() || password == null || password.isEmpty()) {
            throw new ValidacionException("Completa el DNI y la contraseña.");
        }
        String dniNorm = Dni.validar(dni, "del empleado");
        Usuario u = bd.buscarUsuarioPorDni(dniNorm);
        if (u == null || !u.verificarPassword(password)) {
            throw new AccesoDenegadoException("DNI o contraseña incorrectos.");
        }
        usuarioActual = u;
        return u;
    }

    /** Al escribir el DNI y presionar Enter, el login muestra el nombre del empleado. */
    public String nombrePorDni(String dni) throws ValidacionException {
        String dniNorm = Dni.validar(dni, "del empleado");
        Usuario u = bd.buscarUsuarioPorDni(dniNorm);
        if (u == null) {
            throw new ValidacionException("No hay ningún empleado con el DNI " + dniNorm + ".");
        }
        return u.getNombreCompleto();
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public boolean haySesion() {
        return usuarioActual != null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Usuario requerirSesion() throws AccesoDenegadoException {
        if (usuarioActual == null) {
            throw new AccesoDenegadoException("Sin sesión activa.");
        }
        return usuarioActual;
    }

    public Administrador requerirAdmin() throws AccesoDenegadoException {
        Usuario u = requerirSesion();
        if (!(u instanceof Administrador)) {
            throw new AccesoDenegadoException("Acceso denegado. Solo administradores.");
        }
        return (Administrador) u;
    }

    public Empleado requerirEmpleado(String mensaje) throws AccesoDenegadoException {
        Usuario u = requerirSesion();
        if (!(u instanceof Empleado)) {
            throw new AccesoDenegadoException(mensaje);
        }
        return (Empleado) u;
    }

    /**
     * Alta de un nuevo cajero (solo ADMIN). No hay registro público: solo el administrador crea cuentas.
     * El DNI debe tener 8 dígitos y no repetirse; la contraseña, al menos 6 caracteres.
     */
    public Empleado registrarEmpleado(String dni, String password, String nombreCompleto) throws CocheraException {
        requerirAdmin();
        String dniNorm = Dni.validar(dni, "del empleado");
        if (password == null || password.length() < LONGITUD_MINIMA_CLAVE) {
            throw new ValidacionException("La contraseña debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres.");
        }
        Empleado nuevo = new Empleado(dniNorm, password, nombreCompleto, reloj.ahora());
        bd.insertarUsuario(nuevo);
        return nuevo;
    }

    public List<Usuario> listarUsuarios() throws CocheraException {
        requerirAdmin();
        return bd.listarUsuarios();
    }
}
