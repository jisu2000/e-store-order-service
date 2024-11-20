package org.estore.e_store_order_service.external;

import java.util.Base64;

import org.estore.e_store_order_service.response.RazorpayTxnResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RazorpayExternalClient {

    private final RestClient.Builder restClientBuilder;
    private RestClient restClient;

    @Value("${razorpay.txn.api}")
    private String razorPayUrl;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    public RazorpayTxnResponse getTransactionDetails(String txnId) {
        try {
            String credentials = razorpayKey + ":" + razorpaySecret;
            String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

            restClient = restClientBuilder.baseUrl(razorPayUrl).build();

            return restClient
                    .get()
                    .uri("/{paymentId}", txnId)
                    .header("Authorization", "Basic " + encodedAuth)
                    .retrieve()
                    .body(RazorpayTxnResponse.class); 
        } catch (Exception e) {
            System.err.println("Error fetching Razorpay transaction details: " + e.getMessage());
        }

        return null;
    }
}
