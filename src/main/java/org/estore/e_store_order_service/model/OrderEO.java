package org.estore.e_store_order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.estore.e_store_order_service.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class OrderEO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String razorpayOrderId;
    private double grandTotal;
    private double finalPrice;
    private Integer userId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
