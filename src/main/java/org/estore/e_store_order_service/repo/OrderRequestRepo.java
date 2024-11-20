package org.estore.e_store_order_service.repo;

import org.estore.e_store_order_service.model.OrderRequestEO;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRequestRepo extends JpaRepository<OrderRequestEO,Integer>{
    OrderRequestEO findByRazorpayOrderId(String razorpayOrderId);
}
