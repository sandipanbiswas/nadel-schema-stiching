package com.atlassian.graphqlgatewaydemo;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import graphql.language.AstPrinter;
import graphql.nadel.Nadel;
import graphql.nadel.ServiceExecution;
import graphql.nadel.ServiceExecutionFactory;
import graphql.nadel.ServiceExecutionParameters;
import graphql.nadel.ServiceExecutionResult;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

@Component
public class NadelProvider {

    @Autowired
    HttpClient httpClient;

    Nadel nadel;


    private static final Logger log = LoggerFactory.getLogger(NadelProvider.class);

    @PostConstruct
    public void init() throws IOException {
        log.info("initializing Nadel");
        String nadelDSL = loadResource("api.nadel");
        log.info("nadel DSL: {}", nadelDSL);
        nadel = Nadel.newNadel()
                .dsl(nadelDSL)
                .serviceExecutionFactory(new ServiceExecutionFactory() {
                    @Override
                    public ServiceExecution getServiceExecution(String serviceName) {
                        return newServiceExecution(serviceName);
                    }

                    @Override
                    public TypeDefinitionRegistry getUnderlyingTypeDefinitions(String serviceName) {
                        return loadUnderlyingSchema(serviceName);
                    }
                })
                .build();
        log.info("Nadel created");
    }

    private ServiceExecution newServiceExecution(String serviceName) {
        Properties serviceConfig = loadServiceConfig(serviceName);
        log.info("config for service {}: {}", serviceName, serviceConfig);
        String endpointUrl = requireNonNull(serviceConfig.getProperty("url"), "url is required for a service configuration");
        return new ServiceExecution() {
            @Override
            public CompletableFuture<ServiceExecutionResult> execute(ServiceExecutionParameters serviceExecutionParameters) {
                String queryString = AstPrinter.printAstCompact(serviceExecutionParameters.getQuery());
                Map<String, Object> body = ImmutableMap.of(
                        "query", queryString,
                        "variables", serviceExecutionParameters.getVariables()
                );
                return httpClient.makeJsonPostRequestReturningJson(endpointUrl, body)
                        .thenApply(underlyingServiceResult -> {
                            Map<String, Object> data = (Map<String, Object>) underlyingServiceResult.get("data");
                            List<Map<String, Object>> errors = (List<Map<String, Object>>) underlyingServiceResult.get("errors");
                            return new ServiceExecutionResult(data, errors);
                        });
            }
        };
    }

    private Properties loadServiceConfig(String serviceName) {
        Properties properties = loadProperties("services/" + serviceName + "/config.properties");
        return properties;
    }

    private TypeDefinitionRegistry loadUnderlyingSchema(String serviceName) {
        String sdl = loadResource("services/" + serviceName + "/schema.graphqls");
        log.info("schema for service {}: {}", serviceName, sdl);
        return new SchemaParser().parse(sdl);
    }

    private String loadResource(String path) {
        URL url = Resources.getResource(path);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Properties loadProperties(String path) {
        URL url = Resources.getResource(path);
        ByteSource byteSource = Resources.asByteSource(url);
        final Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = byteSource.openBufferedStream();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Bean
    Nadel getNadel() {
        return nadel;
    }

}
