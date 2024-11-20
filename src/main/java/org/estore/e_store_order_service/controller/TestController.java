package org.estore.e_store_order_service.controller;

import org.estore.e_store_order_service.external.AuthService;
import org.estore.e_store_order_service.external.ProductService;
import org.estore.e_store_order_service.external.RazorpayExternalClient;
import org.estore.e_store_order_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final AuthService authService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RazorpayExternalClient razorpayExternalClient;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(
            @RequestHeader("Authorization") String authHeader) {
        return new ResponseEntity<>(authService.getUserFromHeader(authHeader), HttpStatus.OK);
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> getProduct(
            @RequestParam("pId") String productId) {
        return new ResponseEntity<>(productService.getProductByProuctId(productId), HttpStatus.OK);
    }

    @GetMapping("/create-order")
    public ResponseEntity<?> testPayment(
            @RequestParam("amount") double amount
    ){
        return new ResponseEntity<>(paymentService.getOrderTxnDetails(amount),HttpStatus.OK);
    }

    @GetMapping("/get-txn/{txnId}")
    public ResponseEntity<?> getTxn(@PathVariable String txnId){
        
        return new ResponseEntity<>(razorpayExternalClient.getTransactionDetails(txnId),HttpStatus.OK);
    }
}
