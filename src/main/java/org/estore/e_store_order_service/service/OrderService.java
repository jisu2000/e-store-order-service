package org.estore.e_store_order_service.service;

import java.util.List;

import org.estore.e_store_order_service.response.ApiResponse;
import org.estore.e_store_order_service.response.OrderResponse;
import org.estore.e_store_order_service.response.PaginatedResponse;
import org.estore.e_store_order_service.response.TransactionDetails;

public interface OrderService {
    public TransactionDetails requestOrder(Integer addressId, String authHeader);

    public boolean shouldAccess(String authHeader, String orderId);

    List<OrderResponse> getAllOrders();

    PaginatedResponse getAllUserOrder(String authHeader, Integer pageNo, Integer pageSize , String sortBy, String sortDir);

    ApiResponse<?> cancelOrder(String authHeader, Integer orderId);
}
