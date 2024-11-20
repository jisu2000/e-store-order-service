package org.estore.e_store_order_service.repo;

import org.estore.e_store_order_service.model.OrderAddressEO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepo extends JpaRepository<OrderAddressEO,Integer>{
    OrderAddressEO findByOrderId(Integer orderId);
}
