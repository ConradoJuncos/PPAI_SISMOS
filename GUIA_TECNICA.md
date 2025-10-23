# Guía Técnica Detallada - PPAI Sismos

## 📋 Tabla de Contenidos
1. [Cambios Realizados](#cambios-realizados)
2. [Instalación de Dependencias](#instalación-de-dependencias)
3. [Endpoints API](#endpoints-api)
4. [Errores Corregidos](#errores-corregidos)
5. [Cómo Extender](#cómo-extender)

---

## ✨ Cambios Realizados

### Backend - Nuevos Archivos y Cambios

#### 1. DatabaseConnection.java (NUEVO)
**Ubicación:** `backend/src/main/java/com/ppai/app/datos/DatabaseConnection.java`

**Responsabilidad:** Manejar conexiones SQLite y inicialización de BD

```java
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:sismos.db";
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException
    public static void inicializarDB()
    public static void cerrarConexion()
}
```

**Características:**
- ✅ Singleton pattern para conexión
- ✅ Inicializa BD automáticamente
- ✅ Crea tabla `entidad_ejemplo` si no existe
- ✅ Manejo correcto de recursos

**Tabla creada:**
```sql
CREATE TABLE entidad_ejemplo (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

#### 2. ControladorEjemplo.java (ACTUALIZADO)
**Ubicación:** `backend/src/main/java/com/ppai/app/controlador/ControladorEjemplo.java`

**Nuevos Endpoints:**
```java
app.post("/crear_entidad", this::crearEntidad);
app.get("/obtener_entidades", this::obtenerEntidades);
app.get("/obtener_entidad/{id}", this::obtenerEntidadPorId);
```

**Métodos implementados:**
- `crearEntidad(Context ctx)` - Crea nueva entidad
- `obtenerEntidades(Context ctx)` - Obtiene todas
- `obtenerEntidadPorId(Context ctx)` - Obtiene por ID

#### 3. Main.java (ACTUALIZADO)
**Cambios:**
```java
// Inicializar BD al iniciar servidor
DatabaseConnection.inicializarDB();

// Cerrar BD al detener servidor
DatabaseConnection.cerrarConexion();
```

#### 4. pom.xml (ACTUALIZADO)
**Dependencias agregadas:**
```xml
<!-- Jackson DataBind - Object Mapper para Javalin -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.0</version>
</dependency>

<!-- Exec Plugin para ejecutar Main -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.6.0</version>
    <configuration>
        <mainClass>com.ppai.app.Main</mainClass>
    </configuration>
</plugin>
```

---

### Frontend - Nuevos Archivos y Cambios

#### 1. MainFrame.java (NUEVO)
**Ubicación:** `frontend/src/main/java/com/ppai/app/frontend/gui/MainFrame.java`

**Responsabilidad:** Interfaz gráfica de escritorio con Swing

**Componentes Swing:**
- `JTextField txtNombre` - Campo para ingresar nombre
- `JButton btnCrear` - Botón crear entidad
- `JButton btnObtener` - Botón obtener entidades
- `JTextArea txtResultados` - Muestra respuestas JSON
- `JScrollPane scrollPane` - Scroll para el área de texto

**Métodos:**
```java
private void crearEntidad()      // Maneja click en "Crear"
private void obtenerEntidades()  // Maneja click en "Obtener"
private void mostrarError(String mensaje)
```

#### 2. ApiService.java (ACTUALIZADO)
**Ubicación:** `frontend/src/main/java/com/ppai/app/frontend/service/ApiService.java`

**Nuevos Métodos:**
```java
public String crearEntidad(String jsonBody) 
    throws IOException, ParseException

public String obtenerEntidades() 
    throws IOException, ParseException

public String obtenerEntidadPorId(Long id) 
    throws IOException, ParseException
```

**Características:**
- ✅ Formatea respuestas JSON con pretty-printing
- ✅ Maneja excepciones correctamente
- ✅ Usa Apache HttpClient 5.x
- ✅ Compatible con ParseException de HC5

---

## 📦 Instalación de Dependencias

### Backend pom.xml - Dependencias Completas

```xml
<dependencies>
    <!-- Javalin 5.6.3 - Framework HTTP -->
    <dependency>
        <groupId>io.javalin</groupId>
        <artifactId>javalin</artifactId>
        <version>5.6.3</version>
    </dependency>

    <!-- SLF4J 2.0.9 - Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>

    <!-- Gson 2.10.1 - JSON Serialization -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>

    <!-- SQLite JDBC 3.43.0.0 - Driver SQLite -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.43.0.0</version>
    </dependency>

    <!-- Jackson DataBind 2.15.0 - Object Mapper (IMPORTANTE) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.0</version>
    </dependency>
</dependencies>
```

### Frontend pom.xml - Dependencias Completas

```xml
<dependencies>
    <!-- Gson 2.10.1 - JSON Serialization -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>

    <!-- Apache HttpClient 5.2.1 - HTTP Client -->
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
        <version>5.2.1</version>
    </dependency>
</dependencies>
```

---

## 🔌 Endpoints API

### 1. POST /crear_entidad

Crea una nueva entidad en la base de datos.

**Request:**
```bash
POST http://localhost:8080/crear_entidad
Content-Type: application/json

{
  "nombre": "Sismo del Este"
}
```

**Response (201 Created):**
```json
{
  "mensaje": "Entidad creada exitosamente",
  "entidad": {
    "id": 1,
    "nombre": "Sismo del Este"
  }
}
```

**Response (400 Bad Request):**
```json
{
  "error": "El nombre es requerido"
}
```

**Code Pattern:**
```java
private void crearEntidad(Context ctx) {
    // Parsea JSON
    EntidadEjemplo entidad = gson.fromJson(ctx.body(), EntidadEjemplo.class);
    
    // Valida
    if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
        ctx.status(400).json(Map.of("error", "El nombre es requerido"));
        return;
    }
    
    // Inserta en BD usando last_insert_rowid() para SQLite
    String sql = "INSERT INTO entidad_ejemplo (nombre) VALUES (?)";
    // ... ejecuta SQL y retorna JSON con ID
}
```

---

### 2. GET /obtener_entidades

Obtiene todas las entidades registradas.

**Request:**
```bash
GET http://localhost:8080/obtener_entidades
```

**Response (200 OK):**
```json
{
  "cantidad": 3,
  "entidades": [
    {"id": 3, "nombre": "Tercera Entidad"},
    {"id": 2, "nombre": "Segunda Entidad"},
    {"id": 1, "nombre": "Sismo del Este"}
  ]
}
```

---

### 3. GET /obtener_entidad/{id}

Obtiene una entidad específica por ID.

**Request:**
```bash
GET http://localhost:8080/obtener_entidad/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Sismo del Este"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Entidad no encontrada"
}
```

---

## 🐛 Errores Corregidos

### Error 1: Path Syntax Javalin 4 vs 5

**Problema:**
```
IllegalArgumentException: Path '/obtener_entidad/:id' invalid - 
Javalin 4 switched from ':param' to '{param}'
```

**Solución:**
```java
// ❌ Incorrecto (Javalin 4)
app.get("/obtener_entidad/:id", this::obtenerEntidadPorId);

// ✅ Correcto (Javalin 5)
app.get("/obtener_entidad/{id}", this::obtenerEntidadPorId);
```

---

### Error 2: Object Mapper No Configurado

**Problema:**
```
WARN io.javalin.Javalin - It looks like you don't have an 
object mapper configured
```

**Solución:** Agregar a pom.xml:
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.0</version>
</dependency>
```

---

### Error 3: SQLFeatureNotSupportedException

**Problema:**
```
java.sql.SQLFeatureNotSupportedException: not implemented by SQLite JDBC driver
at org.sqlite.jdbc3.JDBC3Statement.getGeneratedKeys
```

**Solución:** Usar `last_insert_rowid()` de SQLite:

```java
// ❌ Incorrecto para SQLite
try (ResultSet rs = pstmt.getGeneratedKeys()) {
    if (rs.next()) {
        long id = rs.getLong(1);
    }
}

// ✅ Correcto para SQLite
String sqlLastId = "SELECT last_insert_rowid() as id";
try (PreparedStatement pstmtLastId = conn.prepareStatement(sqlLastId);
     ResultSet rs = pstmtLastId.executeQuery()) {
    if (rs.next()) {
        long id = rs.getLong("id");
    }
}
```

---

### Error 4: ParseException en ApiService

**Problema:**
```
unreported exception org.apache.hc.core5.http.ParseException
```

**Solución:**
```java
public String crearEntidad(String jsonBody) 
    throws IOException, ParseException {  // ← Agregar ParseException
    // ...
}
```

---

## 🔧 Cómo Extender

### Agregar Nueva Entidad - Paso a Paso

#### Paso 1: Crear clase Entidad
```java
// entidad/MiEntidad.java
public class MiEntidad {
    private Long id;
    private String atributo1;
    
    public MiEntidad() {}
    public MiEntidad(Long id, String atributo1) {
        this.id = id;
        this.atributo1 = atributo1;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAtributo1() { return atributo1; }
    public void setAtributo1(String atributo1) { this.atributo1 = atributo1; }
}
```

#### Paso 2: Crear tabla en DatabaseConnection
```java
public static void inicializarDB() {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {
        
        String sqlMiEntidad = "CREATE TABLE IF NOT EXISTS mi_entidad (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "atributo1 TEXT NOT NULL," +
                "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        stmt.execute(sqlMiEntidad);
    }
}
```

#### Paso 3: Agregar endpoints en Controlador
```java
public void registrarRutas(Javalin app) {
    app.post("/mi_entidad", this::crearMiEntidad);
    app.get("/mis_entidades", this::obtenerMisEntidades);
}

private void crearMiEntidad(Context ctx) {
    try {
        MiEntidad miEntidad = gson.fromJson(ctx.body(), MiEntidad.class);
        // INSERT y retornar
        ctx.status(201).json(Map.of("entidad", miEntidad));
    } catch (Exception e) {
        ctx.status(500).json(Map.of("error", e.getMessage()));
    }
}
```

#### Paso 4: Agregar métodos en ApiService (Frontend)
```java
public String crearMiEntidad(String jsonBody) 
    throws IOException, ParseException {
    HttpPost request = new HttpPost(BASE_URL + "/mi_entidad");
    request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
    
    try (CloseableHttpResponse response = httpClient.execute(request)) {
        String responseBody = EntityUtils.toString(response.getEntity());
        Object json = gson.fromJson(responseBody, Object.class);
        return gson.toJson(json);
    }
}
```

---

## 📊 Base de Datos

**Ubicación:** `sismos.db` (raíz del proyecto)

**Inspeccionar BD:**
- Descargar [SQLite Browser](https://sqlitebrowser.org/)
- Abrir `sismos.db`
- Ver datos en tiempo real

**Para recrear BD:**
```cmd
del sismos.db
```
Se recreará automáticamente al iniciar el backend.

---

## 📞 Referencias Rápidas

| Comando | Descripción |
|---------|------------|
| `run-backend.bat` | Inicia servidor en puerto 8080 |
| `run-frontend.bat` | Inicia GUI de escritorio |
| `mvn clean compile` | Compila solo |
| `mvn clean package` | Compila y empaqueta |
| `mvn exec:java` | Ejecuta Main |
| `del sismos.db` | Elimina BD (se recrea) |

---

**Última actualización:** Octubre 2025
**Versión:** 1.0.0 - Estable

