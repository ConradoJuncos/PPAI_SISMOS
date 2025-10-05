# Proyecto Base PPAI - Java + SQLite + JFrame

## Requisitos

- Java 17
- Maven (incluido Maven Wrapper, no necesitas instalarlo globalmente)
- NetBeans (opcional, para editar la interfaz gráfica visualmente)

## Estructura del Proyecto

```
PPAI_SISMOS/
├── backend/                  # API REST con Javalin y SQLite
│   ├── src/main/java/com/ppai/app/
│   │   ├── Main.java                    # Clase principal del servidor
│   │   ├── controlador/
│   │   │   └── ControladorEjemplo.java  # Controlador REST
│   │   ├── gestor/
│   │   │   └── Gestor.java              # Gestor base
│   │   ├── entidad/
│   │   │   └── EntidadEjemplo.java      # Entidad de dominio
│   │   └── datos/
│   │       └── DatabaseConnection.java  # Conexión a SQLite
│   ├── pom.xml
│   └── mvnw.cmd                         # Maven Wrapper
│
└── frontend/                 # Aplicación de escritorio Java Swing
    ├── src/main/java/com/ppai/app/frontend/
    │   ├── gui/
    │   │   └── MainFrame.java           # Ventana principal
    │   ├── model/
    │   │   └── ModeloEjemplo.java       # Modelo de datos
    │   └── service/
    │       └── ApiService.java          # Comunicación con backend
    ├── pom.xml
    └── mvnw.cmd                         # Maven Wrapper
```

## 🚀 Cómo Ejecutar

### Opción 1: Scripts Automáticos (Recomendado)

#### 1. Iniciar el Backend
```cmd
run-backend.bat
```

#### 2. Ejecutar el Frontend (en otra terminal)
```cmd
run-frontend.bat
```

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
