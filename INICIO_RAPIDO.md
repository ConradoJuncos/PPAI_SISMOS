# Guía Rápida de Inicio

## ⚡ Inicio en 5 Minutos

### Requisitos Previos
- ✅ Java 17 instalado
- ✅ Estar en la carpeta raíz del proyecto

---

## 🚀 Paso 1: Iniciar el Backend

Abre **CMD o PowerShell** en la carpeta raíz del proyecto y ejecuta:

```cmd
run-backend.bat
```

**Verás esto:**
```
✓ Base de datos inicializada correctamente
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Server started on http://localhost:8080
```

✅ **El backend está listo en `http://localhost:8080`**

> 💡 **Deja esta ventana abierta**, el backend debe seguir corriendo

---

## 🚀 Paso 2: Iniciar el Frontend

**Abre UNA NUEVA terminal** (CMD o PowerShell) en la misma carpeta raíz y ejecuta:

```cmd
run-frontend.bat
```

**Verás:**
```
[Compilando...]
[Ejecutando la aplicación de escritorio...]
```

✅ **Se abrirá la ventana de la aplicación de escritorio**

---

## 📝 Paso 3: Usar la Aplicación

En la ventana de la aplicación de escritorio:

### Crear una Entidad
1. Escribe un nombre en el campo de texto (ej: "Sismo del Este")
2. Haz clic en el botón **"Crear Entidad"**
3. Verás la respuesta JSON con el ID asignado:
```json
{
  "mensaje": "Entidad creada exitosamente",
  "entidad": {
    "id": 1,
    "nombre": "Sismo del Este"
  }
}
```

### Ver Todas las Entidades
1. Haz clic en el botón **"Obtener Entidades"**
2. Verás un JSON con todas las entidades creadas:
```json
{
  "cantidad": 2,
  "entidades": [
    {"id": 2, "nombre": "Segunda Entidad"},
    {"id": 1, "nombre": "Sismo del Este"}
  ]
}
```

---

## 🧪 Testing Manual (Opcional)

Si quieres probar los endpoints directamente, abre una tercera terminal y usa `curl`:

### Crear Entidad
```cmd
curl -X POST http://localhost:8080/crear_entidad ^
  -H "Content-Type: application/json" ^
  -d "{\"nombre\":\"Mi Entidad\"}"
```

### Ver Todas las Entidades
```cmd
curl http://localhost:8080/obtener_entidades
```

### Ver Entidad Específica
```cmd
curl http://localhost:8080/obtener_entidad/1
```

---

## 🛑 Detener la Aplicación

- **Backend:** Presiona `Ctrl + C` en la terminal del backend
- **Frontend:** Cierra la ventana de la aplicación o presiona `Ctrl + C`

---

## ❌ Problemas Comunes

### ❌ "Port 8080 already in use"
**Solución:** El puerto está ocupado por otra aplicación
```cmd
# Encuentra qué está usando el puerto (Windows PowerShell)
Get-NetTCPConnection -LocalPort 8080
```
O cambia el puerto en `backend/src/main/java/com/ppai/app/Main.java`

### ❌ "Could not find or load main class"
**Solución:** Compila primero
```cmd
cd backend
mvnw.cmd clean compile
cd ..
run-backend.bat
```

### ❌ "Connection refused" en el frontend
**Solución:** Asegúrate de que el backend esté corriendo en la otra terminal
- Verifica que veas el mensaje `Server started on http://localhost:8080`

### ❌ Base de datos corrupta
**Solución:** Elimina el archivo `sismos.db` en la raíz del proyecto
```cmd
del sismos.db
```
La BD se recreará automáticamente cuando inicies el backend

---

## 📊 Estructura de Archivos Generados

Cuando ejecutes la aplicación, se crearán:

```
PPAI_SISMOS/
├── sismos.db          ← Base de datos (creada automáticamente)
├── backend/
│   └── target/        ← Archivos compilados
├── frontend/
│   └── target/        ← Archivos compilados
└── ...
```

