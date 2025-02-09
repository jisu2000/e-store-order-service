package org.estore.e_store_order_service.repo;

import org.estore.e_store_order_service.model.OrderEO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.razorpay.Order;


public interface OrderRepo extends JpaRepository<OrderEO,Integer> {

    Page<OrderEO> findByUserId(Integer userId, Pageable pageable);
    Order findByRazorpayOrderId(String rzpId);
}
