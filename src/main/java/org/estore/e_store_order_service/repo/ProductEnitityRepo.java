package org.estore.e_store_order_service.repo;

import org.estore.e_store_order_service.model.ProductEntityEO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductEnitityRepo extends JpaRepository<ProductEntityEO,Integer>{

    List<ProductEntityEO> findByOrderId(Integer orderId);
}
