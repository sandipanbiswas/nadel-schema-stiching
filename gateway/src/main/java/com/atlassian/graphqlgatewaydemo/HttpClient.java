package com.atlassian.graphqlgatewaydemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.print.attribute.standard.RequestingUserName;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.util.HttpConstants.ResponseStatusCodes.OK_200;

@Component
public class HttpClient {

    AsyncHttpClient asyncHttpClient;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    @PostConstruct
    public void init() {
        asyncHttpClient = asyncHttpClient();
    }

    public CompletableFuture<Response> makeRequest(Request request) {
        ListenableFuture<Response> responseListenableFuture = asyncHttpClient.executeRequest(request);
        return responseListenableFuture.toCompletableFuture().thenApply(response -> {
            log.info("http client response {}", response);
            return response;
        });
    }

    public CompletableFuture<Map<String, Object>> makeJsonPostRequestReturningJson(String url, Map<String, Object> body) {
        String jsonString = toJsonString(body);
        Request request = new RequestBuilder()
                .setUrl(url)
                .setMethod(HttpConstants.Methods.POST)
                .setBody(jsonString)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        return asyncHttpClient.executeRequest(request).toCompletableFuture()
                .thenApply(response -> {
                    if (response.getStatusCode() != OK_200) {
                        throw new RuntimeException(format("Non 200 status code %s", valueOf(response.getStatusCode())));
                    }
                    return fromJson(response.getResponseBody());
                });
    }

    private Map<String, Object> fromJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}