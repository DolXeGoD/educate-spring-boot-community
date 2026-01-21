package com.gbsw.board.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsw.board.dto.passwordless.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordlessApiClient {

    @Value("${passwordless.serving-api-url}")
    private String servingApiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient;

    public ServingApiResponse<StatusResponse> checkStatus(String userId) {
        return executeGet(
                "/api/passwordless/status",
                Map.of("userId", userId),
                StatusResponse.class
        );
    }

    public ServingApiResponse<RegisterResponse> register(String userId) {
        return executePost(
                "/api/passwordless/register",
                Map.of("userId", userId),
                RegisterResponse.class
        );
    }

    public ServingApiResponse<LoginTriggerResponse> triggerLogin(String userId, String ip) {
        return executePost(
                "/api/passwordless/login-trigger",
                Map.of("userId", userId, "ip", ip),
                LoginTriggerResponse.class
        );
    }

    public ServingApiResponse<ResultResponse> checkResult(String userId, String sessionId) {
        return executeGet(
                "/api/passwordless/result",
                Map.of("userId", userId, "sessionId", sessionId),
                ResultResponse.class
        );
    }

    public ServingApiResponse<Object> cancel(String userId, String sessionId) {
        return executePost(
                "/api/passwordless/cancel",
                Map.of("userId", userId, "sessionId", sessionId),
                Object.class
        );
    }

    public ServingApiResponse<Object> withdraw(String userId) {
        return executePost(
                "/api/passwordless/withdrawal",
                Map.of("userId", userId),
                Object.class
        );
    }

    private <T> ServingApiResponse<T> executeGet(String path, Map<String, String> params, Class<T> dataType) {
        try {
            String jsonResponse = restClient.get()
                    .uri(buildUri(path, params))
                    .retrieve()
                    .body(String.class);

            return objectMapper.readValue(
                    jsonResponse,
                    objectMapper.getTypeFactory().constructParametricType(ServingApiResponse.class, dataType)
            );
        } catch (Exception e) {
            log.error("Passwordless API failed: {}", e.getMessage());
            return null;
        }
    }

    private <T> ServingApiResponse<T> executePost(String path, Map<String, String> params, Class<T> dataType) {
        try {
            String jsonResponse = restClient.post()
                    .uri(buildUri(path, params))
                    .retrieve()
                    .body(String.class);

            return objectMapper.readValue(
                    jsonResponse,
                    objectMapper.getTypeFactory().constructParametricType(ServingApiResponse.class, dataType)
            );
        } catch (Exception e) {
            log.error("Passwordless API failed: {}", e.getMessage());
            return null;
        }
    }

    private Function<UriBuilder, URI> buildUri(String path, Map<String, String> params) {
        return uriBuilder -> {
            uriBuilder.scheme(servingApiUrl.startsWith("https") ? "https" : "http")
                    .host(extractHost())
                    .port(extractPort())
                    .path(path);
            params.forEach(uriBuilder::queryParam);
            return uriBuilder.build();
        };
    }

    private String extractHost() {
        return servingApiUrl.replaceFirst("^https?://", "").split(":")[0];
    }

    private int extractPort() {
        String withoutProtocol = servingApiUrl.replaceFirst("^https?://", "");
        if (withoutProtocol.contains(":")) {
            return Integer.parseInt(withoutProtocol.split(":")[1].split("/")[0]);
        }
        return servingApiUrl.startsWith("https") ? 443 : 80;
    }
}
