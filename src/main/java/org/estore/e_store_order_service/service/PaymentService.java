package org.estore.e_store_order_service.service;

import java.util.Map;

import org.estore.e_store_order_service.model.TransactionEO;
import org.estore.e_store_order_service.response.TransactionDetails;

public interface PaymentService {

    TransactionDetails getOrderTxnDetails(double amount);
    TransactionEO processConfirmPayment(Map<String,Object> request);
}
