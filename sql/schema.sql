/* =====================================================================
   Cochera Los Ángeles - Esquema de base de datos (SQL Server 2019 o superior)
   Crea la base CocheraLosAngeles y sus tablas relacionadas.

   Ejecutar en SQL Server Management Studio (SSMS): abrir este archivo y
   presionar Execute (F5). Luego ejecutar datos.sql.
   Por consola:  sqlcmd -S localhost -E -i schema.sql
   ===================================================================== */

IF DB_ID(N'CocheraLosAngeles') IS NULL
    CREATE DATABASE CocheraLosAngeles;
GO

USE CocheraLosAngeles;
GO

/* Opciones necesarias para los índices filtrados (SSMS ya las usa; sqlcmd no). */
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* Se borran en orden inverso a las llaves foráneas para poder volver a ejecutar el script. */
IF OBJECT_ID(N'dbo.v_ventas_cliente', N'V') IS NOT NULL DROP VIEW dbo.v_ventas_cliente;
IF OBJECT_ID(N'dbo.sp_consultar_transacciones', N'P') IS NOT NULL DROP PROCEDURE dbo.sp_consultar_transacciones;
IF OBJECT_ID(N'dbo.venta', N'U') IS NOT NULL DROP TABLE dbo.venta;
IF OBJECT_ID(N'dbo.pago', N'U') IS NOT NULL DROP TABLE dbo.pago;
IF OBJECT_ID(N'dbo.alquiler', N'U') IS NOT NULL DROP TABLE dbo.alquiler;
IF OBJECT_ID(N'dbo.turno_caja', N'U') IS NOT NULL DROP TABLE dbo.turno_caja;
IF OBJECT_ID(N'dbo.metodo_pago', N'U') IS NOT NULL DROP TABLE dbo.metodo_pago;
IF OBJECT_ID(N'dbo.cliente', N'U') IS NOT NULL DROP TABLE dbo.cliente;
IF OBJECT_ID(N'dbo.modelo', N'U') IS NOT NULL DROP TABLE dbo.modelo;
IF OBJECT_ID(N'dbo.marca', N'U') IS NOT NULL DROP TABLE dbo.marca;
IF OBJECT_ID(N'dbo.tipo_vehiculo', N'U') IS NOT NULL DROP TABLE dbo.tipo_vehiculo;
IF OBJECT_ID(N'dbo.empleado', N'U') IS NOT NULL DROP TABLE dbo.empleado;
GO

/* Empleados del sistema: el administrador y los vendedores (cajeros) inician sesión con su DNI. */
CREATE TABLE dbo.empleado (
    dni             CHAR(8)        NOT NULL CONSTRAINT pk_empleado PRIMARY KEY,
    nombre_completo NVARCHAR(100)  NOT NULL,
    password_hash   CHAR(64)       NOT NULL,              -- SHA-256 en hexadecimal
    rol             VARCHAR(10)    NOT NULL CONSTRAINT ck_empleado_rol CHECK (rol IN ('ADMIN', 'EMPLEADO')),
    activo          BIT            NOT NULL CONSTRAINT df_empleado_activo DEFAULT 1,
    creado_en       DATETIME2(0)   NOT NULL CONSTRAINT df_empleado_creado DEFAULT SYSDATETIME(),
    CONSTRAINT ck_empleado_dni CHECK (dni LIKE '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')
);

/* Tarifario: tarifa por hora (se cobra por minuto = tarifa / 60) y tarifa mensual. */
CREATE TABLE dbo.tipo_vehiculo (
    id              INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_tipo_vehiculo PRIMARY KEY,
    nombre          NVARCHAR(50)   NOT NULL CONSTRAINT uq_tipo_vehiculo UNIQUE,
    precio_hora     DECIMAL(10, 2) NOT NULL CONSTRAINT ck_tipo_precio CHECK (precio_hora >= 0),
    tarifa_mensual  DECIMAL(10, 2) NOT NULL CONSTRAINT df_tipo_mensual DEFAULT 0
                                   CONSTRAINT ck_tipo_mensual CHECK (tarifa_mensual >= 0)
);

/* RP-02: cada marca tiene varios modelos y cada modelo tiene su tipo de vehículo. */
CREATE TABLE dbo.marca (
    id     INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_marca PRIMARY KEY,
    nombre NVARCHAR(50) NOT NULL CONSTRAINT uq_marca UNIQUE
);

