package org.estore.e_store_order_service.external;

import java.util.HashMap;
import java.util.Map;

import org.estore.e_store_order_service.exceptions.FailureException;
import org.estore.e_store_order_service.exceptions.ResourceNotFoundException;
import org.estore.e_store_order_service.response.AddressResponse;
import org.estore.e_store_order_service.response.ApiResponse;
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
public class AdddressService {
    @Value("${user.address.url}")
    private String addressUrl;
    private RestClient restClient;
    private final RestClient.Builder restClientBuilder;

    /**
     * @param authHeader
     * @return
     */
    @SuppressWarnings("unchecked")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "addressFallBack")
    @Retry(name = "addressRetry")
    public AddressResponse getProductByProuctId(Integer addressId) {

        try {
            restClient = restClientBuilder.baseUrl(addressUrl).build();

            ApiResponse<AddressResponse> addressResponse = restClient.get()
                    .uri("/find-by-id/" + addressId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<AddressResponse>>() {
                    });

            return addressResponse != null ? addressResponse.getData() : null;
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

    public AddressResponse addressFallBack(Throwable ex) {

        if (ex instanceof ResourceNotFoundException) {
            throw new ResourceNotFoundException(ex.getMessage());
        }

        throw new FailureException("Your request can not be fullfilled this time");
    }
}
