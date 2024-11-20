package org.estore.e_store_order_service.repo;

import java.util.List;

import org.estore.e_store_order_service.model.OrderPlaceEntityEO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPlacedProductRepo extends JpaRepository<OrderPlaceEntityEO,Integer> {

    List<OrderPlaceEntityEO> findByOrderId(Integer orderId);
}
