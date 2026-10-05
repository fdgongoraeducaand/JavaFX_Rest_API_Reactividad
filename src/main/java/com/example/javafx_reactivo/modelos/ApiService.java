package com.example.javafx_reactivo.modelos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class ApiService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/users";
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retorna una tarea concurrente observable que obtiene los datos
     * de forma asíncrona sin bloquear la interfaz.
     */
    public Task<List<Usuario>> crearTareaDescarga() {
        return new Task<>() {
            @Override
            protected List<Usuario> call() throws Exception {
                updateMessage("Conectando con la API REST...");

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    updateMessage("Procesando JSON recibido...");
                    Usuario[] usuariosArray = mapper.readValue(response.body(), Usuario[].class);
                    return Arrays.asList(usuariosArray);
                } else {
                    throw new IOException("Error HTTP " + response.statusCode() + ": " + response.body());
                }
            }
        };
    }
}