CREATE TABLE dbo.modelo (
    id               INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_modelo PRIMARY KEY,
    marca_id         INT NOT NULL CONSTRAINT fk_modelo_marca REFERENCES dbo.marca (id) ON DELETE CASCADE,
    nombre           NVARCHAR(50) NOT NULL,
    tipo_vehiculo_id INT NULL CONSTRAINT fk_modelo_tipo REFERENCES dbo.tipo_vehiculo (id),  -- NULL = "Desconocido"
    CONSTRAINT uq_modelo UNIQUE (marca_id, nombre)
);

/* RP-01: el cliente se identifica por su DNI (solo los datos necesarios, Ley 29733). */
CREATE TABLE dbo.cliente (
    dni             CHAR(8)       NOT NULL CONSTRAINT pk_cliente PRIMARY KEY,
    nombre_completo NVARCHAR(100) NOT NULL,
    telefono        VARCHAR(15)   NULL,
    creado_en       DATETIME2(0)  NOT NULL CONSTRAINT df_cliente_creado DEFAULT SYSDATETIME(),
    CONSTRAINT ck_cliente_dni CHECK (dni LIKE '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')
);

CREATE TABLE dbo.metodo_pago (
    id     INT NOT NULL CONSTRAINT pk_metodo_pago PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL CONSTRAINT uq_metodo_pago UNIQUE
);

CREATE TABLE dbo.turno_caja (
    id                 INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_turno_caja PRIMARY KEY,
    empleado_dni       CHAR(8)        NOT NULL CONSTRAINT fk_turno_empleado REFERENCES dbo.empleado (dni),
    fecha_apertura     DATETIME2(0)   NOT NULL,
    fecha_cierre       DATETIME2(0)   NULL,
    estado             VARCHAR(10)    NOT NULL CONSTRAINT ck_turno_estado CHECK (estado IN ('ABIERTO', 'CERRADO')),
    efectivo_declarado DECIMAL(10, 2) NULL,
    diferencia         DECIMAL(10, 2) NULL
);
/* Un empleado solo puede tener un turno ABIERTO a la vez. */
CREATE UNIQUE INDEX ux_turno_abierto ON dbo.turno_caja (empleado_dni) WHERE estado = 'ABIERTO';

/* Alquiler (antes ticket): guarda el DNI del cliente, el DNI del empleado, un solo ModeloID y la placa.
   RP-03: el alquiler mensual guarda fecha de inicio, fecha de fin (30 días) y el descuento (10%). */
CREATE TABLE dbo.alquiler (
    id               INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_alquiler PRIMARY KEY,
    codigo_barras    CHAR(14)       NOT NULL CONSTRAINT uq_alquiler_codigo UNIQUE,   -- TK-AAAA-NNNNNN
    placa            VARCHAR(6)     NOT NULL,
    cliente_dni      CHAR(8)        NOT NULL CONSTRAINT fk_alquiler_cliente REFERENCES dbo.cliente (dni),
    empleado_dni     CHAR(8)        NOT NULL CONSTRAINT fk_alquiler_empleado REFERENCES dbo.empleado (dni),
    modelo_id        INT            NOT NULL CONSTRAINT fk_alquiler_modelo REFERENCES dbo.modelo (id),
    tipo_vehiculo_id INT            NOT NULL CONSTRAINT fk_alquiler_tipo REFERENCES dbo.tipo_vehiculo (id),
    tipo_alquiler    VARCHAR(10)    NOT NULL CONSTRAINT df_alquiler_tipo DEFAULT 'POR_HORA'
                                    CONSTRAINT ck_alquiler_tipo CHECK (tipo_alquiler IN ('POR_HORA', 'MENSUAL')),
    fecha_entrada    DATETIME2(0)   NOT NULL,
    fecha_salida     DATETIME2(0)   NULL,                 -- NULL hasta que el vehículo sale
    fecha_inicio     DATE           NULL,                 -- solo alquiler mensual
    fecha_fin        DATE           NULL,                 -- solo alquiler mensual (inicio + 30 días)
    descuento        DECIMAL(4, 2)  NOT NULL CONSTRAINT df_alquiler_descuento DEFAULT 0,
    ubicacion        VARCHAR(10)    NULL,
    observacion      NVARCHAR(200)  NULL,
    estado           VARCHAR(10)    NOT NULL CONSTRAINT df_alquiler_estado DEFAULT 'EN_CURSO'
                                    CONSTRAINT ck_alquiler_estado CHECK (estado IN ('EN_CURSO', 'PAGADO', 'ANULADO')),
    CONSTRAINT ck_alquiler_mensual CHECK (
        (tipo_alquiler = 'POR_HORA' AND fecha_inicio IS NULL AND fecha_fin IS NULL AND descuento = 0)
        OR (tipo_alquiler = 'MENSUAL' AND fecha_inicio IS NOT NULL AND fecha_fin = DATEADD(DAY, 30, fecha_inicio)
            AND descuento = 0.10))
);
/* Una placa no puede estar dos veces EN_CURSO. */
CREATE UNIQUE INDEX ux_alquiler_placa_en_curso ON dbo.alquiler (placa) WHERE estado = 'EN_CURSO';

