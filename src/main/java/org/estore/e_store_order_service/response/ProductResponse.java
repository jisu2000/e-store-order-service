package org.estore.e_store_order_service.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse {
    private String productId;
    private String productName;
    private boolean isUnavaliable;
    private double basePrice;
    private Integer stocks;
    private String imageUrl;
    private String category;
    private double modifiedPrice;
}
