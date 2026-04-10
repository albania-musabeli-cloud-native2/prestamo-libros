package com.musabeli.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class GraphQLConfig {

    private static GraphQL instance;

    public static GraphQL getInstance() {
        if (instance == null) {
            instance = build();
        }
        return instance;
    }

    private static GraphQL build() {
        InputStream stream = GraphQLConfig.class.getClassLoader()
                .getResourceAsStream("prestamo.graphqls");
        if (stream == null) {
            throw new RuntimeException("No se encontró prestamo.graphqls en resources");
        }

        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(reader);

        PrestamoDataFetchers fetchers = new PrestamoDataFetchers();

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                        .dataFetcher("prestamos", fetchers.getPrestamosFetcher())
                        .dataFetcher("prestamo", fetchers.getPrestamoByIdFetcher()))
                .type("Mutation", builder -> builder
                        .dataFetcher("crearPrestamo", fetchers.crearPrestamoFetcher())
                        .dataFetcher("actualizarPrestamo", fetchers.actualizarPrestamoFetcher())
                        .dataFetcher("eliminarPrestamo", fetchers.eliminarPrestamoFetcher()))
                .build();

        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }
}
