# 🏗️ Arquitectura del Sistema - PPAI Red Sísmica

## ⚙️ Descripción General

El sistema **PPAI Red Sísmica** es una aplicación **de escritorio
local** desarrollada en **Java (Swing)**, que permite la **revisión
manual de eventos sísmicos** detectados automáticamente por la red de
estaciones.

Toda la lógica (persistencia, dominio y presentación) se ejecuta **en un
mismo proceso local**, sin necesidad de servidor HTTP.\
El flujo principal se inicia desde `Main.java`, que: 1. Inicializa la
base de datos SQLite embebida.\
2. Carga el contexto de dominio (`Contexto`).\
3. Inicia la interfaz gráfica (`PantallaRevisionManual`).

------------------------------------------------------------------------

## 🧩 Arquitectura en Capas

    ┌────────────────────────────────────────────────────────────┐
    │               CAPA DE PRESENTACIÓN (FRONTEND)              │
    │             Swing - Interfaz de Escritorio (GUI)           │
    │                                                            │
    │  - PantallaRevisionManual.java                             │
    │  - Interfaces gráficas e interacción con el usuario         │
    │  - Comunicación directa con GestorRevisionManual            │
    └─────────────────────────────┬───────────────────────────────┘
                                  │
                                  ▼
    ┌────────────────────────────────────────────────────────────┐
    │               CAPA DE LÓGICA DE NEGOCIO                    │
    │                                                            │
    │  - GestorRevisionManual.java                               │
    │  - Orquesta la interacción entre la pantalla y el dominio  │
    │  - Implementa el flujo del caso de uso “CU23 - Revisión    │
    │    Manual de Eventos Sísmicos”                             │
    │  - Aplica reglas de negocio y valida datos                 │
    └─────────────────────────────┬───────────────────────────────┘
                                  │
                                  ▼
    ┌────────────────────────────────────────────────────────────┐
    │               CAPA DE PERSISTENCIA (DATOS)                 │
    │                                                            │
    │  - DatabaseConnection.java                                 │
    │  - Clases DAO (Data Access Object)                         │
    │  - Gestión de conexión SQLite                              │
    │  - Inserción de datos iniciales y creación automática de   │
    │    tablas al iniciar el sistema                            │
    └─────────────────────────────┬───────────────────────────────┘
                                  │
                                  ▼
    ┌────────────────────────────────────────────────────────────┐
    │                 CAPA DE DOMINIO (MODELO)                   │
    │                                                            │
    │  - EventoSismico.java                                      │
    │  - Usuario.java                                            │
    │  - Sismografo.java                                         │
    │  - TipoDeDato.java                                         │
    │  - SerieTemporal.java                                      │
    │  - Estado y subclases (Detectado, Confirmado, etc.)        │
    │                                                            │
    │  Representa los objetos del dominio y su comportamiento.   │
    └────────────────────────────────────────────────────────────┘

------------------------------------------------------------------------

## 🧠 Flujo Principal del Sistema

    Main.java
       │
       ├──▶ DatabaseConnection.inicializarDB()
       │       └── Crea tablas e inserta datos iniciales
       │
       ├──▶ Contexto()
       │       └── Carga objetos de dominio (usuarios, eventos, etc.)
       │
       └──▶ PantallaRevisionManual(contexto)
               └──▶ Crea GestorRevisionManual(this, eventos, usuario)
                       └──▶ Ejecuta flujo de revisión manual

------------------------------------------------------------------------

## 🖥️ Interfaz Gráfica (Swing)

**Clase principal:** `PantallaRevisionManual`

Responsabilidades: - Mostrar los eventos sísmicos no revisados.\
- Permitir seleccionar un evento para revisión.\
- Interactuar con el `GestorRevisionManual` para ejecutar la revisión.\
- Mostrar los resultados dentro de la propia ventana (tabla).

**Ejemplo visual (interfaz actual mejorada):**

    ┌──────────────────────────────────────────────┐
    │ CU23 - Revisión Manual de Eventos Sísmicos   │
    ├──────────────────────────────────────────────┤
    │ [Ejecutar Caso de Uso]                       │
    ├──────────────────────────────────────────────┤
    │ Fecha/Hora         | Latitud | Longitud | ... │
    │──────────────────────────────────────────────│
    │ 2025-04-01 10:00   | -31.1   | -65.2    | ... │
    │ 2025-04-02 11:15   | -31.3   | -65.4    | ... │
    │ 2025-04-03 09:30   | -31.4   | -65.6    | ... │
    └──────────────────────────────────────────────┘

------------------------------------------------------------------------

## 🧩 Detalle por Componente

### 1️⃣ `Main.java`

-   Punto de entrada del sistema.\
-   Inicializa la base de datos y el contexto.\
-   Lanza la interfaz Swing.

### 2️⃣ `DatabaseConnection.java`

