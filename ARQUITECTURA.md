# Arquitectura del Sistema - PPAI Sismos

## 🏗️ Estructura de Capas

```
┌──────────────────────────────────────────────────────────┐
│                    CLIENTE (Frontend)                    │
│         Swing JFrame - Interfaz de Escritorio            │
│  - MainFrame.java (GUI)                                  │
│  - ApiService.java (Cliente HTTP)                        │
└────────────────────────┬─────────────────────────────────┘
                         │
                    HTTP/JSON
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                   SERVIDOR (Backend)                     │
│              Javalin 5.x - API REST                      │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  CAPA DE PRESENTACIÓN                           │   │
│  │  ControladorEjemplo.java                        │   │
│  │  - Recibe peticiones HTTP (POST, GET)           │   │
│  │  - Parsea JSON a objetos                        │   │
│  │  - Valida datos de entrada                      │   │
│  │  - Retorna respuestas HTTP/JSON                 │   │
│  └─────────────────────┬──────────────────────────┘   │
│                        │                               │
│  ┌──────────────────────▼──────────────────────────┐   │
│  │  CAPA DE LÓGICA DE NEGOCIO                      │   │
│  │  Gestor.java                                    │   │
│  │  - Orquesta colaboraciones entre objetos        │   │
│  │  - Implementa reglas de negocio                 │   │
│  │  - Coordina operaciones complejas               │   │
│  └─────────────────────┬──────────────────────────┘   │
│                        │                               │
│  ┌──────────────────────▼──────────────────────────┐   │
│  │  CAPA DE PERSISTENCIA                           │   │
│  │  DatabaseConnection.java                        │   │
│  │  - Maneja conexiones SQL                        │   │
│  │  - CRUD en BD                                   │   │
│  │  - Inicializa BD automáticamente                │   │
│  └─────────────────────┬──────────────────────────┘   │
│                        │                               │
│  ┌──────────────────────▼──────────────────────────┐   │
│  │  CAPA DE DOMINIO                                │   │
│  │  EntidadEjemplo.java                            │   │
│  │  - POJOs (Plain Old Java Objects)               │   │
│  │  - Propiedades: id, nombre, fecha_creacion      │   │
│  └─────────────────────┬──────────────────────────┘   │
│                        │                               │
└────────────────────────┬───────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│              Base de Datos SQLite                        │
│                  sismos.db                              │
│                                                          │
│  Tabla: entidad_ejemplo                                 │
│  - id (INTEGER PRIMARY KEY AUTOINCREMENT)               │
│  - nombre (TEXT NOT NULL)                               │
│  - fecha_creacion (TIMESTAMP)                           │
└──────────────────────────────────────────────────────────┘
```

---

## 📦 Responsabilidades por Componente

### 1️⃣ ControladorEjemplo (Capa de Presentación)

**Ubicación:** `backend/src/main/java/com/ppai/app/controlador/ControladorEjemplo.java`

**Responsabilidades:**
- ✅ Registrar rutas HTTP (endpoints)
- ✅ Recibir peticiones REST (POST, GET, PUT, DELETE)
- ✅ Parsear JSON del request a objetos Java
- ✅ Validar datos básicos de entrada
- ✅ Llamar al Gestor para lógica de negocio
- ✅ Manejar excepciones y retornar códigos HTTP apropiados
- ✅ Serializar objetos a JSON para la respuesta

**NO debe hacer:**
- ❌ Lógica de negocio compleja
- ❌ Acceder directamente a la base de datos
- ❌ Transacciones complejas

**Endpoints implementados:**
```java
POST /crear_entidad        → crearEntidad()
GET  /obtener_entidades    → obtenerEntidades()
GET  /obtener_entidad/{id} → obtenerEntidadPorId()
```

---

### 2️⃣ Gestor (Capa de Lógica de Negocio)

**Ubicación:** `backend/src/main/java/com/ppai/app/gestor/Gestor.java`

**Responsabilidades:**
- ✅ Orquestar colaboraciones entre objetos
- ✅ Implementar reglas de negocio
- ✅ Coordinar operaciones complejas
- ✅ Validaciones de negocio
- ✅ Transacciones y flujos de trabajo

**Patrón:** Aquí implementas tu patrón arquitectónico específico (Strategy, State, Observer, etc.)

**NO debe hacer:**
- ❌ Manejar HTTP
- ❌ Acceder directamente a BD (delegar a DAO)

---

### 3️⃣ DatabaseConnection (Capa de Persistencia)

**Ubicación:** `backend/src/main/java/com/ppai/app/datos/DatabaseConnection.java`

