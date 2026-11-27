package cochera.prueba;

import cochera.ContextoAplicacion;
import cochera.datos.DatosIniciales;
import cochera.excepcion.AccesoDenegadoException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Alquiler;
import cochera.modelo.Marca;
import cochera.modelo.Modelo;
import cochera.modelo.Tarifa;
import cochera.servicio.VehiculoServicio;
import java.util.List;

/**
 * RP-02: marca y modelo del vehículo, y tipos de vehículo nuevos (RF-ING-02, RF-ING-03, RF-MAN-06, RF-TAR-04).
 * Ejecutar: java -cp bin cochera.prueba.PruebaMarcaModelo
 */
public class PruebaMarcaModelo {

    public static void main(String[] args) throws Exception {
        final Verificador v = new Verificador();
        final ContextoAplicacion ctx = new ContextoAplicacion(false);
        final VehiculoServicio veh = ctx.getVehiculos();

        System.out.println("=== Combos de marca y modelo ===");
        List<Marca> marcas = veh.listarMarcas();
        v.verdadero(marcas.get(0).esDesconocida(), "La primera marca del combo es \"Desconocido\"");
        Marca toyota = ctx.getBaseDatos().buscarMarcaPorNombre("Toyota");
        List<Modelo> modelosToyota = veh.modelosDe(toyota);
        boolean soloToyota = true;
        for (Modelo m : modelosToyota) {
            soloToyota &= m.getMarca() == toyota;
        }
        v.verdadero(soloToyota && modelosToyota.size() == 5, "Al elegir Toyota solo aparecen sus 5 modelos");
        v.verdadero(modelosToyota.get(0).esDesconocido(), "Cada marca ofrece primero el modelo \"Desconocido\"");
        Modelo hilux = null;
        for (Modelo m : modelosToyota) {
            if (m.getNombre().equals("Hilux")) {
                hilux = m;
            }
        }
        v.verdadero(hilux != null && hilux.getTipoVehiculo().getTipoVehiculo().startsWith("Camioneta"),
                "Cada modelo tiene su tipo de vehiculo (Hilux = Camioneta)");

        System.out.println("=== Entrada con marca y modelo ===");
        ctx.getAuth().validarAcceso(DatosIniciales.DNI_CAJERO1, DatosIniciales.CLAVE_DEMO_CAJERO);
        ctx.getTurnos().abrirTurno();
        Alquiler conModelo = ctx.getCheckin().registrarEntrada("45678912", "Juan", "HLX111", hilux.getId(), 0);
        v.verdadero(conModelo.getModelo() == hilux && conModelo.getTarifa() == hilux.getTipoVehiculo(),
                "Sin tipo elegido se usa el tipo del modelo (Camioneta)");
        v.verdadero(conModelo.getDescripcionVehiculo().equals("Toyota Hilux"), "El ticket muestra \"Toyota Hilux\"");
        Alquiler sinModelo = ctx.getCheckin().registrarEntrada("45678912", "", "DSC222", 0, 6);
        v.verdadero(sinModelo.getModelo().esDesconocido() && sinModelo.getDescripcionVehiculo().equals("Desconocido"),
                "Sin marca ni modelo queda \"Desconocido\"");
        v.lanza(ValidacionException.class, () -> ctx.getCheckin().registrarEntrada("45678912", "", "DSC333", 0, 0),
                "Con modelo Desconocido el tipo de vehiculo es obligatorio");
        v.lanza(AccesoDenegadoException.class, () -> veh.agregarMarca("Suzuki"), "El cajero no agrega marcas");
        ctx.getAuth().cerrarSesion();

        System.out.println("=== Mantenimiento del administrador ===");
        ctx.getAuth().validarAcceso(DatosIniciales.DNI_ADMIN, DatosIniciales.CLAVE_DEMO_ADMIN);
        Marca suzuki = veh.agregarMarca("Suzuki");
        v.verdadero(veh.modelosDe(suzuki).size() == 1 && veh.modelosDe(suzuki).get(0).esDesconocido(),
                "Una marca nueva nace con su modelo \"Desconocido\"");
        v.lanza(ValidacionException.class, () -> veh.agregarMarca("toyota"), "No se repite una marca");
        Tarifa auto = ctx.getBaseDatos().buscarTarifa(6);
        Modelo swift = veh.agregarModelo(suzuki, "Swift", auto);
        v.verdadero(swift.getMarca() == suzuki && swift.getTipoVehiculo() == auto, "Modelo Swift agregado a Suzuki");
        v.lanza(ValidacionException.class, () -> veh.agregarModelo(suzuki, "Vitara", null),
                "Un modelo necesita su tipo de vehiculo");
        final Modelo hiluxFinal = hilux;
        v.lanza(ValidacionException.class, () -> veh.eliminarModelo(hiluxFinal),
                "No se elimina un modelo usado en un alquiler (llave foranea)");
        v.lanza(ValidacionException.class, () -> veh.eliminarMarca(toyota),
                "No se elimina una marca con modelos usados");
        veh.eliminarModelo(swift);
        v.verdadero(veh.modelosDe(suzuki).size() == 1, "Se elimina un modelo sin alquileres");
        veh.eliminarMarca(suzuki);
        v.verdadero(ctx.getBaseDatos().buscarMarcaPorNombre("Suzuki") == null, "Se elimina una marca sin alquileres");

        System.out.println("=== Agregar tipos de vehiculo ===");
        int antes = ctx.getBaseDatos().listarTarifas().size();
        Tarifa cuatrimoto = veh.agregarTipoVehiculo("Cuatrimoto", 2.50);
        v.verdadero(ctx.getBaseDatos().listarTarifas().size() == antes + 1 && cuatrimoto.getId() == antes + 1,
                "Tipo nuevo \"Cuatrimoto\" agregado con id " + cuatrimoto.getId());
        v.lanza(ValidacionException.class, () -> veh.agregarTipoVehiculo("Patineta", -1), "Precio negativo rechazado");
        v.lanza(ValidacionException.class, () -> veh.agregarTipoVehiculo("auto", 3), "No se repite un tipo");

        v.resumen();
        System.exit(v.getFallos() == 0 ? 0 : 1);
    }
}