-   Gestiona la conexión SQLite (`sismos.db`).\
-   Crea tablas automáticamente si no existen.\
-   Inserta datos iniciales (estados, usuarios, eventos).\
-   Ofrece métodos para obtener `Connection` y ejecutar scripts SQL.

### 3️⃣ `Contexto.java`

-   Carga los datos del dominio desde la base de datos.\
-   Mantiene referencias globales a listas de entidades
    (`eventosSismicos`, `usuarios`, etc.).\
-   Se pasa como dependencia a la pantalla principal.

### 4️⃣ `PantallaRevisionManual.java`

-   Ventana Swing (`JFrame`) principal del caso de uso CU23.\
-   Contiene un botón principal **"Ejecutar Caso de Uso"**.\
-   Al presionarlo, solicita al `GestorRevisionManual` que ejecute la
    revisión.\
-   Muestra los eventos en una **tabla integrada**, evitando pop-ups.

### 5️⃣ `GestorRevisionManual.java`

-   Controla el flujo de revisión de eventos no revisados.\
-   Interactúa con la `PantallaRevisionManual` para mostrar resultados.\
-   Aplica validaciones y delega al dominio las operaciones
    específicas.\
-   Puede acceder a DAOs para actualizar estados.

### 6️⃣ Entidades de Dominio

-   `EventoSismico` → datos del evento (fecha, ubicación, magnitud,
    estado).\
-   `Usuario` → usuario autenticado o actual.\
-   `Sismografo`, `SerieTemporal`, `MuestraSismica` → entidades técnicas
    del dominio.\
-   `Estado` → patrón State para representar fases de revisión
    (Detectado, Confirmado, Rechazado, etc.).

------------------------------------------------------------------------

## 📦 Responsabilidades por Capa

  --------------------------------------------------------------------------------
  Capa           Responsabilidad Principal              Clases Destacadas
  -------------- -------------------------------------- --------------------------
  Presentación   Mostrar interfaz, recibir acciones del `PantallaRevisionManual`
                 usuario                                

  Lógica de      Implementar flujo del caso de uso      `GestorRevisionManual`
  Negocio                                               

  Persistencia   Acceso a BD, creación y carga de datos `DatabaseConnection`, DAOs

  Dominio        Representación de entidades y          `EventoSismico`,
                 comportamiento                         `Usuario`, `Estado`, etc.
  --------------------------------------------------------------------------------

------------------------------------------------------------------------

## 🧮 Diagrama Simplificado de Colaboración

    ┌──────────────────────────────────────┐
    │          PantallaRevisionManual      │
    │   (Interfaz Swing)                   │
    └──────────────┬───────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────┐
    │        GestorRevisionManual          │
    │   (Lógica del Caso de Uso)           │
    └──────────────┬───────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────┐
    │             Contexto                 │
    │   (Repositorio en memoria)           │
    └──────────────┬───────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────┐
    │         DatabaseConnection           │
    │     (Acceso y carga SQLite)          │
    └──────────────────────────────────────┘

------------------------------------------------------------------------

## 🎯 Principios de Diseño Aplicados

-   **SRP (Single Responsibility):** cada clase tiene una función
    única.\
-   **Bajo acoplamiento:** las capas se comunican por interfaces o
    servicios.\
-   **Alta cohesión:** cada capa agrupa responsabilidades afines.\
-   **Separación de Concerns:** GUI, lógica y persistencia están
    desacopladas.\
-   **Inversión de Dependencias:** el gestor depende de interfaces
    (`IPantallaRevisionManual`) y no de implementaciones concretas.

------------------------------------------------------------------------

## 🧱 Extensibilidad

Para agregar un nuevo caso de uso:

1.  Crear una nueva **Pantalla** (`PantallaNuevoCasoUso`).\
2.  Crear un **Gestor** asociado (`GestorNuevoCasoUso`).\
3.  Reutilizar el `Contexto` y los DAOs para acceder a datos.\
4.  Añadir la inicialización en `Main.java`.

------------------------------------------------------------------------

## 🪄 Tecnologías Utilizadas

  -----------------------------------------------------------------------
  Componente              Tecnología              Descripción
  ----------------------- ----------------------- -----------------------
  GUI                     Java Swing              Interfaz de escritorio
                                                  local

  Persistencia            SQLite                  Base de datos embebida

  ORM manual              JDBC                    Acceso a BD mediante
                                                  DAOs

  Dominio                 Java puro               POJOs y patrón State

  Logging/Consola         ANSI + System.out       Colores y estructura
                                                  con `ConsolaSistema`
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 🔒 Consideraciones

-   El sistema es **local**, sin servidor ni cliente web.\
-   Puede ejecutarse directamente con `run-sistema.bat`.\
-   Los datos se regeneran automáticamente al iniciar.\
-   El flujo principal se centra en el caso de uso **CU23 - Registrar Resultado de Revision Manual**.
