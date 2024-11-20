package org.estore.e_store_order_service.repo;

import java.util.List;

import org.estore.e_store_order_service.model.CartEO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<CartEO,Integer>{
    List<CartEO> findByUserId(Integer userId);
    CartEO findByUserIdAndProductId(Integer userId, String productId);
}
