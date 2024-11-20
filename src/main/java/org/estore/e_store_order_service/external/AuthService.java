package org.estore.e_store_order_service.external;

import java.util.*;
import org.estore.e_store_order_service.exceptions.*;
import org.estore.e_store_order_service.response.ApiResponse;
import org.estore.e_store_order_service.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthService {

    @Value("${auth.service.url}")
    private String authServiceUrl;
    private RestClient restClient;
    private final RestClient.Builder restClientBuilder;

    /**
     * @param authHeader
     * @return
     */
    @SuppressWarnings("unchecked")
    @CircuitBreaker(name = "authCircuitBreaker", fallbackMethod = "authFallBack")
    @Retry(name = "authRetry")
    public UserResponse getUserFromHeader(String authHeader) {

        try {
            restClient = restClientBuilder.baseUrl(authServiceUrl).build();

            ApiResponse<UserResponse> fetchedUser = restClient.get()
                    .uri(authServiceUrl + "/get-user")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {
                    });

            return fetchedUser != null ? fetchedUser.getData() : null;
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap=new HashMap<>();
            try {
                responseMap = mapper.readValue(responseBody, Map.class);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
            Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
            String errorMsg = error.get("error").toString();

            throw new UnauthorizeException(errorMsg);
        }

    }

    public UserResponse authFallBack(Throwable ex) {

        if (ex instanceof UnauthorizeException) {
            throw new UnauthorizeException(ex.getMessage());
        }

        throw new FailureException("Your request can not be fullfilled this time");
    }
}
