package org.estore.e_store_order_service.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductQuantity {
    private ProductResponse product;
    private Integer quantity;
}
