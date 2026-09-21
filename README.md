# Hennovo Backend — Documentación de API

API REST de Hennovo (gestión de pedidos, clientes, pagos, logística y flota) construida con Spring Boot.

- **Base path:** todos los endpoints cuelgan de `/api`.
- **Formato:** JSON (`application/json`) para request y response, salvo el endpoint de PDF de remitos.
- **Autenticación:** JWT (Bearer token), salvo `POST /api/auth/login`.
- **Errores:** formato `application/problem+json` (RFC 7807) — ver [Manejo de errores](#manejo-de-errores).

---

## Índice

1. [Autenticación y autorización](#autenticación-y-autorización)
2. [Manejo de errores](#manejo-de-errores)
3. [Convenciones generales](#convenciones-generales)
4. [Auth](#auth)
5. [Usuarios](#usuarios)
6. [Clientes](#clientes)
7. [Categorías de Cliente](#categorías-de-cliente)
8. [Productos](#productos)
9. [Listas de Precio](#listas-de-precio)
10. [Pedidos](#pedidos)
11. [Pedidos Habituales](#pedidos-habituales)
12. [Remitos](#remitos)
13. [Pagos y Cuenta Corriente](#pagos-y-cuenta-corriente)
14. [Cheques](#cheques)
15. [Rutas](#rutas)
16. [Vehículos](#vehículos)
17. [Plantillas de Carga](#plantillas-de-carga)
18. [Planilla de Ventas](#planilla-de-ventas)
19. [Gastos](#gastos)
20. [Pérdidas](#pérdidas)
21. [Control Horario](#control-horario)
22. [Enums de referencia](#enums-de-referencia)

---

## Autenticación y autorización

La API usa **JWT** vía header `Authorization: Bearer <token>`. El token se obtiene con `POST /api/auth/login`.

Existen dos roles: `ADMIN` y `EMPLEADO` (ver enum [`NombreRol`](#nombrerol)).

Reglas de acceso definidas globalmente:

| Ruta | Regla |
|---|---|
| `POST /api/auth/login` | Pública (sin token) |
| `GET /api/users/empleados` | Requiere estar autenticado (cualquier rol) |
| `PATCH /api/users/me/password` | Requiere estar autenticado (cualquier rol) |
| `/api/users/**` (resto) | Solo `ADMIN` |
| `/api/cheques/**` | Solo `ADMIN` |
| `GET /api/control-horario` | Solo `ADMIN` |
| `/api/control-horario/resumen` | Solo `ADMIN` |
| `/api/control-horario/usuario/**` | Solo `ADMIN` |
| `PATCH /api/pedidos/{id}/asignar` | Solo `ADMIN` |
| Cualquier otra ruta | Requiere estar autenticado (cualquier rol) |

> Nota: los controladores no declaran anotaciones `@PreAuthorize` propias — las restricciones de rol viven centralizadas en `SecurityConfig`. Si un endpoint no aparece en la tabla, solo exige estar autenticado.

También existe **rate limiting** por IP: al superarlo, la API responde `429 Too Many Requests` con:

```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Demasiadas solicitudes. Intente nuevamente más tarde."
}
```
y header `Retry-After` con los segundos de espera.

CORS: habilitado para origen `http://localhost:5173`, métodos `GET, POST, PUT, PATCH, DELETE, OPTIONS`, headers `Authorization, Content-Type`, con credenciales.

---

## Manejo de errores

Los errores se devuelven como `ProblemDetail` (RFC 7807), con esta forma general:

```json
{
  "type": "about:blank",
  "title": "Título del error",
  "status": 400,
  "detail": "Descripción del error",
  "errors": ["detalle 1", "detalle 2"]
}
```

| Status | Cuándo ocurre |
|---|---|
| `400 Bad Request` | Validación de campos (`errors` trae un mensaje por campo, formato `campo: mensaje`) o reglas de negocio (`BadRequestException`) |
| `401 Unauthorized` | Credenciales inválidas en login, o `UnauthorizedException` |
| `403 Forbidden` | Usuario desactivado al hacer login, o falta de rol requerido |
| `404 Not Found` | Recurso no encontrado (`NotFoundException`) |
| `409 Conflict` | Conflicto de negocio, p. ej. duplicados (`ConflictException`) |
| `429 Too Many Requests` | Rate limit excedido (formato distinto, ver arriba) |
| `500 Internal Server Error` | Error inesperado no controlado |

---

## Convenciones generales

- Los `id` son `Long`.
- Las fechas (`LocalDate`) van en formato `YYYY-MM-DD`; las horas (`LocalTime`) en `HH:mm:ss`; los datetime (`LocalDateTime`) en formato ISO-8601.
- Los importes (`BigDecimal`) se serializan como número JSON.
- Los endpoints de creación devuelven `201 Created` con el recurso creado en el body.
- Los endpoints de baja/activación/estado que no devuelven cuerpo responden `204 No Content`.
- Los campos de texto marcados como "sin HTML" son validados por una anotación custom `@NoHtml` que rechaza etiquetas HTML.
- Los campos marcados `@NotBlank` / `@NotNull` / etc. son obligatorios; si faltan, la API responde `400` con el detalle del campo.

---

## Auth

Base: `/api/auth`

### `POST /api/auth/login`
Autentica un usuario y devuelve un JWT. **Público.**

**Request body** (`LoginRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `email` | string | Sí | Formato email |
| `password` | string | Sí | |

**Response `200 OK`** (`AuthResponse`):

| Campo | Tipo |
|---|---|
| `token` | string |
| `type` | string |
| `id` | Long |
| `nombre` | string |
| `apellido` | string |
| `email` | string |
| `rol` | [`NombreRol`](#nombrerol) |

**Errores:** `401` credenciales inválidas · `403` usuario desactivado.

---

## Usuarios

Base: `/api/users` · **Requiere rol `ADMIN`**, salvo donde se indica.

### `POST /api/users`
Crea un usuario/empleado. `201 Created` → `UserResponse`.

**Request body** (`CreateUserRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `nombre` | string | Sí | máx. 20 car., sin HTML |
| `apellido` | string | Sí | máx. 20 car., sin HTML |
| `email` | string | Sí | formato email |
| `password` | string | Sí | |

### `GET /api/users/{id}`
Obtiene un usuario por id → `UserResponse`.

### `GET /api/users`
Lista todos los usuarios → `List<UserResponse>`.

### `GET /api/users/empleados`
Lista empleados activos → `List<UserResponse>`. **Cualquier usuario autenticado.**

### `PUT /api/users/{id}`
Actualiza nombre/apellido → `UserResponse`.

**Request body** (`UpdateUserRequest`): `nombre` (string, obligatorio, sin HTML), `apellido` (string, obligatorio, sin HTML).

### `PATCH /api/users/{id}/deactivate`
Desactiva un usuario. `204 No Content`.

### `PATCH /api/users/me/password`
Cambia la contraseña del usuario autenticado. `204 No Content`. **Cualquier usuario autenticado.**

**Request body** (`ChangePasswordRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `currentPassword` | string | Sí | |
| `newPassword` | string | Sí | mín. 6 caracteres |

**`UserResponse`:**

| Campo | Tipo |
|---|---|
| `id` | Long |
| `nombre` | string |
| `apellido` | string |
| `email` | string |
| `rol` | [`NombreRol`](#nombrerol) |
| `activo` | boolean |

---

## Clientes

Base: `/api/clientes`

### `POST /api/clientes`
Crea un cliente. `201 Created` → `ClienteResponseDTO`.

**Request body** (`ClienteRequestDTO`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `nombre` | string | Sí | sin HTML |
| `direccion` | string | Sí | sin HTML |
| `localidad` | string | Sí | sin HTML |
| `telefono` | string | Sí | sin HTML |
| `idCategoria` | Long | Sí | referencia a categoría de cliente |

### `GET /api/clientes/buscar?nombre=`
Busca clientes por nombre → `List<ClienteResponseDTO>`.

### `GET /api/clientes/{id}`
Obtiene un cliente por id → `ClienteResponseDTO`.

### `GET /api/clientes/{clienteId}/cuenta-corriente`
Obtiene la cuenta corriente del cliente → `CuentaCorrienteResponseDTO` (ver [Pagos y Cuenta Corriente](#pagos-y-cuenta-corriente)).

### `GET /api/clientes`
Lista todos los clientes → `List<ClienteResponseDTO>`.

### `PUT /api/clientes/{id}`
Modifica un cliente (mismo body que la creación) → `ClienteResponseDTO`.

### `PATCH /api/clientes/{id}/desactivar`
Desactiva un cliente. `204 No Content`.

### `PATCH /api/clientes/{id}/reactivar`
Reactiva un cliente → `ClienteResponseDTO`.

**`ClienteResponseDTO`:**

| Campo | Tipo |
|---|---|
| `id` | Long |
| `nombre` | string |
| `direccion` | string |
| `localidad` | string |
| `telefono` | string |
| `activo` | boolean |
| `idCategoria` | Long |
| `nombreCategoria` | string |

---

## Categorías de Cliente

Base: `/api/categorias-clientes`

### `POST /api/categorias-clientes`
Crea una categoría. `201 Created` → `CategoriaClienteResponseDTO`.

**Request body** (`CategoriaClienteRequestDTO`): `nombre` (string, obligatorio, sin HTML).

### `GET /api/categorias-clientes`
Lista todas → `List<CategoriaClienteResponseDTO>`.

### `GET /api/categorias-clientes/{id}`
Obtiene por id → `CategoriaClienteResponseDTO`.

### `PUT /api/categorias-clientes/{id}`
Modifica (mismo body) → `CategoriaClienteResponseDTO`.

### `DELETE /api/categorias-clientes/{id}`
Elimina. `204 No Content`.

**`CategoriaClienteResponseDTO`:** `id` (Long), `nombre` (string).

---

## Productos

Base: `/api/productos`

### `POST /api/productos`
Crea un producto. `201 Created` → `ProductoResponse`.

**Request body** (`ProductoRequest`):

| Campo | Tipo |
|---|---|
| `tipoHuevo` | [`TipoHuevo`](#tipohuevo) |
| `tamaño` | [`Tamaño`](#tamaño) |
| `presentacion` | [`Presentacion`](#presentacion) |

> Este DTO no lleva anotaciones `@Valid`/`@NotNull` explícitas en el controlador.

### `GET /api/productos`
Lista productos activos → `List<ProductoResponse>`.

### `GET /api/productos/todos`
Lista todos los productos (activos e inactivos) → `List<ProductoResponse>`.

### `GET /api/productos/{id}`
Obtiene por id → `ProductoResponse`.

### `PUT /api/productos/{id}`
Actualiza (mismo body que creación) → `ProductoResponse`.

### `PATCH /api/productos/{id}/desactivar`
Desactiva. `204 No Content`.

### `PATCH /api/productos/{id}/reactivar`
Reactiva → `ProductoResponse`.

**`ProductoResponse`:** `id` (Long), `tipoHuevo`, `tamaño`, `presentacion`, `activo` (boolean).

---

## Listas de Precio

Base: `/api/listas-precio`

### `POST /api/listas-precio`
Crea una lista de precios. `201 Created` → `ListaPrecioResponse`.

**Request body** (`ListaPrecioRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `fechaDesde` | LocalDate | Sí | |
| `fechaHasta` | LocalDate | No | |
| `precios` | `List<PrecioProductoRequest>` | Sí, no vacía | |

`PrecioProductoRequest`:

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `productoId` | Long | Sí | |
| `categoriaId` | Long | Sí | categoría de cliente |
| `precio` | BigDecimal | Sí | > 0, hasta 10 enteros y 2 decimales |

### `GET /api/listas-precio`
Lista todas → `List<ListaPrecioResponse>`.

### `GET /api/listas-precio/vigente`
Obtiene la lista de precios vigente → `ListaPrecioResponse`.

### `GET /api/listas-precio/{id}`
Obtiene por id → `ListaPrecioResponse`.

### `PUT /api/listas-precio/{id}`
Actualiza (mismo body que creación) → `ListaPrecioResponse`.

**`ListaPrecioResponse`:** `id`, `fechaDesde`, `fechaHasta`, `precios: List<PrecioProductoResponse>`.
**`PrecioProductoResponse`:** `id`, `productoId`, `categoriaId`, `precio`.

---

## Pedidos

Base: `/api/pedidos`

### `POST /api/pedidos`
Crea un pedido. `201 Created` → `PedidoResponse`.

**Request body** (`PedidoRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `clienteId` | Long | Sí | |
| `fecha` | LocalDate | Sí | |
| `observaciones` | string | No | sin HTML |
| `banco` | string | No | |
| `usuarioId` | Long | No | |
| `detalles` | `List<DetallePedidoRequest>` | Sí, no vacía | |

`DetallePedidoRequest`: `productoId` (Long, obligatorio), `cantidad` (Integer, obligatorio, ≥ 1).

### `GET /api/pedidos/{id}`
Obtiene por id → `PedidoResponse`.

### `GET /api/pedidos`
Lista paginada de pedidos → `PaginaResponse<PedidoResponse>`.

**Query params:**

| Param | Tipo | Obligatorio | Default |
|---|---|---|---|
| `fecha` | LocalDate | No | — |
| `buscar` | string | No | — |
| `page` | int | No | `0` |
| `size` | int | No | `10` |

`PaginaResponse<T>`: `contenido: List<T>`, `pagina` (int), `tamano` (int), `totalElementos` (long), `totalPaginas` (int).

### `PUT /api/pedidos/{id}`
Actualiza un pedido (mismo body que creación) → `PedidoResponse`.

### `PATCH /api/pedidos/{id}/entregado`
Marca el pedido como entregado. `204 No Content`.

### `PATCH /api/pedidos/{id}/pagado`
Marca el pedido como pagado. `204 No Content`.

### `PATCH /api/pedidos/{id}/asignar`
Asigna un usuario/empleado al pedido → `PedidoResponse`. **Requiere rol `ADMIN`.**

**Request body** (`AsignarUsuarioPedidoRequest`): `usuarioId` (Long, obligatorio).

**`PedidoResponse`:**

| Campo | Tipo |
|---|---|
| `id` | Long |
| `fecha` | LocalDate |
| `entregado` | boolean |
| `pagado` | boolean |
| `observaciones` | string |
| `banco` | string |
| `clienteId` | Long |
| `clienteNombre` | string |
| `usuarioId` | Long |
| `usuarioNombre` | string |
| `rutaId` | Long |
| `ordenRuta` | Integer |
| `detalles` | `List<DetallePedidoResponse>` |
| `total` | BigDecimal |

`DetallePedidoResponse`: `id`, `productoId`, `cantidad`, `precioUnitario`, `subtotal`.

---

## Pedidos Habituales

Base: `/api/pedidos-habituales`

Representa productos/cantidades que un cliente suele pedir de forma recurrente.

### `POST /api/pedidos-habituales`
Crea un pedido habitual. `201 Created` → `PedidoHabitualResponseDTO`.

**Request body** (`PedidoHabitualRequestDTO`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `idCliente` | Long | Sí | |
| `idProducto` | Long | Sí | |
| `cantidad` | Integer | Sí | ≥ 1 |

### `GET /api/pedidos-habituales/{id}`
Obtiene por id → `PedidoHabitualResponseDTO`.

### `GET /api/pedidos-habituales/cliente/{idCliente}`
Lista los pedidos habituales de un cliente → `List<PedidoHabitualResponseDTO>`.

### `PUT /api/pedidos-habituales/{id}`
Modifica (mismo body que creación) → `PedidoHabitualResponseDTO`.

### `DELETE /api/pedidos-habituales/{id}`
Elimina. `204 No Content`.

**`PedidoHabitualResponseDTO`:** `id`, `idCliente`, `nombreCliente`, `idProducto`, `producto`, `cantidad`.

**Errores propios del dominio:** `PedidoHabitualDuplicadoException` (409, cliente+producto repetido), `PedidoHabitualNoEncontradoException` (404).

---

## Remitos

Base: `/api/remitos`

### `POST /api/remitos`
Crea un remito. `201 Created` → `RemitoResponse`.

**Request body** (`RemitoRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `pedidoId` | Long | Sí | |
| `fecha` | LocalDate | Sí | |
| `correspondeFacturacion` | boolean | Sí | |
| `detalles` | `List<DetalleRemitoRequest>` | Sí, no vacía | |

`DetalleRemitoRequest`: `productoId` (Long, obligatorio), `cantidad` (Integer, obligatorio, ≥ 1).

### `GET /api/remitos/{id}`
Obtiene por id → `RemitoResponse`.

### `GET /api/remitos`
Lista todos → `List<RemitoResponse>`.

### `GET /api/remitos/pedido/{pedidoId}`
Obtiene el remito asociado a un pedido → `RemitoResponse`.

### `GET /api/remitos/{id}/pdf`
Genera y descarga el remito en PDF.

**Response:** `200 OK`, `Content-Type: application/pdf`, header `Content-Disposition: attachment; filename=remito-{id}.pdf`, body binario.

**`RemitoResponse`:**

| Campo | Tipo |
|---|---|
| `id` | Long |
| `fecha` | LocalDate |
| `pedidoId` | Long |
| `clienteId` | Long |
| `clienteNombre` | string |
| `clienteDireccion` | string |
| `clienteLocalidad` | string |
| `correspondeFacturacion` | boolean |
| `detalles` | `List<DetalleRemitoResponse>` |
| `total` | BigDecimal |

`DetalleRemitoResponse`: `id`, `productoId`, `cantidad`, `precioUnitario`, `importe`.

---

## Pagos y Cuenta Corriente

Base: `/api/pagos` (más el endpoint de cuenta corriente expuesto bajo `/api/clientes`).

### `POST /api/pagos`
Registra un pago. `201 Created` → `PagoResponseDTO`.

**Request body** (`PagoRequestDTO`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `clienteId` | Long | Sí | |
| `importe` | BigDecimal | Sí | > 0 |
| `medioPago` | [`MedioPago`](#mediopago) | Sí | |
| `numeroComprobante` | string | No | máx. 100 car., sin HTML |
| `observaciones` | string | No | máx. 500 car., sin HTML |
| `titular` | string | No* | sin HTML — datos del cheque |
| `codigoBanco` | string | No* | sin HTML — datos del cheque |
| `nombreBanco` | string | No* | sin HTML — datos del cheque |
| `fechaPago` | LocalDate | No* | datos del cheque |
| `endosado` | boolean | No* | datos del cheque |
| `firmaTitular` | boolean | No* | datos del cheque |

\* Los campos de cheque no llevan `@NotNull` a nivel DTO, pero la capa de servicio los exige cuando `medioPago = CHEQUE`.

### `GET /api/pagos/{id}`
Obtiene por id → `PagoResponseDTO`.

### `GET /api/pagos`
Lista todos → `List<PagoResponseDTO>`.

### `GET /api/pagos/cliente/{clienteId}`
Lista los pagos de un cliente → `List<PagoResponseDTO>`.

### `PATCH /api/pagos/{id}/anular`
Anula un pago. `204 No Content`.

**`PagoResponseDTO`:** `id`, `fecha`, `importe`, `medioPago`, `numeroComprobante`, `observaciones`, `anulado` (boolean), `clienteId`.

### `GET /api/clientes/{clienteId}/cuenta-corriente`
Devuelve el estado de cuenta corriente del cliente → `CuentaCorrienteResponseDTO`.

**`CuentaCorrienteResponseDTO`:**

| Campo | Tipo |
|---|---|
| `clienteId` | Long |
| `clienteNombre` | string |
| `totalPedidos` | BigDecimal |
| `totalPagos` | BigDecimal |
| `saldo` | BigDecimal |
| `saldoAFavor` | BigDecimal |
| `pedidos` | `List<PedidoEstadoCuentaDTO>` |
| `movimientos` | `List<MovimientoCuentaCorrienteDTO>` |

`PedidoEstadoCuentaDTO`: `pedidoId`, `fecha`, `total`, `pagado`, `pendiente`, `estado` ([`EstadoPagoPedido`](#estadopagopedido)).

`MovimientoCuentaCorrienteDTO`: `fecha`, `tipo` (`PEDIDO` | `PAGO`), `referenciaId`, `descripcion`, `importe`, `saldo`.

---

## Cheques

Base: `/api/cheques` · **Requiere rol `ADMIN`** (regla global).

### `POST /api/cheques`
Registra un cheque. `201 Created` → `ChequeResponse`.

**Request body** (`ChequeRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `fechaIngreso` | LocalDate | Sí | |
| `clienteId` | Long | Sí | |
| `titular` | string | Sí | sin HTML |
| `codigoBanco` | string | Sí | sin HTML |
| `nombreBanco` | string | Sí | sin HTML |
| `importe` | BigDecimal | Sí | > 0 |
| `fechaPago` | LocalDate | Sí | |
| `endosado` | boolean | Sí | |
| `firmaTitular` | boolean | Sí | |

### `GET /api/cheques`
Lista todos → `List<ChequeResponse>`.

### `GET /api/cheques/{id}`
Obtiene por id → `ChequeResponse`.

### `PUT /api/cheques/{id}`
Actualiza (mismo body que creación) → `ChequeResponse`.

### `PATCH /api/cheques/{id}/desactivar`
Desactiva. `204 No Content`.

### `PATCH /api/cheques/{id}/reactivar`
Reactiva → `ChequeResponse`.

**`ChequeResponse`:** `id`, `fechaIngreso`, `clienteId`, `clienteNombre`, `titular`, `codigoBanco`, `nombreBanco`, `importe`, `fechaPago`, `endosado`, `firmaTitular`, `activo`.

---

## Rutas

Base: `/api/rutas`

### `POST /api/rutas`
Crea una ruta de reparto. `201 Created` → `RutaResponse`.

**Request body** (`RutaRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `fecha` | LocalDate | Sí | |
| `nombre` | string | Sí | máx. 100 car., sin HTML |
| `observaciones` | string | No | máx. 500 car., sin HTML |
| `usuarioId` | Long | Sí | empleado asignado |
| `vehiculoId` | Long | Sí | |

### `GET /api/rutas/{id}`
Obtiene por id → `RutaResponse`.

### `GET /api/rutas`
Lista todas → `List<RutaResponse>`.

### `GET /api/rutas/pedidos-disponibles?fecha=`
Lista pedidos disponibles para asignar a una ruta en esa fecha → `List<RutaPedidoResponse>`.

### `PUT /api/rutas/{id}`
Actualiza (mismo body que creación) → `RutaResponse`.

### `PATCH /api/rutas/{id}/desactivar`
Desactiva la ruta. `204 No Content`.

### `GET /api/rutas/{id}/pedidos`
Lista los pedidos asignados a la ruta → `List<RutaPedidoResponse>`.

### `PUT /api/rutas/{id}/pedidos`
Asigna/reordena pedidos en la ruta → `List<RutaPedidoResponse>`.

**Request body** (`AsignarPedidosRequest`): `pedidos: List<OrdenPedidoRequest>` (no vacía).
`OrdenPedidoRequest`: `pedidoId` (Long, obligatorio), `orden` (Integer, obligatorio, ≥ 1).

### `DELETE /api/rutas/{rutaId}/pedidos/{pedidoId}`
Quita un pedido de la ruta. `204 No Content`.

**`RutaResponse`:** `id`, `fecha`, `nombre`, `observaciones`, `activa` (boolean), `usuarioId`, `vehiculoId`.

**`RutaPedidoResponse`:** `pedidoId`, `orden`, `clienteId`, `clienteNombre`, `direccion`, `entregado` (boolean).

---

## Vehículos

Base: `/api/vehiculos`

### `POST /api/vehiculos`
Crea un vehículo. `201 Created` → `VehiculoResponse`.

**Request body** (`CreateVehiculoRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `patente` | string | Sí | sin HTML |
| `marca` | string | Sí | sin HTML |
| `modelo` | string | Sí | sin HTML |
| `anio` | Integer | No | |
| `kilometrajeActual` | Integer | Sí | ≥ 0 |

### `GET /api/vehiculos`
Lista vehículos activos → `List<VehiculoResponse>`.

### `GET /api/vehiculos/todos`
Lista todos los vehículos (activos e inactivos) → `List<VehiculoResponse>`.

### `GET /api/vehiculos/vencimientos?diasAnticipacion=`
Lista vehículos con vencimientos próximos (seguro, ITV, SENASA, service). `diasAnticipacion` es `int`, opcional, default `15`.

### `GET /api/vehiculos/{id}`
Obtiene por id → `VehiculoResponse`.

### `PUT /api/vehiculos/{id}`
Actualiza datos generales/mantenimiento → `VehiculoResponse`.

**Request body** (`UpdateVehiculoRequest`, todos los campos opcionales):

| Campo | Tipo | Notas |
|---|---|---|
| `patente` | string | sin HTML |
| `marca` | string | sin HTML |
| `modelo` | string | sin HTML |
| `anio` | Integer | |
| `proximoServiceFecha` | LocalDate | |
| `vencimientoSeguro` | LocalDate | |
| `vencimientoItv` | LocalDate | |
| `vencimientoSenasa` | LocalDate | |
| `proximoCambioAceiteKm` | Integer | |
| `proximaRotacionAlineadoKm` | Integer | |
| `proximoCambioCorreaKm` | Integer | |
| `observacionesMantenimiento` | string | sin HTML |

### `PATCH /api/vehiculos/{id}/desactivar`
Desactiva el vehículo. `204 No Content`.

### `PATCH /api/vehiculos/{id}/reactivar`
Reactiva el vehículo → `VehiculoResponse`.

### `PATCH /api/vehiculos/{id}/kilometraje`
Actualiza el kilometraje actual y registra el cambio en el historial → `VehiculoResponse`.

**Request body** (`ActualizarKilometrajeRequest`): `kilometraje` (Integer, obligatorio, > 0), `observacion` (string, opcional).

### `GET /api/vehiculos/{id}/kilometraje/historial`
Lista el historial de cambios de kilometraje → `List<KilometrajeHistorialResponse>`.

**`VehiculoResponse`:** `id`, `patente`, `marca`, `modelo`, `anio`, `kilometrajeActual`, `proximoServiceFecha`, `vencimientoSeguro`, `vencimientoItv`, `vencimientoSenasa`, `proximoCambioAceiteKm`, `proximaRotacionAlineadoKm`, `proximoCambioCorreaKm`, `observacionesMantenimiento`, `activo`.

**`KilometrajeHistorialResponse`:** `id`, `vehiculoId`, `kilometraje`, `fecha` (LocalDateTime), `observacion`.

---

## Plantillas de Carga

Croquis de carga por vehículo/fecha, organizado en una grilla de celdas con detalles de producto y cantidad. Endpoints repartidos entre `/api/vehiculos/{vehiculoId}/plantillas` y `/api/plantillas-carga`.

### `GET /api/vehiculos/{vehiculoId}/plantillas?fecha=`
Obtiene las plantillas de carga de un vehículo para una fecha → `List<PlantillaCargaResponse>`.

### `PUT /api/vehiculos/{vehiculoId}/plantillas/configurar`
Configura (crea/redimensiona) el croquis del vehículo → `List<PlantillaCargaResponse>`.

**Request body** (`ConfigurarCroquisRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `filas` | Integer | Sí | entre 2 y 20 |
| `columnas` | Integer | Sí | entre 2 y 20 |
| `incluirNivelSuperior` | boolean | Sí | |
| `confirmarPerdidaDatos` | boolean | No | default `false`; confirma sobrescritura si el redimensionado implica perder datos |

### `POST /api/vehiculos/{vehiculoId}/plantillas/copiar`
Copia el contenido del croquis de una fecha a otra. `204 No Content`.

**Request body** (`CopiarCroquisRequest`): `fechaOrigen` (LocalDate, obligatorio), `fechaDestino` (LocalDate, obligatorio).

### `GET /api/vehiculos/{vehiculoId}/plantillas/fechas-con-contenido`
Lista las fechas que tienen contenido cargado para ese vehículo → `List<LocalDate>`.

### `POST /api/plantillas-carga/celdas/{celdaId}/detalles`
Agrega un detalle (producto + cantidad) a una celda. `204 No Content`.

**Request body** (`AgregarDetalleCeldaRequest`): `fecha` (LocalDate, obligatorio), `productoId` (Long, obligatorio), `cantidad` (Integer, obligatorio, ≥ 1).

### `PUT /api/plantillas-carga/detalles/{detalleId}/mover`
Mueve un detalle a otra celda. `204 No Content`.

**Request body** (`MoverDetalleCeldaRequest`): `celdaDestinoId` (Long, obligatorio).

### `PUT /api/plantillas-carga/detalles/{detalleId}`
Actualiza la cantidad de un detalle. `204 No Content`.

**Request body** (`ActualizarCantidadDetalleRequest`): `cantidad` (Integer, obligatorio, ≥ 1).

### `DELETE /api/plantillas-carga/detalles/{detalleId}`
Elimina un detalle. `204 No Content`.

**`PlantillaCargaResponse`:** `id`, `nombre`, `filas`, `columnas`, `nivel` (string), `vehiculoId`, `celdas: List<CeldaPlantillaResponse>`.

**`CeldaPlantillaResponse`:** `id`, `fila`, `columna`, `detalles: List<DetalleCeldaResponse>`.

**`DetalleCeldaResponse`:** `id`, `productoId`, `cantidad`.

---

## Planilla de Ventas

Base: `/api/planilla-ventas`

Vista operativa de ventas por empleado y fecha, con orden de visita por cliente.

### `GET /api/planilla-ventas?usuarioId=&fecha=`
Obtiene la planilla de ventas de un usuario/fecha → `List<PlanillaVentasClienteDTO>`.

### `PUT /api/planilla-ventas/orden`
Guarda el orden de clientes en la planilla. `204 No Content`.

**Request body** (`PlanillaVentasOrdenRequest`):

| Campo | Tipo | Obligatorio |
|---|---|---|
| `usuarioId` | Long | Sí |
| `fecha` | LocalDate | Sí |
| `items` | `List<PlanillaVentasOrdenItemRequest>` | Sí, no vacía |

`PlanillaVentasOrdenItemRequest`: `clienteId` (Long, obligatorio), `orden` (Integer, obligatorio).

**`PlanillaVentasClienteDTO`:**

| Campo | Tipo |
|---|---|
| `clienteId` | Long |
| `clienteNombre` | string |
| `direccion` | string |
| `pedidoId` | Long |
| `entregado` | boolean |
| `pagado` | boolean |
| `banco` | string |
| `totalPedido` | BigDecimal |
| `saldoPendiente` | BigDecimal |
| `saldoAFavor` | BigDecimal |
| `orden` | Integer |
| `topes` | `List<TopeProductoDTO>` |
| `cantidades` | `CantidadesTamanoDTO` |

`TopeProductoDTO`: `productoId`, `producto` (string), `cantidad`.

`CantidadesTamanoDTO`: `t1Color`, `t1Blanco`, `t2Color`, `t2Blanco`, `t3Color`, `t3Blanco`, `otros` (todos Integer).

---

## Gastos

Base: `/api/gastos`

### `POST /api/gastos`
Registra un gasto. `201 Created` → `GastoResponse`.

**Request body** (`GastoRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `fecha` | LocalDate | Sí | |
| `categoria` | string | Sí | sin HTML |
| `descripcion` | string | Sí | sin HTML |
| `importe` | BigDecimal | Sí | > 0 |
| `observaciones` | string | No | sin HTML |

### `GET /api/gastos?desde=&hasta=`
Lista gastos. Si se pasan ambos parámetros (LocalDate, opcionales), filtra por rango de fechas; si no, devuelve todos → `List<GastoResponse>`.

### `GET /api/gastos/{id}`
Obtiene por id → `GastoResponse`.

### `PUT /api/gastos/{id}`
Actualiza (mismo body que creación) → `GastoResponse`.

### `DELETE /api/gastos/{id}`
Elimina. `204 No Content`.

**`GastoResponse`:** `id`, `fecha`, `categoria`, `descripcion`, `importe`, `observaciones`.

---

## Pérdidas

Base: `/api/perdidas`

### `POST /api/perdidas`
Registra una pérdida de producto. `201 Created` → `PerdidaResponse`.

**Request body** (`PerdidaRequest`):

| Campo | Tipo | Obligatorio | Notas |
|---|---|---|---|
| `fecha` | LocalDate | Sí | |
| `productoId` | Long | Sí | |
| `cantidad` | Integer | Sí | ≥ 1 |
| `motivo` | string | Sí | sin HTML |
| `observaciones` | string | No | sin HTML |

### `GET /api/perdidas?desde=&hasta=`
Lista pérdidas, con filtro opcional por rango de fechas (igual comportamiento que Gastos) → `List<PerdidaResponse>`.

### `GET /api/perdidas/{id}`
Obtiene por id → `PerdidaResponse`.

### `PUT /api/perdidas/{id}`
Actualiza (mismo body que creación) → `PerdidaResponse`.

### `DELETE /api/perdidas/{id}`
Elimina. `204 No Content`.

**`PerdidaResponse`:** `id`, `fecha`, `productoId`, `producto` (string), `cantidad`, `motivo`, `observaciones`.

---

## Control Horario

Base: `/api/control-horario`

### `POST /api/control-horario`
Registra una marca de horario (propia del usuario autenticado). `201 Created` → `ControlHorarioResponse`.

**Request body** (`ControlHorarioRequest`):

| Campo | Tipo | Obligatorio |
|---|---|---|
| `fecha` | LocalDate | Sí |
| `turno` | [`Turno`](#turno) | Sí |
| `horaIngreso` | LocalTime | Sí |
| `horaEgreso` | LocalTime | Sí |

### `GET /api/control-horario/mios`
Lista los registros propios del usuario autenticado → `List<ControlHorarioResponse>`.

### `GET /api/control-horario?desde=&hasta=`
Lista todos los registros en un rango de fechas. **Requiere rol `ADMIN`.**

### `GET /api/control-horario/usuario/{usuarioId}?desde=&hasta=`
Lista los registros de un usuario puntual en un rango de fechas. **Requiere rol `ADMIN`.**

### `GET /api/control-horario/resumen?desde=&hasta=`
Resumen de horas trabajadas por usuario en un rango → `List<ResumenHorasDTO>`. **Requiere rol `ADMIN`.**

### `PUT /api/control-horario/{id}`
Actualiza un registro (mismo body que creación) → `ControlHorarioResponse`.

### `DELETE /api/control-horario/{id}`
Elimina un registro. `204 No Content`.

**`ControlHorarioResponse`:** `id`, `fecha`, `turno` (string), `horaIngreso`, `horaEgreso`, `horasTrabajadas` (Double), `usuarioId`, `usuarioNombre`.

**`ResumenHorasDTO`:** `usuarioId`, `usuarioNombre`, `totalHoras` (Double).

---

## Enums de referencia

#### `NombreRol`
`ADMIN` · `EMPLEADO`

#### `MedioPago`
`EFECTIVO` · `TRANSFERENCIA` · `CHEQUE`

#### `EstadoPagoPedido`
`PENDIENTE` · `PARCIAL` · `PAGADO`

#### `Turno`
`MANANA` · `TARDE`

#### `TipoHuevo`
`BLANCO` · `COLOR`

#### `Tamaño`
`GRANDE` · `MEDIANO` · `CHICO` · `CHICO_4` · `BOLITA` · `SUPER`

#### `Presentacion`
`MAPLE` · `CAJON` · `CAJITA` · `CAJON_DE_CAJITAS`
