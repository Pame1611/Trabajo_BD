/* =====================================================================
   Cochera Los Ángeles - Datos de prueba (ejecutar después de schema.sql)
   Son los mismos datos iniciales que carga la aplicación (DatosIniciales.java).
   Por consola:  sqlcmd -S localhost -E -i datos.sql
   ===================================================================== */

USE CocheraLosAngeles;
GO

/* Opciones necesarias para los índices filtrados (SSMS ya las usa; sqlcmd no). */
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO
SET NOCOUNT ON;

/* Usuarios de prueba: DNI / contraseña (se guarda el hash SHA-256).
   11111111 / admin123   22222222 / cajero123   33333333 / cajero123 */
INSERT INTO dbo.empleado (dni, nombre_completo, password_hash, rol) VALUES
('11111111', N'Administrador General', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('22222222', N'Carlos Ramírez',        '1ed4353e845e2e537e017c0fac3a0d402d231809b7989e90da15191c1148a93f', 'EMPLEADO'),
('33333333', N'Maria Torres',          '1ed4353e845e2e537e017c0fac3a0d402d231809b7989e90da15191c1148a93f', 'EMPLEADO');

SET IDENTITY_INSERT dbo.tipo_vehiculo ON;
INSERT INTO dbo.tipo_vehiculo (id, nombre, precio_hora, tarifa_mensual) VALUES
(1, N'Bicicleta', 1.00, 40.00),
(2, N'Triciclo', 1.50, 60.00),
(3, N'Moto Lineal', 1.50, 60.00),
(4, N'Carretilla', 1.50, 60.00),
(5, N'Mototaxi', 2.00, 80.00),
(6, N'Auto', 3.00, 200.00),
(7, N'Furgo Pequeño', 4.00, 250.00),
(8, N'Camioneta Cerrada y Abierta', 5.00, 300.00),
(9, N'Minivan Pequeña', 5.00, 300.00),
(10, N'Furgon Grande', 6.00, 350.00),
(11, N'Minivan Grande', 6.00, 350.00),
(12, N'Camion 1 Eje', 7.00, 400.00),
(13, N'Micro', 7.00, 400.00),
(14, N'Maquinaria Pequeña', 7.00, 400.00),
(15, N'Omnibus', 8.00, 450.00),
(16, N'Mini Bus', 8.00, 450.00),
(17, N'Camion 2 Ejes', 9.00, 500.00),
(18, N'Maquinaria Grande', 9.00, 500.00),
(19, N'Volquete', 9.00, 500.00),
(20, N'Omnibus 2 Pisos', 12.00, 650.00),
(21, N'Trailer Sin Carreta', 12.00, 650.00),
(22, N'Trailer Con Carreta', 15.00, 800.00);
SET IDENTITY_INSERT dbo.tipo_vehiculo OFF;

INSERT INTO dbo.marca (nombre) VALUES
(N'Desconocido'), (N'Toyota'), (N'Nissan'), (N'Hyundai'), (N'Kia'), (N'Chevrolet'),
(N'Honda'), (N'Bajaj'), (N'Mercedes-Benz'), (N'Volvo');

/* Cada marca tiene el modelo "Desconocido" (sin tipo: lo elige el empleado). */
INSERT INTO dbo.modelo (marca_id, nombre, tipo_vehiculo_id)
SELECT id, N'Desconocido', NULL FROM dbo.marca;

INSERT INTO dbo.modelo (marca_id, nombre, tipo_vehiculo_id)
SELECT m.id, x.modelo, x.tipo
FROM (VALUES
    (N'Toyota', N'Yaris', 6), (N'Toyota', N'Corolla', 6), (N'Toyota', N'Hilux', 8), (N'Toyota', N'Hiace', 11),
    (N'Nissan', N'Sentra', 6), (N'Nissan', N'Versa', 6), (N'Nissan', N'Frontier', 8),
    (N'Hyundai', N'Accent', 6), (N'Hyundai', N'Tucson', 8), (N'Hyundai', N'H-1', 9),
    (N'Kia', N'Rio', 6), (N'Kia', N'Picanto', 6), (N'Kia', N'Sportage', 8),
    (N'Chevrolet', N'Sail', 6), (N'Chevrolet', N'N300', 7),
    (N'Honda', N'CB190R', 3), (N'Honda', N'Wave 110', 3),
    (N'Bajaj', N'Torito', 5), (N'Bajaj', N'Pulsar NS200', 3),
    (N'Mercedes-Benz', N'Sprinter', 10), (N'Mercedes-Benz', N'Actros', 17),
    (N'Volvo', N'B12M', 15), (N'Volvo', N'FH', 22)
) AS x (marca, modelo, tipo)
JOIN dbo.marca m ON m.nombre = x.marca;

INSERT INTO dbo.metodo_pago (id, nombre) VALUES (1, 'Efectivo'), (2, 'Yape'), (3, 'Plin');

INSERT INTO dbo.cliente (dni, nombre_completo, telefono) VALUES
('45678912', N'Juan Pérez Rojas', '949123456'),
('47851236', N'Rosa Díaz Vega', NULL),
('41236987', N'Luis Gómez Paredes', '961457823'),
('48521473', N'Ana Castillo León', NULL),
('46587912', N'Pedro Salazar Ruiz', '987654321');

/* ── Movimientos de ejemplo ── */
DECLARE @ayer DATETIME2(0) = DATEADD(HOUR, 7, CAST(CAST(DATEADD(DAY, -1, SYSDATETIME()) AS DATE) AS DATETIME2(0)));
DECLARE @hoy DATE = CAST(SYSDATETIME() AS DATE);

INSERT INTO dbo.turno_caja (empleado_dni, fecha_apertura, fecha_cierre, estado, efectivo_declarado, diferencia)
VALUES ('33333333', @ayer, DATEADD(HOUR, 12, @ayer), 'CERRADO', 11.75, -1.00);
DECLARE @turno INT = SCOPE_IDENTITY();

/* Alquileres por hora ya cobrados (monto = minutos x tarifa por hora / 60). */
INSERT INTO dbo.alquiler (codigo_barras, placa, cliente_dni, empleado_dni, modelo_id, tipo_vehiculo_id,
                          fecha_entrada, fecha_salida, estado)
VALUES
('TK-2026-000001', 'T7Y274', '45678912', '33333333',
    (SELECT mo.id FROM dbo.modelo mo JOIN dbo.marca ma ON ma.id = mo.marca_id WHERE ma.nombre = N'Kia' AND mo.nombre = N'Rio'), 6,
    DATEADD(MINUTE, -190, DATEADD(HOUR, 4, @ayer)), DATEADD(HOUR, 4, @ayer), 'PAGADO'),
('TK-2026-000002', 'Z1I490', '47851236', '33333333',
    (SELECT mo.id FROM dbo.modelo mo JOIN dbo.marca ma ON ma.id = mo.marca_id WHERE ma.nombre = N'Bajaj' AND mo.nombre = N'Pulsar NS200'), 3,
    DATEADD(MINUTE, -130, DATEADD(HOUR, 8, @ayer)), DATEADD(HOUR, 8, @ayer), 'PAGADO');

INSERT INTO dbo.pago (alquiler_id, turno_id, metodo_pago_id, monto_total, fecha_pago)
SELECT a.id, @turno, 1, CAST(DATEDIFF(MINUTE, a.fecha_entrada, a.fecha_salida) * t.precio_hora / 60.0 AS DECIMAL(10, 2)), a.fecha_salida
FROM dbo.alquiler a JOIN dbo.tipo_vehiculo t ON t.id = a.tipo_vehiculo_id
WHERE a.estado = 'PAGADO';

INSERT INTO dbo.venta (cliente_dni, alquiler_id, pago_id, fecha, subtotal, descuento, total)
SELECT a.cliente_dni, a.id, p.id, p.fecha_pago, p.monto_total, 0, p.monto_total
FROM dbo.pago p JOIN dbo.alquiler a ON a.id = p.alquiler_id;

/* Vehículo dentro (por hora) y 2 autos de Pedro por mes con 10% de descuento cada uno. */
INSERT INTO dbo.alquiler (codigo_barras, placa, cliente_dni, empleado_dni, modelo_id, tipo_vehiculo_id,
                          tipo_alquiler, fecha_entrada, fecha_inicio, fecha_fin, descuento)
VALUES
('TK-2026-000003', 'ABC123', '48521473', '22222222',
    (SELECT mo.id FROM dbo.modelo mo JOIN dbo.marca ma ON ma.id = mo.marca_id WHERE ma.nombre = N'Toyota' AND mo.nombre = N'Yaris'), 6,
    'POR_HORA', DATEADD(MINUTE, -120, SYSDATETIME()), NULL, NULL, 0),
('TK-2026-000004', 'AMX111', '46587912', '22222222',
    (SELECT mo.id FROM dbo.modelo mo JOIN dbo.marca ma ON ma.id = mo.marca_id WHERE ma.nombre = N'Toyota' AND mo.nombre = N'Corolla'), 6,
    'MENSUAL', SYSDATETIME(), @hoy, DATEADD(DAY, 30, @hoy), 0.10),
('TK-2026-000005', 'AMX222', '46587912', '22222222',
    (SELECT mo.id FROM dbo.modelo mo JOIN dbo.marca ma ON ma.id = mo.marca_id WHERE ma.nombre = N'Kia' AND mo.nombre = N'Picanto'), 6,
    'MENSUAL', SYSDATETIME(), @hoy, DATEADD(DAY, 30, @hoy), 0.10);
GO

/* ── Consultas de ejemplo ──
   -- Ventas por cliente (JOIN):
   SELECT * FROM dbo.v_ventas_cliente WHERE dni = '45678912';
   -- Monto mensual por vehículo y total del cliente (2 autos de S/ 200.00 = S/ 360.00):
   SELECT a.placa, t.tarifa_mensual AS subtotal, t.tarifa_mensual * a.descuento AS descuento,
          t.tarifa_mensual * (1 - a.descuento) AS total
   FROM dbo.alquiler a JOIN dbo.tipo_vehiculo t ON t.id = a.tipo_vehiculo_id
   WHERE a.cliente_dni = '46587912' AND a.tipo_alquiler = 'MENSUAL';
   -- Transacciones de un empleado entre dos fechas:
   EXEC dbo.sp_consultar_transacciones '33333333', '2026-01-01', '2026-12-31';
*/
