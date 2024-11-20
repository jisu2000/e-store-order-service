package org.estore.e_store_order_service.repo;

import org.estore.e_store_order_service.model.TransactionEO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<TransactionEO,Integer>{
    TransactionEO findByOrderId(String orderId);
}
