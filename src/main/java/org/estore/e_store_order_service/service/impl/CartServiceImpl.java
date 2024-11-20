package org.estore.e_store_order_service.service.impl;

import java.util.List;

import org.estore.e_store_order_service.exceptions.InvalidRequestException;
import org.estore.e_store_order_service.external.AuthService;
import org.estore.e_store_order_service.external.ProductService;
import org.estore.e_store_order_service.model.CartEO;
import org.estore.e_store_order_service.repo.CartRepo;
import org.estore.e_store_order_service.response.ApiResponse;
import org.estore.e_store_order_service.response.CartResponse;
import org.estore.e_store_order_service.response.ProductQuantity;
import org.estore.e_store_order_service.response.ProductResponse;
import org.estore.e_store_order_service.response.UserResponse;
import org.estore.e_store_order_service.service.CartService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    private final ProductService productService;
    private final AuthService authService;
    private final CartRepo cartRepo;

    @Override
    public ApiResponse<?> addItemToCart(String authHeader, String productId) {

        UserResponse userResponse = authService.getUserFromHeader(authHeader);
        ProductResponse product = productService.getProductByProuctId(productId);

        boolean isUnavailbale = product.isUnavaliable();
        boolean isOutOfStock = product.getStocks() < 1;

        if (isUnavailbale || isOutOfStock) {
            throw new InvalidRequestException(isUnavailbale ? "Product is Unavailable" : "Product is out of stock");
        }

        CartEO productAlreadyExist = cartRepo.findByUserIdAndProductId(userResponse.getId(), productId);

        CartEO saved = null;



        if (productAlreadyExist != null) {
            productAlreadyExist.setQuantity(productAlreadyExist.getQuantity() + 1);
            saved = cartRepo.save(productAlreadyExist);
        } else {
            saved = cartRepo.save(
                    CartEO.builder()
                            .userId(userResponse.getId())
                            .productId(productId)
                            .quantity(1)
                            .build());
        }

        if (saved != null) {
            return new ApiResponse<String>("Item added to cart new quantity : " + saved.getQuantity());
        }

        throw new InvalidRequestException("Product can not be added to Cart");
    }

    @Override
    public ApiResponse<?> removeItemFromCart(String authHeader, String productId) {
        UserResponse userResponse = authService.getUserFromHeader(authHeader);
        CartEO fetched = cartRepo.findByUserIdAndProductId(userResponse.getId(), productId);

        Integer currentQuntity = 0;

        if (fetched == null) {
            throw new InvalidRequestException("This product is not in Your Cart");
        }
        try {
            if (fetched.getQuantity() > 1) {
                currentQuntity = fetched.getQuantity() - 1;
                fetched.setQuantity(fetched.getQuantity() - 1);
                cartRepo.save(fetched);

            } else {
                cartRepo.delete(fetched);
                currentQuntity = 0;
            }

            return new ApiResponse<String>("Product has been Removed new Quantity : " + currentQuntity);
        } catch (Exception ex) {

        }

        throw new InvalidRequestException("Product can not be removed from Cart");
    }

    @Override
    public ApiResponse<?> removeWholeItemFromCart(String authHeader, String productId) {

        UserResponse userResponse = authService.getUserFromHeader(authHeader);
        CartEO fetched = cartRepo.findByUserIdAndProductId(userResponse.getId(), productId);

        if (fetched == null) {
            throw new InvalidRequestException("This product is not in Your Cart");
        }

        try {
            cartRepo.delete(fetched);
            return new ApiResponse<String>("Whole Product has been Removed");
        } catch (Exception e) {
        }
        throw new InvalidRequestException("Product can not be removed from Cart");

    }

    @Override
    public CartResponse getCart(String authHeader) {
        UserResponse userResponse = authService.getUserFromHeader(authHeader);

        CartResponse cartResponse = new CartResponse();

        double totalPrice = 0;
        double discountPrice = 0;
        Integer totalItems = 0;

        List<CartEO> cartItmes = cartRepo.findByUserId(userResponse.getId());

        cartItmes.sort((c1,c2)->c2.getId().compareTo(c1.getId()) );

        for (CartEO cartItem : cartItmes) {
            ProductResponse product = null;

            try {
                product = productService.getProductByProuctId(cartItem.getProductId());
            } catch (Exception e) {
                cartRepo.delete(cartItem);
            }

            if (product != null) {
                ProductQuantity productQuantity = new ProductQuantity();
                productQuantity.setProduct(product);
                productQuantity.setQuantity(cartItem.getQuantity());
                cartResponse.getProducts().add(productQuantity);
                totalPrice += (product.getBasePrice() * cartItem.getQuantity());
                discountPrice += (product.getModifiedPrice() * cartItem.getQuantity());
                totalItems+=cartItem.getQuantity();
            }
        }

        cartResponse.setTotalItems(totalItems);
        cartResponse.setTotalPrice(totalPrice);
        cartResponse.setDiscountedPrice(discountPrice);
        cartResponse.setDiscount(totalPrice - discountPrice);

        return cartResponse;
    }

}
