# Manual de usuario – Cochera Los Ángeles (v2.0)

Guía corta para usar el sistema. Hay dos perfiles: **Cajero (vendedor)**, que registra entradas y cobra salidas, y **Administrador**, que revisa reportes y mantiene los datos. Cómo instalar y ejecutar el programa está en el [README](../README.md).

## 1. Iniciar sesión

1. Escribe tu **DNI** (8 dígitos) y presiona **Enter**: aparece tu nombre.
2. Escribe tu **contraseña** (se ve oculta) y presiona **Ingresar**.
3. **Cancelar** limpia los campos. Si falta un dato o es incorrecto, el motivo aparece en rojo y no se entra.

Usuarios de prueba: `11111111 / admin123` (administrador), `22222222 / cajero123` y `33333333 / cajero123` (cajeros).

![Inicio de sesión](capturas/01_login.png)

## 2. Cajero

La ventana del cajero muestra arriba su nombre, el estado del turno y los botones **Impresora**, **Demo** y **Cerrar sesión**.

### 2.1 Abrir el turno

En **Turno de caja** presiona **Abrir turno**. Sin turno abierto no se puede registrar ni cobrar. Solo se permite un turno abierto por cajero.

![Turno de caja](capturas/02_turno_de_caja.png)

### 2.2 Registrar una entrada (Check-in)

1. Escribe el **DNI del cliente** y presiona Enter. Si ya existe, aparece su nombre en verde; si es nuevo, escribe su nombre (opcional) y se registrará al guardar.
2. Escribe la **placa** (se da formato solo: `ABC-123`).
3. Elige la **marca** y luego el **modelo** (el combo solo muestra los modelos de esa marca). Si no se conocen, deja **Desconocido**.
4. El **tipo de vehículo** se elige solo según el modelo; si el modelo es Desconocido, elígelo tú. Debajo se ve su precio.
5. Elige la **modalidad**: *Por hora* (se cobra por minuto) o *Por mes* (10% de descuento).
6. Presiona **Registrar entrada e imprimir ticket** (o Enter en la placa).

![Entrada por hora](capturas/03_check_in.png)

Ticket de ingreso que se entrega al cliente:

![Ticket de ingreso](capturas/04_ticket_de_ingreso.png)

**Alquiler por mes.** Con la modalidad *Por mes* se muestra la tarifa mensual con el 10% de descuento y las fechas de inicio y fin (30 días). El ticket indica el subtotal, el descuento y el total del mes.

![Entrada por mes](capturas/20_check_in_mensual.png)

![Ticket de ingreso mensual](capturas/21_ticket_ingreso_mensual.png)

### 2.3 Cobrar una salida (Check-out)

1. Escanea el código del ticket (`TK-…`), escribe la **placa** o el **DNI del cliente** y presiona Enter. Con el DNI, si el cliente tiene varios vehículos dentro, el sistema pregunta cuál sale.
2. Revisa el detalle: tiempo, precio por hora y cálculo (`minutos × tarifa / 60`).
3. Elige el método de pago. En **Efectivo** escribe el monto recibido y se muestra el vuelto (si no alcanza, no se puede cobrar).
4. Presiona **Cobrar** y confirma. Se imprime el ticket de salida y se registra la venta del cliente.

![Cobro por hora](capturas/05_check_out.png)

![Ticket de salida](capturas/06_ticket_de_salida.png)

En un alquiler **por mes** el detalle muestra la tarifa mensual, el periodo y el descuento del 10%:

![Cobro mensual](capturas/22_check_out_mensual.png)

![Ticket de salida mensual](capturas/23_ticket_salida_mensual.png)

Búsqueda por DNI del cliente:

![Cobro por DNI](capturas/24_check_out_por_dni.png)

### 2.4 Autos dentro

Lista en vivo (se actualiza cada 5 segundos) con el tiempo, lo que pagaría ahora y la alerta: **OK** (menos de 10 h), **POR VENCER** (10 a 12 h), **+12H**; en los alquileres por mes, **MENSUAL** o **POR VENCER (MES)** en los últimos 3 días. Se puede filtrar por placa o tipo, y con **Cobrar salida del seleccionado** se pasa directo al cobro.

