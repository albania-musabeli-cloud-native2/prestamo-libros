package com.musabeli.functions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.musabeli.config.GsonConfig;
import com.musabeli.graphql.GraphQLConfig;
import graphql.ExecutionInput;
import graphql.ExecutionResult;

import java.util.Optional;

public class GraphQLFunction {

    private final Gson gson = GsonConfig.create();

    @FunctionName("GraphQL")
    public HttpResponseMessage execute(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST},
                    route = "graphql", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            String body = request.getBody().orElse("{}");
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String query = json.has("query") ? json.get("query").getAsString() : "";
            JsonObject variables = json.has("variables") && !json.get("variables").isJsonNull()
                    ? json.get("variables").getAsJsonObject()
                    : new JsonObject();

            ExecutionInput input = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(gson.<java.util.Map<String, Object>>fromJson(variables, java.util.Map.class))
                    .build();

            ExecutionResult result = GraphQLConfig.getInstance().execute(input);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(result.toSpecification()))
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error GraphQL: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error ejecutando operación GraphQL").build();
        }
    }
}
