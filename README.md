# Proyecto PPAI Sismos - Java 17 + Javalin + SQLite + JFrame Desktop

Sistema de gestión de entidades con arquitectura de capas, API REST y frontend de escritorio.

## 📋 Requisitos

- **Java 17** o superior
- **Maven 3.6+** (incluido Maven Wrapper `mvnw.cmd`, no necesita instalación global)
- **Windows** (los scripts .bat están optimizados para Windows)
- Navegador web (para testing de endpoints)

## 📦 Dependencias Principales

### Backend
- **Javalin 5.6.3** - Framework HTTP ligero
- **SQLite JDBC 3.43.0.0** - Driver SQLite
- **Gson 2.10.1** - Parsing JSON
- **Jackson DataBind 2.15.0** - Object mapper para Javalin
- **SLF4J 2.0.9** - Logging

### Frontend
- **Swing/AWT** - Framework GUI de Java (incluido en JDK)
- **Gson 2.10.1** - Parsing JSON
- **Apache HttpClient 5.2.1** - Cliente HTTP

## 🏗️ Estructura del Proyecto

```
PPAI_SISMOS/
├── backend/
│   ├── src/main/java/com/ppai/app/
│   │   ├── Main.java                          # Servidor principal - Inicializa BD y rutas
│   │   ├── controlador/
│   │   │   └── ControladorEjemplo.java        # Endpoints REST (POST, GET)
│   │   ├── gestor/
│   │   │   └── Gestor.java                    # Gestor de lógica de negocio
│   │   ├── entidad/
│   │   │   └── EntidadEjemplo.java            # Modelo de datos (POJO)
│   │   └── datos/
│   │       └── DatabaseConnection.java        # Conexión y inicialización SQLite ✨ NUEVO
│   ├── pom.xml                                # Configuración Maven con dependencias
│   ├── mvnw.cmd                               # Maven Wrapper para Windows
│   └── target/                                # Archivos compilados y JAR
│
├── frontend/
│   ├── src/main/java/com/ppai/app/frontend/
│   │   ├── gui/
│   │   │   └── MainFrame.java                 # Ventana principal Swing ✨ NUEVO
│   │   ├── model/
│   │   │   └── ModeloEjemplo.java             # Modelo de datos
│   │   └── service/
│   │       └── ApiService.java                # Cliente HTTP ✨ ACTUALIZADO
│   ├── pom.xml                                # Configuración Maven
│   ├── mvnw.cmd                               # Maven Wrapper para Windows
│   └── target/                                # Archivos compilados y JAR
│
├── sismos.db                                  # Base de datos SQLite (creada automáticamente)
├── run-backend.bat                            # Script para ejecutar backend
├── run-frontend.bat                           # Script para ejecutar frontend
│
├── README.md                                  # Este archivo
├── INICIO_RAPIDO.md                           # Guía de inicio rápido
├── ARQUITECTURA.md                            # Documentación de arquitectura
└── GUIA_IMPLEMENTACION.md                     # Guía técnica detallada
```

## 🚀 Cómo Ejecutar

### Opción 1: Scripts Batch (RECOMENDADO)

**Terminal 1 - Iniciar Backend:**
```cmd
run-backend.bat
```
Esperarás este mensaje:
```
✓ Base de datos inicializada correctamente
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Server started on http://localhost:8080
```

**Terminal 2 - Iniciar Frontend (cuando el backend esté listo):**
```cmd
run-frontend.bat
```
Se abrirá la ventana de la aplicación de escritorio.

### Opción 2: Maven Manual

**Backend:**
```cmd
cd backend
mvnw.cmd clean package
mvnw.cmd exec:java
```

**Frontend:**
```cmd
cd frontend
mvnw.cmd clean package
mvnw.cmd exec:java
```

## 🔌 Endpoints API REST

### POST /crear_entidad
Crea una nueva entidad en la base de datos.

**Request:**
```bash
curl -X POST http://localhost:8080/crear_entidad \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Sismo del Este"}'
```

**Response (201):**
```json
{
  "mensaje": "Entidad creada exitosamente",
  "entidad": {
    "id": 1,
    "nombre": "Sismo del Este"
  }
}
```

### GET /obtener_entidades
Obtiene todas las entidades registradas.

**Request:**
```bash
curl http://localhost:8080/obtener_entidades
```

**Response (200):**
```json
{
  "cantidad": 2,
  "entidades": [
    {"id": 2, "nombre": "Segunda Entidad"},
    {"id": 1, "nombre": "Primera Entidad"}
  ]
}
```

### GET /obtener_entidad/{id}
Obtiene una entidad específica por ID.

**Request:**
```bash
curl http://localhost:8080/obtener_entidad/1
```

