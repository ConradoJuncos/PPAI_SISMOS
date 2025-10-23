package com.ppai.app.frontend.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

/**
 * Servicio para comunicarse con el backend mediante HTTP.
 * Puedes crear métodos aquí para hacer peticiones GET, POST, PUT, DELETE, etc.
 */
public class ApiService {

    private static final String BASE_URL = "http://localhost:8080";
    private final Gson gson;
    private final CloseableHttpClient httpClient;

    public ApiService() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Crea una nueva entidad en el backend
     * @param jsonBody JSON con los datos de la entidad
     * @return Response del servidor formateada
     */
    public String crearEntidad(String jsonBody) throws IOException, ParseException {
        HttpPost request = new HttpPost(BASE_URL + "/crear_entidad");
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());

            // Formatear la respuesta para mostrar
            try {
                Object json = gson.fromJson(responseBody, Object.class);
                return gson.toJson(json);
            } catch (Exception e) {
                return responseBody;
            }
        }
    }

    /**
     * Obtiene todas las entidades del backend
     * @return Listado de entidades formateado
     */
    public String obtenerEntidades() throws IOException, ParseException {
        HttpGet request = new HttpGet(BASE_URL + "/obtener_entidades");
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());

            // Formatear la respuesta para mostrar
            try {
                Object json = gson.fromJson(responseBody, Object.class);
                return gson.toJson(json);
            } catch (Exception e) {
                return responseBody;
            }
        }
    }

    /**
     * Obtiene una entidad específica por ID
     * @param id ID de la entidad
     * @return Datos de la entidad
     */
    public String obtenerEntidadPorId(Long id) throws IOException, ParseException {
        HttpGet request = new HttpGet(BASE_URL + "/obtener_entidad/" + id);
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());

            try {
                Object json = gson.fromJson(responseBody, Object.class);
                return gson.toJson(json);
            } catch (Exception e) {
                return responseBody;
            }
        }
    }

    /**
     * Cierra la conexión HTTP
     */
    public void cerrar() throws IOException {
        httpClient.close();
    }
}