**Responsabilidades:**
- ✅ Crear y mantener conexiones SQLite
- ✅ Inicializar base de datos automáticamente
- ✅ Crear tablas si no existen
- ✅ Proporcionar métodos para obtener conexiones
- ✅ Cerrar conexiones correctamente

**Métodos públicos:**
```java
public static Connection getConnection() throws SQLException
public static void inicializarDB()
public static void cerrarConexion()
```

**Características SQLite:**
- Base de datos de archivo embebido (no necesita servidor)
- Ubicación: `sismos.db` en la raíz del proyecto
- Inicialización automática en `Main.java`

---

### 4️⃣ EntidadEjemplo (Capa de Dominio)

**Ubicación:** `backend/src/main/java/com/ppai/app/entidad/EntidadEjemplo.java`

**Responsabilidades:**
- ✅ Representar modelo de datos
- ✅ Proporcionar getters/setters
- ✅ Mapear a/desde JSON
- ✅ Ser serializable

**Propiedades:**
```java
Long id                  // ID único, autogenerado
String nombre            // Nombre de la entidad
```

---

## 🔄 Flujo de una Solicitud

```
1. Cliente (Frontend)
   │
   └──> HTTP POST /crear_entidad
        Body: {"nombre": "Mi Entidad"}
        
2. ControladorEjemplo.crearEntidad()
   │
   ├──> Parsea JSON → EntidadEjemplo
   ├──> Valida datos (nombre no vacío)
   └──> Llama gestor.crear(entidad)
   
3. Gestor.crear()
   │
   ├──> Aplica lógica de negocio
   └──> Llamaa DatabaseConnection.insert()
   
4. DatabaseConnection
   │
   ├──> INSERT INTO entidad_ejemplo (nombre) VALUES (?)
   ├──> SELECT last_insert_rowid() → id
   └──> Retorna EntidadEjemplo con ID
   
5. Controlador retorna respuesta
   │
   ├──> Status 201 (Created)
   └──> Body: {"mensaje": "...", "entidad": {...}}
   
6. Cliente recibe respuesta
   │
   └──> Muestra en área de texto (JSON formateado)
```

---

## 📡 Flujo Frontend-Backend

```
┌──────────────────────┐
│   Interfaz Swing     │
│   (MainFrame.java)   │
└──────────┬───────────┘
           │
           │ User Click
           ▼
┌──────────────────────┐
│ Event Handler        │
│ crearEntidad()       │
│ obtenerEntidades()   │
└──────────┬───────────┘
           │
           │ Crea JsonObject
           ▼
┌──────────────────────┐
│ ApiService           │
│ .crearEntidad(json)  │
│ .obtenerEntidades()  │
└──────────┬───────────┘
           │
           │ HTTP Request
           │ (POST/GET)
           ▼
     [NETWORK]
           │
           │ HTTP Response
           ▼
┌──────────────────────┐
│ Backend API          │
│ (Javalin)            │
└──────────┬───────────┘
           │
           │ Retorna JSON
           ▼
┌──────────────────────┐
│ ApiService parses    │
│ & returns String     │
└──────────┬───────────┘
           │
           │ Actualiza UI
           ▼
┌──────────────────────┐
│ txtResultados        │
│ (JTextArea)          │
│ Muestra resultado    │
└──────────────────────┘
```

---

## 🎯 Patrones y Principios

### SOLID Principles
- **S (Single Responsibility):** Cada clase tiene una responsabilidad
- **O (Open/Closed):** Abierto para extensión, cerrado para modificación
- **L (Liskov Substitution):** Las entidades pueden reemplazarse sin problemas
- **I (Interface Segregation):** Interfaces específicas, no genéricas
- **D (Dependency Inversion):** Depender de abstracciones, no implementaciones

### Arquitectura de Capas
- Cada capa tiene responsabilidades claras
- Las capas superiores dependen de las inferiores
- Las capas pueden reutilizarse independientemente

---

## 🔧 Extensibilidad

### Agregar Nueva Entidad

1. **Crear clase entidad:**
   ```java
   // entidad/MiEntidad.java
   public class MiEntidad {
       private Long id;
       private String propiedad1;
       // getters/setters
   }
   ```

2. **Crear tabla en DatabaseConnection:**
   ```java
   // En inicializarDB()
   String sqlMiTabla = "CREATE TABLE IF NOT EXISTS mi_tabla (...)";
   stmt.execute(sqlMiTabla);
   ```

3. **Agregar métodos en Gestor:**
   ```java
   public MiEntidad crearMiEntidad(MiEntidad obj) { ... }
   public List<MiEntidad> obtenerMiEntidades() { ... }
   ```