**Response (200):**
```json
{
  "id": 1,
  "nombre": "Sismo del Este"
}
```

**Response (404):**
```json
{
  "error": "Entidad no encontrada"
}
```

## 📊 Base de Datos SQLite

**Ubicación:** `sismos.db` (se crea automáticamente en la raíz del proyecto)

**Tabla `entidad_ejemplo`:**
```sql
CREATE TABLE entidad_ejemplo (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

Para inspeccionar la BD, usa una herramienta como:
- [SQLite Browser](https://sqlitebrowser.org/)
- [DBeaver Community](https://dbeaver.io/)

## 🎨 Interfaz Frontend

La aplicación de escritorio incluye:
- **Campo de texto** para ingresar nombre de entidad
- **Botón "Crear Entidad"** - Envía POST al backend
- **Botón "Obtener Entidades"** - Recupera todas las entidades
- **Área de texto** - Muestra respuestas JSON formateadas

## ⚙️ Configuración

### CORS
CORS está habilitado en el backend automáticamente (configurado en Javalin).

### Puerto
El backend corre en **puerto 8080**. Si necesitas cambiar:
1. Edita `backend/src/main/java/com/ppai/app/Main.java`
2. Modifica la constante `PORT`

### Base de Datos
La URL de conexión SQLite es: `jdbc:sqlite:sismos.db`

Para cambiar la ubicación:
1. Edita `backend/src/main/java/com/ppai/app/datos/DatabaseConnection.java`
2. Modifica la constante `DB_URL`

## 🐛 Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `Port 8080 already in use` | Otro proceso usa el puerto | Cambia el puerto en Main.java |
| `ClassNotFoundException: MainFrame` | Frontend no compilado correctamente | Ejecuta `mvn clean compile` en frontend |
| `Connection refused` | Backend no está corriendo | Inicia backend con `run-backend.bat` |
| `SQLFeatureNotSupportedException` | Driver SQLite antiguo | Actualiza sqlite-jdbc a 3.43.0.0+ |

## 🎯 Características Implementadas

✅ Backend con API REST (Javalin 5.x)
✅ Base de datos SQLite con inicialización automática
✅ Endpoints POST/GET para crear y obtener entidades
✅ Frontend de escritorio con JFrame (Swing)
✅ Comunicación HTTP cliente-servidor
✅ Manejo robusto de excepciones
✅ CORS habilitado
✅ JSON serialization con Gson
✅ Object mapper Jackson para Javalin
✅ Compatible con Java 17+

## 📝 Próximos Pasos

- [ ] Implementar endpoints DELETE y PUT
- [ ] Agregar validaciones más robustas
- [ ] Crear más entidades según tu dominio
- [ ] Implementar autenticación/autorización
- [ ] Agregar pruebas unitarias
- [ ] Crear documentación de tu patrón arquitectónico específico

## 👨‍💻 Estructura de Desarrollo Recomendada

Para agregar nuevas funcionalidades:

1. **Crear nueva entidad** en `entidad/`
2. **Crear DAO/Repositorio** en `datos/`
3. **Agregar métodos en Gestor** para lógica de negocio
4. **Crear endpoints** en `ControladorEjemplo`
5. **Agregar métodos en ApiService** para frontend

## 📞 Soporte

Para más información:
- Ver `INICIO_RAPIDO.md` para pasos básicos
- Ver `ARQUITECTURA.md` para diseño del sistema
- Ver `GUIA_IMPLEMENTACION.md` para detalles técnicos

---

**Última actualización:** Octubre 2025
**Versión:** 1.0.0


### Opción 2: Desde NetBeans

#### Backend:
1. Abrir el proyecto `backend` en NetBeans
2. Hacer clic derecho en `Main.java`
3. Seleccionar "Run File"

#### Frontend:
1. Abrir el proyecto `frontend` en NetBeans
2. Hacer clic derecho en `MainFrame.java`
3. Seleccionar "Run File"

## 🔧 Tecnologías Incluidas

| Componente | Tecnología | Versión |
|------------|------------|---------|
| Java | OpenJDK | 17 |
| Servidor HTTP | Javalin | 5.6.3 |
| Base de datos | SQLite JDBC | 3.43.0 |
| JSON | Gson | 2.10.1 |
| HTTP Client | Apache HttpClient | 5.2.1 |
| UI | Java Swing | Built-in |

### 📖 Documentación Interactiva con Swagger

- **Swagger UI**: `http://localhost:8080/swagger-ui`
  
- **ReDoc**: `http://localhost:8080/redoc`
  - Documentación alternativa con un diseño más limpio
  - Solo lectura (no permite probar endpoints)

Cuando crees tus propios endpoints, usa las anotaciones `@OpenApi` para documentarlos automáticamente.
