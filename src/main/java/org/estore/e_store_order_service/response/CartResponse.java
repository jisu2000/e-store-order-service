package org.estore.e_store_order_service.response;
import java.util.*;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartResponse {
    private List<ProductQuantity> products= new ArrayList<>();
    private double totalPrice;
    private double discountedPrice;
    private double discount;
    private Integer totalItems;
}
