package org.estore.e_store_order_service.repo;

import org.estore.e_store_order_service.model.OrderPlaceAddressEO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface OrderPlaceAddressRepo extends JpaRepository<OrderPlaceAddressEO,Integer> {
    OrderPlaceAddressEO findByOrderId(Integer orderId);
}