**Nota:** Los archivos en `target/` se pueden eliminar sin problema, se regenerarán al compilar de nuevo.

---

## 🎯 Próximos Pasos

✅ **Básico completado:**
- Backend corriendo
- Frontend funcionando
- Endpoints trabajando
- Base de datos inicializada

📖 **Para profundizar:**
- Lee `ARQUITECTURA.md` para entender el diseño
- Lee `GUIA_IMPLEMENTACION.md` para detalles técnicos
- Modifica las clases en `backend/src/main/java/com/ppai/app/` para agregar funcionalidades

---

## 📞 Referencias Rápidas

| Recurso | URL |
|---------|-----|
| Backend | http://localhost:8080 |
| Obtener Entidades | http://localhost:8080/obtener_entidades |
| Crear Entidad | POST a http://localhost:8080/crear_entidad |
| Base de Datos | sismos.db (SQLite) |

---

**¿Tienes problemas?** Consulta el README.md o GUIA_IMPLEMENTACION.md para más información.

```
http://localhost:8080/
```

Deberías ver: "Backend funcionando correctamente"

También puedes probar el endpoint de health check:
```
http://localhost:8080/health
```

### 🎯 Probar la API con Swagger UI

Una vez que el backend esté corriendo, abre en tu navegador:

**Swagger UI**: `http://localhost:8080/swagger-ui`

Aquí podrás:
- ✅ Ver todos los endpoints disponibles
- ✅ Probar cada endpoint directamente desde el navegador
- ✅ Ver la estructura de las peticiones y respuestas
- ✅ Ejecutar peticiones GET, POST, PUT, DELETE sin necesidad de Postman


### Conecta Frontend con Backend

En `frontend/src/main/java/com/ppai/app/frontend/service/ApiService.java`:

```java
public List<MiEntidad> obtenerTodas() throws IOException {
    HttpGet request = new HttpGet(BASE_URL + "/entidades");
    try (CloseableHttpResponse response = httpClient.execute(request)) {
        String json = EntityUtils.toString(response.getEntity());
        Type listType = new TypeToken<ArrayList<MiEntidad>>(){}.getType();
        return gson.fromJson(json, listType);
    }
}
```

## 🔧 Flujo de Trabajo Recomendado

1. **Backend primero**: Implementa entidades, DAOs, Gestor y Controladores
2. **Prueba con Postman o navegador**: Verifica que los endpoints funcionen
3. **Frontend después**: Diseña la interfaz y conéctala al backend
4. **Integración**: Prueba todo junto

## 🛠️ Editar en NetBeans

### Backend:
1. File → Open Project → Selecciona `backend`
2. Edita las clases Java normalmente
3. Run → Run File para probar

### Frontend:
1. File → Open Project → Selecciona `frontend`  
2. Para editar visualmente:
   - Abre `MainFrame.java`
   - Verás el diseñador visual si NetBeans lo reconoce
   - Arrastra componentes desde la paleta
3. Para editar código:
   - Usa la vista "Source"

## 🗄️ Trabajar con SQLite

La base de datos se crea automáticamente. Para crear tus tablas:

1. Edita `DatabaseConnection.java`
2. Agrega un método para crear tablas:

```java
private void initDatabase() {
    String createTableSQL = """
        CREATE TABLE IF NOT EXISTS mi_tabla (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT
        )
        """;
    
    try (var stmt = connection.createStatement()) {
        stmt.execute(createTableSQL);
    } catch (SQLException e) {
        throw new RuntimeException("Error al crear la tabla", e);
    }
}
```

3. Llama a `initDatabase()` en el constructor

## 📖 Recursos Útiles

- **Javalin Docs**: https://javalin.io/documentation
- **SQLite JDBC**: https://github.com/xerial/sqlite-jdbc
- **Gson**: https://github.com/google/gson
- **Java Swing Tutorial**: https://docs.oracle.com/javase/tutorial/uiswing/
