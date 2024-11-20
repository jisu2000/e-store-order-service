package org.estore.e_store_order_service.controller;

import org.estore.e_store_order_service.constant.PagingConstant;
import org.estore.e_store_order_service.response.PaginatedResponse;
import org.estore.e_store_order_service.service.OrderService;
import org.estore.e_store_order_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("address") Integer addressId) {
        return new ResponseEntity<>(orderService.requestOrder(addressId, authHeader), HttpStatus.OK);
    }

    @PostMapping("/process-payment")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> request) {
        return new ResponseEntity<>(paymentService.processConfirmPayment(request), HttpStatus.OK);
    }

    @GetMapping("/should-access")
    public ResponseEntity<?> getFinalPge(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("orderId") String orderId
    ){
        return new ResponseEntity<>(orderService.shouldAccess(authHeader,orderId),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(){
        return new ResponseEntity<>(orderService.getAllOrders(),HttpStatus.OK);
    }

    @GetMapping("/user-order")
    public ResponseEntity<?> getAllOrder(
           @RequestHeader("Authorization") String authHeader,
           @RequestParam(
                   value = "pageNo",
                   required = false,
                   defaultValue = PagingConstant.DEFAULT_PAGE_NO
           ) Integer pageNo,

           @RequestParam(
                   value = "pageSize",
                   required = false,
                   defaultValue = PagingConstant.DEFAULT_PAGE_SIZE
           ) Integer pageSize,

           @RequestParam(
                   value = "sortBy",
                   required = false,
                   defaultValue = PagingConstant.DEFAULT_SORT_BY
           ) String sortBy,

           @RequestParam(
                   value = "sortDir",
                   required = false,
                   defaultValue = PagingConstant.DEFAULT_SORT_DIR
           ) String sortDir
    ){

        PaginatedResponse response = orderService.getAllUserOrder(authHeader, pageNo, pageSize, sortBy, sortDir);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/cancel-order")
    public ResponseEntity<?> cancelOrder(
            @RequestParam("orderId") Integer id,
            @RequestHeader("Authorization") String authHeader
    ){
        return new ResponseEntity<>(orderService.cancelOrder(authHeader,id),HttpStatus.OK);
    }
}
