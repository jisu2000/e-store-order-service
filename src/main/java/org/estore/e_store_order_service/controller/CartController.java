package org.estore.e_store_order_service.controller;

import org.estore.e_store_order_service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("productId") String productid) {
        return new ResponseEntity<>(cartService.addItemToCart(authHeader, productid), HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeSingle(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("productId") String productid) {
        return new ResponseEntity<>(cartService.removeItemFromCart(authHeader, productid), HttpStatus.OK);
    }

    @DeleteMapping("/remove-whole")
    public ResponseEntity<?> dremoveWhole(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("productId") String productid) {
        return new ResponseEntity<>(cartService.removeWholeItemFromCart(authHeader, productid), HttpStatus.OK);

    }

    @GetMapping("/get-cart")
    public ResponseEntity<?> getCart(
        @RequestHeader("Authorization") String authHeader
    ){
        return new ResponseEntity<>(cartService.getCart(authHeader),HttpStatus.OK);
    }
}
