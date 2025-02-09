package org.estore.e_store_order_service.repo;

import java.util.List;

import org.estore.e_store_order_service.model.OrderEO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepo extends JpaRepository<OrderEO,Integer> {

    Page<OrderEO> findByUserId(Integer userId, Pageable pageable);
    List<OrderEO> findByRazorpayOrderId(String rzpId);
}
