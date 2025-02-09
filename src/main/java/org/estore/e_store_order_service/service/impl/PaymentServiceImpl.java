package org.estore.e_store_order_service.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.estore.e_store_order_service.enums.OrderStatus;
import org.estore.e_store_order_service.exceptions.FailureException;
import org.estore.e_store_order_service.exceptions.InvalidRequestException;
import org.estore.e_store_order_service.exceptions.ResourceNotFoundException;
import org.estore.e_store_order_service.external.RazorpayExternalClient;
import org.estore.e_store_order_service.model.*;
import org.estore.e_store_order_service.repo.*;
import org.estore.e_store_order_service.response.RazorpayTxnResponse;
import org.estore.e_store_order_service.response.TransactionDetails;
import org.estore.e_store_order_service.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;
    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final RazorpayExternalClient razorpayExternalClient;
    private final TransactionRepo transactionRepo;
    private final OrderRequestRepo orderRequestRepo;
    private final OrderAddressRepo orderAddressRepo;
    private final ProductEnitityRepo productEnitityRepo;
    private final OrderPlaceAddressRepo orderPlaceAddressRepo;
    private final OrderPlacedProductRepo orderPlacedProductRepo;
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;

    @Override
    public TransactionDetails getOrderTxnDetails(double amount) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", amount * 100);
            jsonObject.put("currency", "INR");

            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            Order order = razorpayClient.orders.create(jsonObject);

            return TransactionDetails.builder()
                    .amount(order.get("amount"))
                    .orderId(order.get("id"))
                    .currency(order.get("currency"))
                    .key(keyId)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @Transactional
    public TransactionEO processConfirmPayment(Map<String, Object> request) {
        String orderId = request.get("orderId").toString();
        String txnId = request.get("txnId").toString();
        String signature = request.get("signature").toString();

        // FETCH THE TRANSACTION FROM RAZORPAY TRANSACTION CLIENT

        RazorpayTxnResponse txnResponse = razorpayExternalClient.getTransactionDetails(txnId);

        if (txnResponse == null) {
            throw new FailureException("Payment Failed");
        }

        if (!txnResponse.getStatus().equals("captured")) {
            throw new FailureException("Payment is not Successful");
        }

        OrderRequestEO orderRequestEO = orderRequestRepo.findByRazorpayOrderId(orderId);

        if (orderRequestEO == null) {
            throw new ResourceNotFoundException("Invalid order Id");
        }

        List<OrderEO> prevOrder = orderRepo.findByRazorpayOrderId(orderRequestEO.getRazorpayOrderId());

        if (!prevOrder.isEmpty()) {
            throw new InvalidRequestException("This action can not be performed");
        }

        TransactionEO transactionEO = new TransactionEO();
        transactionEO.setAmount(txnResponse.getAmount() / 100);
        transactionEO.setOrderId(orderId);
        transactionEO.setSignature(signature);
        transactionEO.setStatus(txnResponse.getStatus());
        transactionEO.setTransactionId(txnId);
        transactionEO.setUserId(orderRequestEO.getUserId());

        long unixTimestamp = txnResponse.getCreatedAt();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp), ZoneId.systemDefault());

        transactionEO.setCreatedAt(dateTime);

        TransactionEO saved = transactionRepo.save(transactionEO);

        OrderEO orderEO = new OrderEO();
        orderEO.setRazorpayOrderId(orderRequestEO.getRazorpayOrderId());
        orderEO.setFinalPrice(orderRequestEO.getFinalPrice());
        orderEO.setGrandTotal(orderRequestEO.getGrandTotal());
        orderEO.setStatus(OrderStatus.CREATED);
        orderEO.setTransactionId(saved.getTransactionId());
        orderEO.setUserId(orderRequestEO.getUserId());

        OrderEO savedOrder = orderRepo.save(orderEO);

        List<ProductEntityEO> products = productEnitityRepo.findByOrderId(orderRequestEO.getId());

        if (products.isEmpty()) {
            throw new FailureException("Order can not be placed");
        }

        products.forEach(e -> {
            OrderPlaceEntityEO entityEO = new OrderPlaceEntityEO();
            entityEO.setProductResponse(e.getProductResponse());
            entityEO.setQuantity(e.getQuantity());
            entityEO.setOrderId(savedOrder.getId());

            orderPlacedProductRepo.save(entityEO);
        });

        OrderAddressEO orderAddressEO = orderAddressRepo.findByOrderId(orderRequestEO.getId());

        if (orderAddressEO == null) {
            throw new FailureException("Order can not be placed");
        }

        OrderPlaceAddressEO orderPlaceAddressEO = new OrderPlaceAddressEO();

        orderPlaceAddressEO.setAddressType(orderAddressEO.getAddressType());
        orderPlaceAddressEO.setLandmark(orderAddressEO.getLandmark());
        orderPlaceAddressEO.setState(orderAddressEO.getState());
        orderPlaceAddressEO.setPhoneNumber(orderAddressEO.getPhoneNumber());
        orderPlaceAddressEO.setZipCode(orderAddressEO.getZipCode());
        orderPlaceAddressEO.setLocality(orderAddressEO.getLocality());
        orderPlaceAddressEO.setOrderId(savedOrder.getId());

        orderPlaceAddressRepo.save(orderPlaceAddressEO);

        List<CartEO> carts = cartRepo.findByUserId(saved.getUserId());

        carts.forEach(cartRepo::delete);

        if (savedOrder == null || saved == null) {
            throw new FailureException("Order can not be placed");
        }

        return saved;

    }
}
