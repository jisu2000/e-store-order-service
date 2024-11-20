package org.estore.e_store_order_service.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponse {
    private Integer id;
    private String razorpayOrderId;
    private double grandTotal;
    private double finalPrice;
    private Integer userId;
    private LocalDateTime createdAt;
    private String transactionId;
    private String status;
    private List<PlaceOrderProductResponse> products = new ArrayList<>();
    private PlaceOrderAddessResponse address;

}
