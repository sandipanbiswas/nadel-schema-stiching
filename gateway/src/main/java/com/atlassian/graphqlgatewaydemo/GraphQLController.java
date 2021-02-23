package com.atlassian.graphqlgatewaydemo;

import graphql.ExecutionResult;
import graphql.nadel.Nadel;
import graphql.nadel.NadelExecutionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class GraphQLController {

    @Autowired
    Nadel nadel;

    @PostMapping(
            path = "/graphql",
            consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE},
            produces = APPLICATION_JSON_VALUE
    )
    public CompletableFuture<Map<String, Object>> graphql(@RequestBody @Valid GraphQLRequestBody graphQlRequest,
                                                          ServerWebExchange exchange) {

         NadelExecutionInput nadelExecutionInput = NadelExecutionInput.newNadelExecutionInput()
                .query(graphQlRequest.getQuery())
                .operationName(graphQlRequest.getOperationName())
                .variables(graphQlRequest.getVariables())
                .build();
        return nadel.execute(nadelExecutionInput).thenApply(ExecutionResult::toSpecification);
    }
}
