package org.estore.e_store_order_service.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionDetails {
    private String orderId;
    private String key;
    private String currency;
    private Integer amount;
}
