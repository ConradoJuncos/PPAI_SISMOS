package com.ppai.app.frontend.service;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * Servicio para comunicarse con el backend mediante HTTP.
 * Puedes crear métodos aquí para hacer peticiones GET, POST, PUT, DELETE, etc.
 */
public class ApiService {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final Gson gson;
    private final CloseableHttpClient httpClient;

    public ApiService() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
    }

    // TODO: Implementa aquí tus métodos para comunicarte con el backend
    // Ejemplos:

    // public List<MiEntidad> obtenerTodas() throws IOException {
    //     HttpGet request = new HttpGet(BASE_URL + "/entidades");
    //     try (CloseableHttpResponse response = httpClient.execute(request)) {
    //         String json = EntityUtils.toString(response.getEntity());
    //         Type listType = new TypeToken<ArrayList<MiEntidad>>(){}.getType();
    //         return gson.fromJson(json, listType);
    //     }
    // }

    // public MiEntidad crear(MiEntidad entidad) throws IOException {
    //     HttpPost request = new HttpPost(BASE_URL + "/entidades");
    //     String json = gson.toJson(entidad);
    //     StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
    //     request.setEntity(entity);
    //     try (CloseableHttpResponse response = httpClient.execute(request)) {
    //         String responseJson = EntityUtils.toString(response.getEntity());
    //         return gson.fromJson(responseJson, MiEntidad.class);
    //     }
    // }

    public void cerrar() throws IOException {
        httpClient.close();
    }
}
