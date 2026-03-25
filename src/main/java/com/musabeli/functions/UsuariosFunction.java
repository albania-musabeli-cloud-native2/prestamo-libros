package com.musabeli.functions;

import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.musabeli.config.GsonConfig;
import com.musabeli.entities.Usuario;
import com.musabeli.repository.UsuarioRepository;

import java.util.Optional;

public class UsuariosFunction {

    private final Gson gson = GsonConfig.create();
    private final UsuarioRepository usuarioRepo = new UsuarioRepository();

    @FunctionName("GetUsuarios")
    public HttpResponseMessage getUsuarios(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET},
                    route = "usuarios", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(usuarioRepo.findAll()))
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error GetUsuarios: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener usuarios").build();
        }
    }

    @FunctionName("GetUsuarioById")
    public HttpResponseMessage getUsuarioById(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET},
                    route = "usuarios/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        try {
            return usuarioRepo.findById(Long.parseLong(id))
                    .<HttpResponseMessage>map(u -> request.createResponseBuilder(HttpStatus.OK)
                            .header("Content-Type", "application/json")
                            .body(gson.toJson(u)).build())
                    .orElse(request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("Usuario no encontrado").build());
        } catch (Exception e) {
            context.getLogger().severe("Error GetUsuarioById: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener usuario").build();
        }
    }

    @FunctionName("CreateUsuario")
    public HttpResponseMessage createUsuario(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST},
                    route = "usuarios", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            String body = request.getBody().orElse("");
            Usuario usuario = gson.fromJson(body, Usuario.class);
            Usuario created = usuarioRepo.create(usuario);
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(created)).build();
        } catch (Exception e) {
            context.getLogger().severe("Error CreateUsuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear usuario").build();
        }
    }

    @FunctionName("UpdateUsuario")
    public HttpResponseMessage updateUsuario(
            @HttpTrigger(name = "req", methods = {HttpMethod.PUT},
                    route = "usuarios/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        try {
            String body = request.getBody().orElse("");
            Usuario usuario = gson.fromJson(body, Usuario.class);
            boolean updated = usuarioRepo.update(Long.parseLong(id), usuario);
            if (updated) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body("Usuario actualizado").build();
            }
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado").build();
        } catch (Exception e) {
            context.getLogger().severe("Error UpdateUsuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar usuario").build();
        }
    }

    @FunctionName("DeleteUsuario")
    public HttpResponseMessage deleteUsuario(
            @HttpTrigger(name = "req", methods = {HttpMethod.DELETE},
                    route = "usuarios/{id}", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        try {
            boolean deleted = usuarioRepo.delete(Long.parseLong(id));
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body("Usuario eliminado").build();
            }
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado").build();
        } catch (Exception e) {
            context.getLogger().severe("Error DeleteUsuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar usuario").build();
        }
    }
}