![Autos dentro](capturas/07_autos_dentro.png)

### 2.5 Mis cobros

Cobros del turno con sus totales por método de pago. Permite filtrar por placa, reimprimir un ticket (sin duplicar el cobro) y corregir el método de pago mientras el turno siga abierto.

![Mis cobros](capturas/08_mis_cobros.png)

### 2.6 Cerrar el turno (arqueo)

En **Turno de caja** cuenta el efectivo y escríbelo: se muestra al momento la diferencia con el efectivo del sistema (Yape y Plin no entran). Presiona **Cerrar turno** para guardar.

![Arqueo de caja](capturas/09_arqueo_de_caja.png)

## 3. Administrador

| Pestaña | Para qué sirve |
| :--- | :--- |
| Resumen del día | Autos dentro, ingresos, cobros por hora y por tipo, y últimos cobros. |
| Cierre 2 PM | Corte de 24 h (14:00 del día anterior a 13:59 del día elegido), con exportación a CSV. |
| Turnos cerrados / Pagos | Auditoría por fechas, corrección del método de pago y anulación de alquileres. |
| Tarifario | Tarifa por hora y mensual de cada tipo; **agregar tipos de vehículo** nuevos. |
| Exportar a Excel | Reporte de ventas en CSV con filtros de fecha, tipo y empleado. |
| Usuarios | Crear cajeros con DNI y contraseña. |
| Clientes | Crear, consultar, actualizar y eliminar clientes, y ver sus ventas. |
| Marcas y modelos | Agregar o eliminar marcas y modelos (cada modelo con su tipo). |
| Alquiler mensual | Alquileres por mes de un cliente con subtotal, descuento y total. |

![Resumen del día](capturas/10_admin_resumen.png)

![Cierre 2 PM](capturas/11_admin_cierre_2pm.png)

### 3.1 Tarifario y tipos de vehículo

Selecciona un tipo en la tabla para cambiar su tarifa por hora o mensual. Para un tipo nuevo, escribe su nombre y tarifas y presiona **Agregar tipo**; aparece de inmediato en el Check-in. No se aceptan precios negativos.

![Tarifario](capturas/14_admin_tarifario.png)

### 3.2 Clientes y sus ventas

Al elegir un cliente se cargan sus datos para editarlos y abajo se listan sus ventas (fecha, código, placa, método y monto). **Nuevo cliente** limpia el formulario. El DNI no se puede cambiar y no se puede eliminar un cliente que ya tiene ventas o alquileres.

![Clientes](capturas/18_admin_clientes.png)

### 3.3 Marcas y modelos

Elige una marca para ver sus modelos. Para agregar un modelo escribe su nombre y elige su tipo de vehículo. Cada marca tiene el modelo "Desconocido". No se puede eliminar una marca o modelo que ya se usó en un alquiler.

![Marcas y modelos](capturas/19_admin_marcas_modelos.png)

### 3.4 Alquiler mensual

Escribe el DNI del cliente y presiona **Buscar** (o **Ver todos**). Abajo se ve el resumen: cada vehículo tiene su 10% de descuento; por ejemplo, 2 autos de S/ 200.00 pagan S/ 360.00.

![Alquiler mensual](capturas/25_alquiler_mensual.png)

### 3.5 Otras pantallas

![Turnos cerrados](capturas/12_admin_turnos.png)

![Pagos](capturas/13_admin_pagos.png)

![Exportar a Excel](capturas/15_admin_exportar.png)

![Usuarios](capturas/16_admin_usuarios.png)

## 4. Herramientas de demostración

Desde el login o con el botón **Demo**: adelantar el reloj (para ver cobros largos sin esperar), elegir dónde sale el ticket (ventana o archivo `.txt`) y ver los usuarios de prueba.

![Herramientas de demostración](capturas/17_herramientas_demo.png)

> Los datos se guardan en memoria: al cerrar el programa vuelven los datos de demostración.