4. **Agregar endpoints en Controlador:**
   ```java
   app.post("/mi_entidad", this::crearMiEntidad);
   app.get("/mis_entidades", this::obtenerMisEntidades);
   ```

5. **Agregar métodos en ApiService (Frontend):**
   ```java
   public String crearMiEntidad(String json) throws IOException { ... }
   public String obtenerMisEntidades() throws IOException { ... }
   ```

---

## 📊 Tecnologías por Capa

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Presentación Backend | Javalin | 5.6.3 |
| Presentación Frontend | Swing/AWT | JDK 17 |
| Serialización | Gson/Jackson | 2.10.1/2.15.0 |
| HTTP Cliente | Apache HttpClient5 | 5.2.1 |
| Base de Datos | SQLite | - |
| Driver JDBC | sqlite-jdbc | 3.43.0.0 |
| Logging | SLF4J | 2.0.9 |

---

## 🔒 Consideraciones de Seguridad

### Actualmente Implementado
- ✅ Validación de entrada (nombre no vacío)
- ✅ Manejo de excepciones
- ✅ CORS habilitado

### Recomendaciones para Producción
- 🔲 Agregar autenticación/autorización
- 🔲 Validar y sanitizar todas las entradas
- 🔲 Usar HTTPS en lugar de HTTP
- 🔲 Implementar rate limiting
- 🔲 Agregar logging de seguridad
- 🔲 Validar tipos de datos en base de datos

---

## 📈 Escalabilidad

### Limitaciones Actuales
- SQLite es de archivo, no ideal para aplicaciones concurrentes
- No hay caché
- No hay índices en BD

### Mejoras Futuras
- Migrar a PostgreSQL/MySQL para producción
- Agregar caché (Redis)
- Implementar índices en BD
- Agregar paginación en endpoints GET
- Implementar async/await para operaciones I/O

---

## 📚 Documentación Adicional

- **README.md** - Visión general del proyecto
- **INICIO_RAPIDO.md** - Cómo empezar rápidamente
- **GUIA_IMPLEMENTACION.md** - Guía técnica detallada


- ✅ Validar datos de negocio
- ✅ Coordinar colaboraciones entre objetos
- ✅ Aplicar reglas de negocio
- ✅ Delegar persistencia al DAO
- ❌ NO sabe de HTTP/JSON
- ❌ NO ejecuta SQL directamente

**Archivo**: `backend/src/main/java/com/ppai/app/gestor/Gestor.java`

### 3. DAO (Data Access Object)
- ✅ Ejecutar consultas SQL
- ✅ Mapear ResultSet a objetos
- ✅ CRUD básico
- ✅ Gestionar conexiones
- ❌ NO tiene validaciones de negocio
- ❌ NO sabe de HTTP

**Archivo**: `backend/src/main/java/com/ppai/app/datos/`

### 4. Entidad
- ✅ Contener datos
- ✅ Getters/Setters
- ✅ Métodos de utilidad simples
- ❌ NO tiene lógica de negocio compleja
- ❌ NO accede a la BD

**Archivo**: `backend/src/main/java/com/ppai/app/entidad/`

## Flujo de Trabajo Típico

1. **Cliente (Frontend)** hace una petición HTTP
2. **Controlador** recibe la petición, parsea el JSON
3. **Gestor** aplica lógica de negocio (aquí usas tu patrón)
4. **DAO** persiste/consulta en la base de datos
5. **Controlador** retorna la respuesta en JSON
6. **Cliente** muestra los datos en la interfaz

## Comunicación Frontend-Backend

```
┌──────────────┐                 ┌──────────────┐
│   Frontend   │   HTTP/JSON     │   Backend    │
│   (JFrame)   │ ◄─────────────► │  (Javalin)   │
│              │                 │              │
│  MainFrame   │                 │  Controller  │
│      │       │                 │      │       │
│      ▼       │                 │      ▼       │
│  ApiService  │                 │    Gestor    │
└──────────────┘                 │      │       │
                                 │      ▼       │
                                 │     DAO      │
                                 │      │       │
                                 │      ▼       │
                                 │   SQLite     │
                                 └──────────────┘
```

## Archivos Clave para Modificar

- `backend/src/main/java/com/ppai/app/gestor/Gestor.java` - **Implementa aquí tu patrón**
- `backend/src/main/java/com/ppai/app/entidad/` - Define tus entidades de dominio
- `backend/src/main/java/com/ppai/app/datos/` - Crea tus DAOs
- `backend/src/main/java/com/ppai/app/controlador/` - Define tus endpoints REST
- `frontend/src/main/java/com/ppai/app/frontend/gui/MainFrame.java` - Diseña tu interfaz
