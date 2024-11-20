package org.estore.e_store_order_service.service;

import org.estore.e_store_order_service.response.ApiResponse;
import org.estore.e_store_order_service.response.CartResponse;

public interface CartService {
    ApiResponse<?> addItemToCart(String authHeader, String productId);
    ApiResponse<?> removeItemFromCart(String authHeader, String productId);
    ApiResponse<?> removeWholeItemFromCart(String authHeder, String productId);
    CartResponse getCart(String authHeader);
}
