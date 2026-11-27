# Documento de pruebas manuales – Cochera Los Ángeles (v2.0)

| Dato | Valor |
| :--- | :--- |
| Versión probada | v2.0 (ramas `feature/cliente-venta`, `feature/marca-modelo` y `feature/alquiler-mensual`) |
| Fecha de ejecución | 27/11/2026 |
| Entorno | Windows 11, JDK de NetBeans (compilado con `--release 8`), versión con ventanas |
| Datos | Datos de demostración que carga el sistema al iniciar ([README](../README.md#4-usuarios-y-datos-de-prueba)) |
| Resultado | **28 de 28 casos conformes** |

Cada caso indica los pasos en la aplicación, lo que debe pasar y lo que pasó al ejecutarlo. Para los casos que dependen del tiempo se usa **Demo → Adelantar reloj**. Las capturas de referencia están en [`docs/capturas`](capturas).

## 1. Inicio de sesión con DNI

| N.º | Caso (requerimiento) | Pasos | Resultado esperado | Resultado obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PM-01 | Ingreso correcto con DNI (RF-AUT-01, RF-AUT-02) | 1. Escribir DNI `22222222` y presionar Enter. 2. Escribir `cajero123`. 3. Ingresar. | Aparece el nombre del empleado y se abre la ventana de Caja. | Muestra "Hola, Carlos Ramírez. Escribe tu contraseña." y abre la ventana de Cajero. | Conforme |
| PM-02 | DNI con formato inválido (RF-AUT-04) | DNI `2222`, contraseña `cajero123`, Ingresar. | No entra y muestra que el DNI debe tener 8 dígitos. | Mensaje: "El DNI del empleado debe tener exactamente 8 dígitos." | Conforme |
| PM-03 | Contraseña incorrecta (RF-AUT-04) | DNI `22222222`, contraseña `malaclave`, Ingresar. | No entra y muestra el motivo. | Mensaje: "DNI o contraseña incorrectos." | Conforme |
| PM-04 | Campos vacíos (RF-AUT-04) | Dejar DNI y contraseña vacíos, Ingresar. | No entra y pide completar los campos. | Mensaje: "Completa el DNI y la contraseña." | Conforme |
| PM-05 | Botón Cancelar (RF-AUT-03) | Escribir un DNI y presionar Cancelar. | Se limpian los campos. | DNI y contraseña quedan en blanco. | Conforme |
| PM-28 | Opciones según el tipo de usuario (RF-AUT-05) | Ingresar como cajero y revisar las pestañas. | El cajero no ve las opciones de mantenimiento. | Pestañas del cajero: Turno de caja, Check-in, Check-out, Autos dentro, Mis cobros, Alquiler mensual (sin Tarifario, Usuarios, Clientes ni Marcas y modelos). | Conforme |

## 2. Entrada de vehículos, cliente, marca y modelo

| N.º | Caso (requerimiento) | Pasos | Resultado esperado | Resultado obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PM-06 | Entrada sin turno abierto (RF-ING-06) | Como cajero sin turno, registrar una entrada. | Se rechaza y pide abrir el turno. | Mensaje: "No tienes un turno abierto. Abre tu turno desde el Inicio antes de registrar entradas." | Conforme |
| PM-07 | Cliente nuevo en la entrada (RF-ING-05, RP-01) | Abrir turno. En Check-in escribir DNI `72345678`, nombre "Lucía Ramos Vega", placa `KRV-789`, tipo Auto, Registrar. | Avisa que es cliente nuevo, lo registra y crea el alquiler. | Aviso "Cliente nuevo: se registrará con este DNI."; se registra 72345678 - Lucía Ramos Vega y el alquiler TK-2026-000011. | Conforme |
| PM-08 | Cliente existente (RF-ING-05) | Escribir DNI `47851236`. | Muestra el nombre del cliente registrado. | Aviso "Cliente registrado: Rosa Díaz Vega". | Conforme |
| PM-09 | Combo de modelos filtrado por marca (RF-ING-02, RP-02) | Elegir la marca Toyota. | El combo Modelo muestra solo los modelos de Toyota. | Combo Modelo: Desconocido, Corolla, Hiace, Hilux, Yaris. | Conforme |
| PM-10 | Marca y modelo por defecto (RF-ING-03) | Dejar marca y modelo sin cambiar y registrar la placa `DSC-100`. | Queda "Desconocido". | Valores por defecto Desconocido / Desconocido; el alquiler TK-2026-000012 queda con vehículo "Desconocido". | Conforme |
| PM-11 | El modelo define el tipo y su precio (RF-ING-04, RP-02) | Elegir Toyota → Hilux. | Se selecciona el tipo Camioneta y se muestra su precio. | "Camioneta Cerrada y Abierta: S/ 5.00 por hora · se cobra por minuto (S/ 5.00 / 60)". | Conforme |
| PM-12 | Placa que ya está dentro (RF-ING-10) | Registrar la placa `ABC-123` (ya está dentro). | Se rechaza e indica el alquiler activo. | Mensaje: "Esta placa ya tiene un alquiler activo (TK-2026-000005). Realiza la salida primero." | Conforme |

## 3. Cobro de salida y ventas

| N.º | Caso (requerimiento) | Pasos | Resultado esperado | Resultado obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PM-13 | Cálculo por minuto con `calcularMonto()` (HU-09, RF-COB-05) | Registrar un Auto (`AUT-600`) y una Camioneta Toyota Hilux (`CAM-900`). Demo → adelantar 60 min y buscar AUT-600; adelantar 30 min más y buscar CAM-900. | Auto a S/ 3.00 por hora con 60 min = S/ 3.00; Camioneta a S/ 5.00 por hora con 90 min = S/ 7.50. | Auto 60 min: S/ 3.00; Camioneta 90 min: S/ 7.50 ("90 min x S/ 5.00 / 60 = S/ 7.50"). | Conforme |
| PM-14 | Búsqueda por DNI del cliente (RF-COB-01) | En Check-out escribir `48521473` y Enter. | Carga el alquiler en curso del cliente. | Se carga el ticket TK-2026-000005 - ABC-123 · Ana Castillo León (DNI 48521473). | Conforme |
| PM-15 | Cobro en efectivo con vuelto (HU-10, RF-COB-07) | Buscar CAM-900 (S/ 7.50), Efectivo, recibido `10`, Cobrar. | Vuelto S/ 2.50; el alquiler pasa a PAGADO. | Pantalla "Vuelto: S/ 2.50"; cobro registrado con vuelto S/ 2.50; el alquiler pasa a PAGADO. | Conforme |
| PM-16 | Monto recibido insuficiente (RF-COB-07) | Cobrar AUT-600 (S/ 4.50) en efectivo recibiendo `1`. | No se registra el cobro y se indica el motivo. | Mensaje: "El monto recibido (S/ 1.00) es menor al total a cobrar (S/ 4.50)." | Conforme |
| PM-17 | Venta vinculada al cliente (RP-01, JOIN cliente–venta) | Como administrador, en Clientes elegir a Juan (`45678912`). | Se listan todas sus ventas con fecha, placa y monto. | Ventas de Juan: 3 filas, la más reciente CAM-900 S/ 7.50; pie "Juan Pérez Rojas: 3 venta(s) · Total S/ 62.83". | Conforme |
| PM-18 | No eliminar cliente con ventas (RP-01) | Elegir a Juan y presionar Eliminar cliente. | Se rechaza por la llave foránea. | Mensaje: "No se puede eliminar a Juan Pérez Rojas: tiene 3 venta(s) registrada(s)." | Conforme |
| PM-19 | CRUD de clientes | Nuevo cliente `70000001` "Cliente Temporal" → Guardar; elegirlo, cambiar el nombre a "Cliente Corregido" → Guardar; Eliminar cliente. | Se crea, se actualiza y se elimina (no tiene ventas). | Cliente 70000001 creado, actualizado ("Cliente Corregido") y eliminado; ya no aparece en la lista. | Conforme |

## 4. Alquiler mensual con 10% de descuento (RP-03)

| N.º | Caso (requerimiento) | Pasos | Resultado esperado | Resultado obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PM-20 | Monto mensual con descuento | Check-in con DNI `72345678`, placa `MEN-200`, Kia Rio (Auto), modalidad **Por mes**, Registrar. | Tarifa S/ 200.00 → paga S/ 180.00; se guardan inicio y fin (30 días). | Alquiler TK-2026-000015 del 27/11/2026 al 27/12/2026: subtotal S/ 200.00, descuento S/ 20.00, total S/ 180.00. | Conforme |
| PM-21 | 10% por cada vehículo | Como administrador, en Alquiler mensual buscar el DNI `46587912` (Pedro, 2 autos por mes). | 2 autos de S/ 200.00 pagan S/ 360.00. | Pedro: 2 vehículos, subtotal S/ 400.00, descuento S/ 40.00; total en pantalla S/ 360.00. | Conforme |
| PM-22 | Comprobante con subtotal, descuento y total | Cobrar la salida de `MEN-200` con Yape. | El ticket de salida muestra subtotal, descuento y total. | Ticket: "Subtotal: S/ 200.00 \| Desc. 10%: -S/ 20.00 \| TOTAL: S/ 180.00". | Conforme |
| PM-23 | El cobro por hora no cambia | Consultar AUT-600 (Auto por hora, 90 min). | Sin descuento. | Subtotal S/ 4.50, descuento S/ 0.00, total S/ 4.50. | Conforme |

## 5. Mantenimiento (administrador)

| N.º | Caso (requerimiento) | Pasos | Resultado esperado | Resultado obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PM-24 | Agregar marca y modelo (RF-MAN-06) | En Marcas y modelos agregar la marca "Suzuki" y el modelo "Swift" de tipo Auto. | La marca nace con su modelo "Desconocido" y se agrega Swift. | Marca Suzuki con modelos [Desconocido, Swift]; Suzuki Swift (Auto). | Conforme |
| PM-25 | No eliminar un modelo usado (llave foránea) | Eliminar el modelo Toyota Hilux. | Se rechaza porque ya se usó en un alquiler. | Mensaje: "No se puede eliminar Toyota Hilux: ya se usó en el alquiler TK-2026-000006." | Conforme |
| PM-26 | Agregar tipo de vehículo (RF-TAR-04) | En Tarifario agregar "Cuatrimoto", S/ 2.50 por hora y S/ 120.00 al mes. | El tipo nuevo aparece en el tarifario y en el combo del Check-in. | Tipo Cuatrimoto (S/ 2.50 por hora) con id 23; disponible en Check-in. | Conforme |
| PM-27 | Precio negativo (RF-TAR-03) | Poner -3 como tarifa por hora del Auto y Guardar. | Se rechaza. | Mensaje: "Los precios no pueden ser negativos." | Conforme |

## 6. Pruebas automáticas complementarias

Además de los casos manuales, las clases del paquete `cochera.prueba` se ejecutaron sobre la misma versión:

| Clase | Qué prueba | Resultado |
| :--- | :--- | :--- |
| `PruebaCalculoTarifa` | `calcularMonto()` por minuto, ejemplos de HU-09 y redondeo | 14 pasan, 0 fallan |
| `PruebaFlujoOperativo` | Login con DNI, turnos, entrada con cliente, traslape de turnos, vuelto, ventas, arqueo, concurrencia, reportes, CRUD de clientes | 54 pasan, 0 fallan |
| `PruebaMarcaModelo` | Combos de marca/modelo, "Desconocido", llaves foráneas, tipos nuevos | 20 pasan, 0 fallan |
| `PruebaAlquilerMensual` | 10% por vehículo, polimorfismo de `calcularMonto()`, alertas, ticket con descuento | 22 pasan, 0 fallan |
