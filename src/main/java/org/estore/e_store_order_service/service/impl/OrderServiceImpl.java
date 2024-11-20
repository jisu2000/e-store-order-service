package org.estore.e_store_order_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.estore.e_store_order_service.enums.OrderStatus;
import org.estore.e_store_order_service.exceptions.InvalidRequestException;
import org.estore.e_store_order_service.exceptions.ResourceNotFoundException;
import org.estore.e_store_order_service.external.AdddressService;
import org.estore.e_store_order_service.external.AuthService;
import org.estore.e_store_order_service.model.*;
import org.estore.e_store_order_service.repo.OrderAddressRepo;
import org.estore.e_store_order_service.repo.OrderPlaceAddressRepo;
import org.estore.e_store_order_service.repo.OrderPlacedProductRepo;
import org.estore.e_store_order_service.repo.OrderRepo;
import org.estore.e_store_order_service.repo.OrderRequestRepo;
import org.estore.e_store_order_service.repo.ProductEnitityRepo;
import org.estore.e_store_order_service.repo.TransactionRepo;
import org.estore.e_store_order_service.response.*;
import org.estore.e_store_order_service.service.CartService;
import org.estore.e_store_order_service.service.OrderService;
import org.estore.e_store_order_service.service.PaymentService;
import org.hibernate.query.SortDirection;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuthService authService;
    private final CartService cartService;
    private final AdddressService adddressService;
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;
    private final OrderRequestRepo orderRequestRepo;
    private final OrderAddressRepo orderAddressRepo;
    private final ProductEnitityRepo productEnitityRepo;
    private final TransactionRepo transactionRepo;
    private final OrderRepo orderRepo;
    private final OrderPlacedProductRepo orderPlacedProductRepo;
    private final OrderPlaceAddressRepo orderPlaceAddressRepo;

    @Override
    @Transactional
    public TransactionDetails requestOrder(Integer addressId, String authHeader) {

        UserResponse userResponse = authService.getUserFromHeader(authHeader);

        AddressResponse addressResponse = adddressService.getProductByProuctId(addressId);

        CartResponse cartResponse = cartService.getCart(authHeader);

        if (cartResponse.getProducts().isEmpty()) {
            throw new InvalidRequestException("Cart is Empty");
        }

        boolean isAnyOneUnavailable = cartResponse.getProducts()
                .stream()
                .filter(e -> e.getProduct().isUnavaliable())
                .findAny()
                .isPresent();

        if (isAnyOneUnavailable) {
            throw new InvalidRequestException("Some of the product is unavailable");
        }

        boolean isOneLessInStock = cartResponse.getProducts()
                .stream()
                .filter(e -> e.getProduct().getStocks() < e.getQuantity())
                .findAny()
                .isPresent();

        if (isOneLessInStock) {
            throw new InvalidRequestException("Please reduce item quantity");
        }

        TransactionDetails transactionDetails = paymentService
                .getOrderTxnDetails(cartResponse.getDiscountedPrice());

        if (transactionDetails == null) {
            throw new InvalidRequestException("Transaction can not be created");
        }

        // CREATE OrderRequest
        OrderRequestEO orderRequestEO = new OrderRequestEO();
        orderRequestEO.setFinalPrice(cartResponse.getDiscountedPrice());
        orderRequestEO.setGrandTotal(cartResponse.getTotalPrice());
        orderRequestEO.setRazorpayOrderId(transactionDetails.getOrderId());
        orderRequestEO.setUserId(userResponse.getId());

        OrderRequestEO saved = orderRequestRepo.save(orderRequestEO);

        // SAVE OrderAddressEO
        OrderAddressEO orderAddressEO = modelMapper.map(addressResponse, OrderAddressEO.class);
        orderAddressEO.setOrderId(saved.getId());
        orderAddressRepo.save(orderAddressEO);

        List<ProductEntityEO> items = cartResponse.getProducts()
                .stream()
                .map(
                        e -> {
                            ProductEntityEO productEntityEO = new ProductEntityEO();
                            productEntityEO.setProductResponse(e.getProduct());
                            productEntityEO.setOrderId(saved.getId());
                            productEntityEO.setQuantity(e.getQuantity());
                            return productEntityEO;
                        })
                .toList();

        productEnitityRepo.saveAll(items);

        return transactionDetails;
    }

    @Override
    public boolean shouldAccess(String authHeader, String orderId) {
        UserResponse userResponse = authService.getUserFromHeader(authHeader);
        OrderRequestEO orderRequestEO = orderRequestRepo.findByRazorpayOrderId(orderId);
        if (orderRequestEO == null) {
            return false;
        }

        if (orderRequestEO.getUserId() != userResponse.getId()) {
            return false;
        }

        TransactionEO transactionEO = transactionRepo.findByOrderId(orderId);

        if (transactionEO == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<OrderResponse> getAllOrders() {

        List<OrderEO> orders = orderRepo.findAll();

        List<OrderResponse> orderResponses = orders
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
        return orderResponses;

    }

    @Override
    public PaginatedResponse getAllUserOrder(String authHeader, Integer pageNo, Integer pageSize, String sortBy,
            String sortDir) {

        UserResponse user = authService.getUserFromHeader(authHeader);


        if (sortDir.equals("ASC")) {
            sortDir = "ASC";
        }

        Sort sort = Sort.by(Direction.valueOf(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<OrderEO> ordersOfTheUser = orderRepo.findByUserId(user.getId(), pageable);

        List<OrderResponse> response = ordersOfTheUser.getContent()
                .stream().map(mapper)
                .toList();

        return PaginatedResponse.builder()
                .content(response)
                .currentPage(ordersOfTheUser.getNumber())
                .isLastPage(ordersOfTheUser.isLast())
                .totalPages(ordersOfTheUser.getTotalPages())
                .totalItems((int) ordersOfTheUser.getTotalElements())
                .currentPageItemsNumber(ordersOfTheUser.getNumberOfElements())
                .pageSize(ordersOfTheUser.getSize())
                .build();

    }

    @Override
    public ApiResponse<?> cancelOrder(String authHeader, Integer orderId) {
        UserResponse userResponse = authService.getUserFromHeader(authHeader);

        OrderEO orderEO
                = orderRepo.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("ORDER","ID",orderId.toString()));

         boolean isAdmin=userResponse
                .getRoles().stream()
                .filter(e -> e.getRoleName().equals("ADMIN"))
                .findAny().isPresent();

         boolean isSameUserOrder =
                 orderEO.getUserId()== userResponse.getId();


         if(!isAdmin && !isSameUserOrder){
             throw new InvalidRequestException("This action can not be performed");
         }

         if(!orderEO.getStatus().equals(OrderStatus.CREATED)){
             throw new InvalidRequestException("Order can not be Cancelled");
         }


         orderEO.setStatus(OrderStatus.CANCELLED);

         orderRepo.save(orderEO);

         return new ApiResponse<String>("Order Cancelled");


    }

    private Function<OrderEO, OrderResponse> mapper = new Function<OrderEO, OrderResponse>() {

        @Override
        public OrderResponse apply(OrderEO order) {

            OrderResponse response = modelMapper.map(order, OrderResponse.class);

            List<OrderPlaceEntityEO> products = orderPlacedProductRepo.findByOrderId(order.getId());

            List<PlaceOrderProductResponse> productResponses = products.stream()
                    .map(e -> modelMapper.map(e, PlaceOrderProductResponse.class))
                    .toList();

            response.setProducts(productResponses);

            OrderPlaceAddressEO address = orderPlaceAddressRepo.findByOrderId(order.getId());

            PlaceOrderAddessResponse addressResponse = modelMapper.map(address, PlaceOrderAddessResponse.class);
            response.setAddress(addressResponse);

            return response;
        }

    };

}
