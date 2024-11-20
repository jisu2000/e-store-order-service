package org.estore.e_store_order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.estore.e_store_order_service.response.ProductResponse;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderPlaceEntityEO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Embedded
    private ProductResponse productResponse;
    private Integer quantity;
    private Integer orderId;
}
