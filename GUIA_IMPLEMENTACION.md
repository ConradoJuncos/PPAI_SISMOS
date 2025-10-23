# Guía de Uso - PPAI Sismos

## ✅ Cambios Realizados

### 1. Backend (Java 17 + Javalin + SQLite)

#### Nuevos Archivos:
- **DatabaseConnection.java**: Clase para manejar conexiones SQLite
  - Inicializa la base de datos automáticamente
  - Crea tabla `entidad_ejemplo` con campos: `id`, `nombre`, `fecha_creacion`
  - Proporciona métodos para obtener conexiones y cerrarlas correctamente

#### Endpoints Disponibles:

**POST /crear_entidad**
- Crea una nueva EntidadEjemplo en la base de datos
- Body (JSON):
```json
{
  "nombre": "Mi Entidad"
}
```
- Response exitosa (201):
```json
{
  "mensaje": "Entidad creada exitosamente",
  "entidad": {
    "id": 1,
    "nombre": "Mi Entidad"
  }
}
```

**GET /obtener_entidades**
- Obtiene todas las entidades registradas
- Response:
```json
{
  "cantidad": 2,
  "entidades": [
    {"id": 2, "nombre": "Segunda Entidad"},
    {"id": 1, "nombre": "Primera Entidad"}
  ]
}
```

**GET /obtener_entidad/:id**
- Obtiene una entidad específica por ID
- Response:
```json
{
  "id": 1,
  "nombre": "Mi Entidad"
}
```

### 2. Frontend (JFrame Desktop)

#### Nuevos Archivos:
- **MainFrame.java**: Ventana principal con interfaz gráfica
  - Campo de texto para ingresar nombre
  - Botón "Crear Entidad" - envía POST al backend
  - Botón "Obtener Entidades" - recupera todas las entidades
  - Área de texto para visualizar resultados en JSON formateado

#### Cambios en ApiService.java:
- Implementados métodos: `crearEntidad()`, `obtenerEntidades()`, `obtenerEntidadPorId()`
- Manejo correcto de excepciones
- Respuestas formateadas con JSON pretty-printing

---

## 🚀 Cómo Ejecutar

### Opción 1: Usando los scripts batch

**Iniciar Backend:**
```bash
run-backend.bat
```
El backend estará disponible en: `http://localhost:8080`

**Iniciar Frontend (en otra terminal):**
```bash
run-frontend.bat
```
Se abrirá la ventana de la aplicación de escritorio.

### Opción 2: Usando Maven directamente

**Backend:**
```bash
cd backend
mvn clean package
mvn exec:java
```

**Frontend:**
```bash
cd frontend
mvn clean package
mvn exec:java
```

---

## 📝 Ejemplo de Uso Completo

1. Inicia el backend (http://localhost:8080)
2. Inicia el frontend
3. En la interfaz:
   - Escribe un nombre en el campo de texto (ej: "Sismo del Este")
   - Haz clic en "Crear Entidad"
   - Verás la respuesta JSON con el ID asignado
4. Haz clic en "Obtener Entidades" para ver todas las entidades creadas

---

## 📊 Base de Datos

**Ubicación:** `sismos.db` (se crea automáticamente en el directorio raíz del backend)

**Tabla:** `entidad_ejemplo`
- `id` (INTEGER PRIMARY KEY AUTOINCREMENT)
- `nombre` (TEXT NOT NULL)
- `fecha_creacion` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

---

## 🔧 Estructura del Proyecto

```
PPAI_SISMOS/
├── backend/
│   ├── src/main/java/com/ppai/app/
│   │   ├── Main.java                      (Servidor principal)
│   │   ├── controlador/
│   │   │   └── ControladorEjemplo.java    (Endpoints REST)
│   │   ├── datos/
│   │   │   └── DatabaseConnection.java    (Conexión SQLite) ✨ NUEVO
│   │   ├── entidad/
│   │   │   └── EntidadEjemplo.java        (Modelo de datos)
│   │   └── gestor/
│   │       └── Gestor.java                (Orquestador de colaboraciones)
│   └── pom.xml
│
├── frontend/
│   ├── src/main/java/com/ppai/app/
│   │   ├── frontend/
│   │   │   ├── gui/
│   │   │   │   └── MainFrame.java         (Ventana principal) ✨ NUEVO
│   │   │   ├── model/
│   │   │   │   └── ModeloEjemplo.java
│   │   │   └── service/
│   │   │       └── ApiService.java        (Cliente HTTP) ✨ ACTUALIZADO
│   └── pom.xml
│
└── run-backend.bat / run-frontend.bat
```

---

## ✨ Características Implementadas

✅ Endpoint POST para crear entidades en SQLite
✅ Endpoint GET para obtener todas las entidades
✅ Endpoint GET para obtener entidad por ID
✅ Interfaz JFrame desktop completamente funcional
✅ Comunicación HTTP cliente-servidor
✅ Manejo correcto de excepciones
✅ CORS habilitado en el backend
✅ Base de datos SQLite automáticamente inicializada

---

## 🐛 Errores Corregidos

✅ Error: "Could not find or load main class com.ppai.app.frontend.gui.MainFrame"
  - **Causa:** MainFrame.java estaba vacío
  - **Solución:** Implementada clase MainFrame con interfaz gráfica completa

✅ Error en dependencias Swagger/Javalin OpenAPI
  - **Causa:** Dependencias no disponibles en Maven Central
  - **Solución:** Removidas dependencias innecesarias, implementados endpoints directamente

---

## 📞 Próximos Pasos (Opcional)

- Agregar validación en frontend para campos vacíos
- Implementar eliminación y actualización de entidades
- Agregar tablas adicionales según tu dominio
- Crear más métodos en el Gestor para la lógica de negocio

