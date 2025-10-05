# Arquitectura del Sistema - Estructura Base

## Estructura de Capas Base

```
┌─────────────────────────────────────────┐
│         Controlador REST                │
│     (HTTP/JSON - Endpoints API)         │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│            GESTOR                       │
│  (Aquí implementas tu patrón)           │
│  • Lógica de negocio                    │
│  • Coordinación de colaboraciones       │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│             DAO                         │
│      (Acceso a Datos)                   │
│  • CRUD                                 │
│  • Consultas SQL                        │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│           ENTIDAD                       │
│         (POJOs - Modelo de dominio)     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│        Base de Datos (SQLite)           │
└─────────────────────────────────────────┘
```

## Responsabilidades por Capa

### 1. Controlador
- ✅ Recibir peticiones HTTP
- ✅ Parsear JSON a objetos
- ✅ Delegar al Gestor
- ✅ Retornar respuestas HTTP
- ❌ NO tiene lógica de negocio
- ❌ NO accede directamente a la BD

**Archivo**: `backend/src/main/java/com/ppai/app/controlador/`

### 2. Gestor

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
