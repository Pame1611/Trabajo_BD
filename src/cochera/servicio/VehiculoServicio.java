package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.CocheraException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Marca;
import cochera.modelo.Modelo;
import cochera.modelo.Tarifa;
import cochera.util.Texto;
import java.util.List;

/**
 * Catálogo de vehículos: marcas, modelos (cada modelo con su tipo) y tipos de vehículo.
 * Consultar lo puede cualquier empleado con sesión; agregar y eliminar es del administrador,
 * así se agregan opciones sin cambiar el programa.
 */
public class VehiculoServicio {

    private final BaseDatos bd;
    private final AutenticacionServicio auth;

    public VehiculoServicio(BaseDatos bd, AutenticacionServicio auth) {
        this.bd = bd;
        this.auth = auth;
    }

    public List<Marca> listarMarcas() {
        return bd.listarMarcas();
    }

    /** Modelos de la marca para el segundo combo ("Desconocido" primero). */
    public List<Modelo> modelosDe(Marca marca) {
        return bd.modelosDeMarca(marca);
    }

    /** Modelo por defecto: marca y modelo "Desconocido". */
    public Modelo modeloDesconocido() {
        Marca desconocida = bd.buscarMarcaPorNombre(Marca.DESCONOCIDO);
        return desconocida == null ? null : bd.modelosDeMarca(desconocida).get(0);
    }

    /** Agrega una marca nueva junto con su modelo "Desconocido". */
    public Marca agregarMarca(String nombre) throws CocheraException {
        auth.requerirAdmin();
        if (Texto.vacio(nombre)) {
            throw new ValidacionException("Escribe el nombre de la marca.");
        }
        Marca marca = new Marca(bd.siguienteIdMarca(), nombre);
        bd.insertarMarca(marca);
        bd.insertarModelo(new Modelo(bd.siguienteIdModelo(), Marca.DESCONOCIDO, marca, null));
        return marca;
    }

    /** Agrega un modelo a una marca; todo modelo conocido debe tener su tipo de vehículo. */
    public Modelo agregarModelo(Marca marca, String nombre, Tarifa tipo) throws CocheraException {
        auth.requerirAdmin();
        if (marca == null) {
            throw new ValidacionException("Selecciona la marca del modelo.");
        }
        if (Texto.vacio(nombre)) {
            throw new ValidacionException("Escribe el nombre del modelo.");
        }
        if (tipo == null) {
            throw new ValidacionException("Selecciona el tipo de vehículo del modelo.");
        }
        Modelo modelo = new Modelo(bd.siguienteIdModelo(), nombre, marca, tipo);
        bd.insertarModelo(modelo);
        return modelo;
    }

    public void eliminarModelo(Modelo modelo) throws CocheraException {
        auth.requerirAdmin();
        if (modelo == null) {
            throw new ValidacionException("Selecciona un modelo.");
        }
        if (modelo.esDesconocido()) {
            throw new ValidacionException("El modelo \"Desconocido\" no se puede eliminar.");
        }
        bd.eliminarModelo(modelo);
    }

    public void eliminarMarca(Marca marca) throws CocheraException {
        auth.requerirAdmin();
        if (marca == null) {
            throw new ValidacionException("Selecciona una marca.");
        }
        if (marca.esDesconocida()) {
            throw new ValidacionException("La marca \"Desconocido\" no se puede eliminar.");
        }
        bd.eliminarMarca(marca);
    }

    /** RF-TAR-04: agrega un tipo de vehículo nuevo con su tarifa por hora (sin tarifa mensual). */
    public Tarifa agregarTipoVehiculo(String nombre, double precioHora) throws CocheraException {
        return agregarTipoVehiculo(nombre, precioHora, 0);
    }

    /** Agrega un tipo de vehículo nuevo con su tarifa por hora y su tarifa mensual. */
    public Tarifa agregarTipoVehiculo(String nombre, double precioHora, double tarifaMensual) throws CocheraException {
        auth.requerirAdmin();
        if (Texto.vacio(nombre)) {
            throw new ValidacionException("Escribe el nombre del tipo de vehículo.");
        }
        Tarifa nueva;
        try {
            nueva = new Tarifa(bd.siguienteIdTarifa(), nombre.trim(), precioHora, tarifaMensual);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException(e.getMessage());
        }
        bd.insertarTarifa(nueva);
        return nueva;
    }
}
