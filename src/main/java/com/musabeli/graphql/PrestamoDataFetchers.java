package com.musabeli.graphql;

import com.musabeli.entities.Prestamo;
import com.musabeli.repository.PrestamoRepository;
import graphql.schema.DataFetcher;

import java.time.LocalDate;

public class PrestamoDataFetchers {

    private final PrestamoRepository repo = new PrestamoRepository();

    public DataFetcher<Object> getPrestamosFetcher() {
        return env -> repo.findAll();
    }

    public DataFetcher<Object> getPrestamoByIdFetcher() {
        return env -> {
            long id = Long.parseLong(env.getArgument("id").toString());
            return repo.findById(id).orElse(null);
        };
    }

    public DataFetcher<Object> crearPrestamoFetcher() {
        return env -> {
            Prestamo p = Prestamo.builder()
                    .idUsuario(Long.parseLong(env.getArgument("idUsuario").toString()))
                    .idLibro(Long.parseLong(env.getArgument("idLibro").toString()))
                    .fechaInicio(LocalDate.parse(env.getArgument("fechaInicio")))
                    .fechaFin(LocalDate.parse(env.getArgument("fechaFin")))
                    .estado(env.getArgument("estado"))
                    .build();
            return repo.create(p);
        };
    }

    public DataFetcher<Object> actualizarPrestamoFetcher() {
        return env -> {
            long id = Long.parseLong(env.getArgument("id").toString());
            return repo.findById(id).map(existing -> {
                try {
                    if (env.getArgument("idUsuario") != null)
                        existing.setIdUsuario(Long.parseLong(env.getArgument("idUsuario").toString()));
                    if (env.getArgument("idLibro") != null)
                        existing.setIdLibro(Long.parseLong(env.getArgument("idLibro").toString()));
                    if (env.getArgument("fechaInicio") != null)
                        existing.setFechaInicio(LocalDate.parse(env.getArgument("fechaInicio")));
                    if (env.getArgument("fechaFin") != null)
                        existing.setFechaFin(LocalDate.parse(env.getArgument("fechaFin")));
                    if (env.getArgument("estado") != null)
                        existing.setEstado(env.getArgument("estado"));
                    return repo.update(id, existing);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).orElse(false);
        };
    }

    public DataFetcher<Object> eliminarPrestamoFetcher() {
        return env -> {
            long id = Long.parseLong(env.getArgument("id").toString());
            return repo.delete(id);
        };
    }
}
