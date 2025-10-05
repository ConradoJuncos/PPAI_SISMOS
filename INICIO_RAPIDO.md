# Guía Rápida de Inicio

## 🚀 Pasos para Ejecutar el Proyecto Base

### 1️⃣ Iniciar el Backend

Abre una terminal (CMD o PowerShell) en la carpeta raíz del proyecto y ejecuta:

```cmd
run-backend.bat
```

O manualmente:
```cmd
cd backend
mvnw.cmd clean install
mvnw.cmd exec:java -Dexec.mainClass="com.ppai.app.Main"
```

Verás un mensaje como este:
```
╔═══════════════════════════════════════════════════════╗
║   Backend - Servidor Iniciado                        ║
║   Puerto: 8080                                       ║
║   URL: http://localhost:8080                       ║
║   CORS: Habilitado                                    ║
╚═══════════════════════════════════════════════════════╝
```

✅ El backend ahora está corriendo en `http://localhost:8080`

### 2️⃣ Iniciar el Frontend

**IMPORTANTE**: Deja el backend corriendo y abre OTRA terminal. Luego ejecuta:

```cmd
run-frontend.bat
```

O manualmente:
```cmd
cd frontend
mvnw.cmd clean install
mvnw.cmd exec:java -Dexec.mainClass="com.ppai.app.frontend.gui.MainFrame"
```

✅ Se abrirá la ventana de la aplicación de escritorio con un mensaje de bienvenida

### 3️⃣ Verificar que Funciona

Abre tu navegador y visita:
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