/* El pago queda en el turno de quien COBRA (aunque otro empleado registró la entrada). */
CREATE TABLE dbo.pago (
    id             INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_pago PRIMARY KEY,
    alquiler_id    INT            NOT NULL CONSTRAINT uq_pago_alquiler UNIQUE
                                  CONSTRAINT fk_pago_alquiler REFERENCES dbo.alquiler (id),
    turno_id       INT            NOT NULL CONSTRAINT fk_pago_turno REFERENCES dbo.turno_caja (id),
    metodo_pago_id INT            NOT NULL CONSTRAINT fk_pago_metodo REFERENCES dbo.metodo_pago (id),
    monto_total    DECIMAL(10, 2) NOT NULL CONSTRAINT ck_pago_monto CHECK (monto_total >= 0),
    fecha_pago     DATETIME2(0)   NOT NULL
);

/* RP-01: cada venta guarda el DNI del cliente como llave foránea (cliente 1 - N venta).
   No se puede registrar una venta sin cliente ni eliminar un cliente con ventas. */
CREATE TABLE dbo.venta (
    id          INT IDENTITY(1, 1) NOT NULL CONSTRAINT pk_venta PRIMARY KEY,
    cliente_dni CHAR(8)        NOT NULL CONSTRAINT fk_venta_cliente REFERENCES dbo.cliente (dni),
    alquiler_id INT            NOT NULL CONSTRAINT uq_venta_alquiler UNIQUE
                               CONSTRAINT fk_venta_alquiler REFERENCES dbo.alquiler (id),
    pago_id     INT            NOT NULL CONSTRAINT uq_venta_pago UNIQUE
                               CONSTRAINT fk_venta_pago REFERENCES dbo.pago (id),
    fecha       DATETIME2(0)   NOT NULL,
    subtotal    DECIMAL(10, 2) NOT NULL,
    descuento   DECIMAL(10, 2) NOT NULL CONSTRAINT df_venta_descuento DEFAULT 0,
    total       DECIMAL(10, 2) NOT NULL
);
GO

/* Ventas por cliente (JOIN cliente - venta - alquiler). */
CREATE VIEW dbo.v_ventas_cliente AS
SELECT c.dni, c.nombre_completo, v.fecha, a.codigo_barras, a.placa, a.tipo_alquiler,
       v.subtotal, v.descuento, v.total
FROM dbo.venta v
JOIN dbo.cliente c ON c.dni = v.cliente_dni
JOIN dbo.alquiler a ON a.id = v.alquiler_id;
GO

/* ConsultarTransacciones(dni, fecha1, fecha2): cobros de un empleado en un rango de fechas. */
CREATE PROCEDURE dbo.sp_consultar_transacciones
    @dni CHAR(8), @fecha1 DATE, @fecha2 DATE
AS
BEGIN
    SET NOCOUNT ON;
    SELECT p.fecha_pago, a.codigo_barras, a.placa, c.nombre_completo AS cliente, m.nombre AS metodo, p.monto_total
    FROM dbo.pago p
    JOIN dbo.turno_caja t ON t.id = p.turno_id
    JOIN dbo.alquiler a ON a.id = p.alquiler_id
    JOIN dbo.cliente c ON c.dni = a.cliente_dni
    JOIN dbo.metodo_pago m ON m.id = p.metodo_pago_id
    WHERE t.empleado_dni = @dni
      AND p.fecha_pago >= @fecha1 AND p.fecha_pago < DATEADD(DAY, 1, @fecha2)
    ORDER BY p.fecha_pago;
END;
GO
