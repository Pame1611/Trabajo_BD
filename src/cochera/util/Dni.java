package cochera.util;

import cochera.excepcion.ValidacionException;

/**
 * Validación del DNI peruano: exactamente 8 dígitos.
 * Lo usan el inicio de sesión de los empleados y el registro de clientes.
 */
public final class Dni {

    public static final int LONGITUD = 8;

    private Dni() {
    }

    /** Quita espacios: " 12345678 " -> "12345678". */
    public static String normalizar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public static boolean esValido(String texto) {
        return normalizar(texto).matches("\\d{" + LONGITUD + "}");
    }

    /** Devuelve el DNI normalizado o lanza un mensaje claro para el usuario. */
    public static String validar(String texto, String quien) throws ValidacionException {
        String dni = normalizar(texto);
        if (dni.isEmpty()) {
            throw new ValidacionException("Ingresa el DNI " + quien + ".");
        }
        if (!esValido(dni)) {
            throw new ValidacionException("El DNI " + quien + " debe tener exactamente 8 dígitos.");
        }
        return dni;
    }
}
