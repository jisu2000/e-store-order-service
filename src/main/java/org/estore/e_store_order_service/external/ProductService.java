package org.estore.e_store_order_service.external;

import java.util.HashMap;
import java.util.Map;

import org.estore.e_store_order_service.exceptions.*;
import org.estore.e_store_order_service.response.ApiResponse;
import org.estore.e_store_order_service.response.ProductResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class ProductService {
    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;
    private RestClient restClient;
    private final RestClient.Builder restClientBuilder;

    /**
     * @param authHeader
     * @return
     */
    @SuppressWarnings("unchecked")
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productFallBack")
    @Retry(name = "productRetry")
    public ProductResponse getProductByProuctId(String productId) {

        try {
            restClient = restClientBuilder.baseUrl(inventoryServiceUrl).build();

            ApiResponse<ProductResponse> producResponse = restClient.get()
                    .uri("/find?productId=" + productId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {
                    });

            return producResponse != null ? producResponse.getData() : null;
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = new HashMap<>();
            try {
                responseMap = mapper.readValue(responseBody, Map.class);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
            Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
            String errorMsg = error.get("error").toString();

            throw new ResourceNotFoundException(errorMsg);
        }

    }

    public ProductResponse productFallBack(Throwable ex) {

        if (ex instanceof ResourceNotFoundException) {
            throw new ResourceNotFoundException(ex.getMessage());
        }

        throw new FailureException("Your request can not be fullfilled this time");
    }
}
