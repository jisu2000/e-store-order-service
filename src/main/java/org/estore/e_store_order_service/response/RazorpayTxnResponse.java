package org.estore.e_store_order_service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RazorpayTxnResponse {
    private String id;
    private String entity;
    private int amount;
    private String currency;
    private String status;
    private String orderId;
    private String method;
    private String description;
     @JsonProperty("created_at")
    private long createdAt;
}
