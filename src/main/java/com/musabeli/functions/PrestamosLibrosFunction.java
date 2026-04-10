package com.musabeli.functions;

import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.musabeli.config.GsonConfig;
import com.musabeli.entities.Libro;
import com.musabeli.repository.LibroRepository;

import java.util.Optional;

public class PrestamosLibrosFunction {

    private final Gson gson = GsonConfig.create();

    private final LibroRepository libroRepo = new LibroRepository();

    // ─── LIBROS ──────────────────────────────────────────────────────────────

    @FunctionName("GetLibros")
    public HttpResponseMessage getLibros(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET},
                    route = "libros", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(libroRepo.findAll()))
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error GetLibros: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener libros").build();
        }
    }

    @FunctionName("GetLibroById")
    public HttpResponseMessage getLibroById(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET},
                    route = "libros/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        try {
            return libroRepo.findById(Long.parseLong(id))
                    .<HttpResponseMessage>map(libro -> request.createResponseBuilder(HttpStatus.OK)
                            .header("Content-Type", "application/json")
                            .body(gson.toJson(libro)).build())
                    .orElse(request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("Libro no encontrado").build());
        } catch (Exception e) {
            context.getLogger().severe("Error GetLibroById: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener libro").build();
        }
    }

    @FunctionName("CreateLibro")
    public HttpResponseMessage createLibro(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST},
                    route = "libros", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            String body = request.getBody().orElse("");
            Libro libro = gson.fromJson(body, Libro.class);
            Libro created = libroRepo.create(libro);
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(created)).build();
        } catch (Exception e) {
            context.getLogger().severe("Error CreateLibro: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear libro").build();
        }
    }

    @FunctionName("UpdateLibro")
    public HttpResponseMessage updateLibro(
            @HttpTrigger(name = "req", methods = {HttpMethod.PUT},
                    route = "libros/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        try {
            String body = request.getBody().orElse("");
            Libro libro = gson.fromJson(body, Libro.class);
            boolean updated = libroRepo.update(Long.parseLong(id), libro);
            if (updated) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body("Libro actualizado").build();
            }
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Libro no encontrado").build();
        } catch (Exception e) {
            context.getLogger().severe("Error UpdateLibro: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar libro").build();
        }
    }

    @FunctionName("DeleteLibro")
    public HttpResponseMessage deleteLibro(
            @HttpTrigger(name = "req", methods = {HttpMethod.DELETE},
                    route = "libros/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        try {
            boolean deleted = libroRepo.delete(Long.parseLong(id));
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body("Libro eliminado").build();
            }
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Libro no encontrado").build();
        } catch (Exception e) {
            context.getLogger().severe("Error DeleteLibro: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar libro").build();
        }
    }

}
