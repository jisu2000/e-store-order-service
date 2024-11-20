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



@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "Working fine";
    }
   
}
