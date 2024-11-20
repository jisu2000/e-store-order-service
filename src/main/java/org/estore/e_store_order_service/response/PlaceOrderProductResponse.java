package org.estore.e_store_order_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlaceOrderProductResponse {
    private ProductResponse productResponse;
    private Integer quantity;
    private Integer orderId;
}
