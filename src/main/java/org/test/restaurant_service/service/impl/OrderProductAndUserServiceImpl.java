package org.test.restaurant_service.service.impl;

import org.springdoc.core.GenericResponseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestWithPayloadDto;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.*;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.*;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderProductAndUserServiceImpl implements OrderProductAndUserService {

    private final OrderService orderService;
    private final OrderProductServiceImpl orderProductService;
    private final UserService userService;
    private final ProductDiscountService productDiscountService;
    private final DiscountService discountService;
    private final ProductRepository productRepository;
    private final OrderProductMapper orderProductMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final AddressService addressService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final TableMapper tableMapper;
    private final OrderDiscountService orderDiscountService;
    private final UserAddressService userAddressService;
    private final GenericResponseService responseBuilder;

    public OrderProductAndUserServiceImpl(OrderService orderService, OrderProductServiceImpl orderProductService, UserService userService, ProductDiscountService productDiscountService, DiscountService discountService, ProductRepository productRepository, OrderProductMapper orderProductMapper, ProductMapper productMapper, @Qualifier("productServiceImpl") ProductService productService, AddressService addressService, OrderMapper orderMapper, AddressMapper addressMapper, TableMapper tableMapper, OrderDiscountService orderDiscountService, UserAddressService userAddressService, GenericResponseService responseBuilder) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.userService = userService;
        this.productDiscountService = productDiscountService;
        this.discountService = discountService;
        this.productRepository = productRepository;
        this.orderProductMapper = orderProductMapper;
        this.productMapper = productMapper;
        this.productService = productService;
        this.addressService = addressService;
        this.orderMapper = orderMapper;
        this.addressMapper = addressMapper;
        this.tableMapper = tableMapper;
        this.orderDiscountService = orderDiscountService;
        this.userAddressService = userAddressService;
        this.responseBuilder = responseBuilder;
    }


    //1 check the user is register
    //2 check the request from restaurant/outside
    //3 check the discount codes
    //build a price based on discount
    //4 save request
    //5 return all data info
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderProductResponseWithPayloadDto createBulk(OrderProductRequestWithPayloadDto orderRequestDtoWithPayloadDto) {

        Order.PaymentMethod paymentMethod = orderRequestDtoWithPayloadDto.getPaymentMethod();
        boolean orderInRestaurant = orderRequestDtoWithPayloadDto.isOrderInRestaurant();
        boolean existDiscountCodes = orderRequestDtoWithPayloadDto.isExistDiscountCodes();
        String productDiscountCode = orderRequestDtoWithPayloadDto.getProductDiscountCode();
        String globalDiscountCode = orderRequestDtoWithPayloadDto.getGlobalDiscountCode();

        OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto =
                OrderProductResponseWithPayloadDto.builder()
                        .orderInRestaurant(orderInRestaurant)
                        .otp(orderRequestDtoWithPayloadDto.getOtp())
                        .existDiscountCodes(existDiscountCodes)
                        .build();


        Order order = Order.builder()
                .paymentMethod(paymentMethod)
                .otp(orderRequestDtoWithPayloadDto.getOtp())
                .build();

        if (orderRequestDtoWithPayloadDto.getPhoneNumber() != null) {
            order.setPhoneNumber(orderRequestDtoWithPayloadDto.getPhoneNumber());
            orderProductResponseWithPayloadDto.setPhoneNumber(order.getPhoneNumber());
        }


        checkTheUserIsRegistered(orderRequestDtoWithPayloadDto, order, orderProductResponseWithPayloadDto);


        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0));
        AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.of(0, 0, 0, 0));
        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
        List<OrderProductRequestDTO> orderProductRequestDTO = orderRequestDtoWithPayloadDto.getOrderProductRequestDTO();

        List<OrderProduct> orderProducts = getOrderProductsAndSetProductsForOrderAndCountTotalCookingTimeAndTotalPriceAndAddToProductResponseDTOList(orderProductRequestDTO, order, totalPrice, totalCookingTime, productResponseDTOS, existDiscountCodes, productDiscountCode);


        BigDecimal globalDiscountAmount = BigDecimal.ZERO;
        BigDecimal productDiscountAmount = BigDecimal.ZERO;
        order.setTotalPrice(totalPrice.get());

        checkTheOrderIsInRestaurant(order, orderRequestDtoWithPayloadDto, orderProductResponseWithPayloadDto);

        OrderDiscount orderDiscount = handleDiscountCodes(existDiscountCodes, globalDiscountCode, productDiscountCode, globalDiscountAmount, productDiscountAmount, totalPrice, order, orderProductResponseWithPayloadDto, productResponseDTOS);
        Order savedOrder = orderService.create(order);
        if (orderDiscount != null) {
            orderDiscountService.save(orderDiscount);
        }
        OrderResponseDTO responseDTO = handleOrderResponse(savedOrder, totalCookingTime, totalPrice, productResponseDTOS);

        orderProductResponseWithPayloadDto.setOrderResponseDTO(responseDTO);

        orderProductService.createAll(orderProducts);

        orderProductService.sendOrdersFromWebsocket(orderProductResponseWithPayloadDto);

        return orderProductResponseWithPayloadDto;
    }

    private OrderResponseDTO handleOrderResponse(Order savedOrder, AtomicReference<LocalTime> totalCookingTime, AtomicReference<BigDecimal> totalPrice, List<ProductResponseDTO> productResponseDTOS) {
        OrderResponseDTO responseDTO = orderMapper.toResponseDTO(savedOrder);
        responseDTO.setTotalCookingTime(totalCookingTime.get());
        BigDecimal roundedValue = totalPrice.get().setScale(2, RoundingMode.HALF_UP);
        responseDTO.setTotalPrice(roundedValue);
        responseDTO.setProducts(productResponseDTOS);
        return responseDTO;
    }

    private OrderDiscount handleDiscountCodes(boolean existDiscountCodes,
                                              String globalDiscountCode,
                                              String productDiscountCode,
                                              BigDecimal globalDiscountAmount,
                                              BigDecimal productDiscountAmount,
                                              AtomicReference<BigDecimal> totalPrice,
                                              Order order,
                                              OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto,
                                              List<ProductResponseDTO> productResponseDTOS) {
        totalPrice.get();
        BigDecimal finalTotalPrice;
        OrderDiscount orderDiscount = null;
        if (existDiscountCodes) {
            if (globalDiscountCode != null && !globalDiscountCode.isEmpty()) {
                Discount discountByCode = discountService.getDiscountByCode(globalDiscountCode);

                BigDecimal globalDiscountPercentage = discountByCode.getDiscount();

                // Calculate the global discount amount
                globalDiscountAmount = totalPrice.get()
                        .multiply(globalDiscountPercentage)
                        .divide(new BigDecimal(100), RoundingMode.HALF_UP); // (total price * discount%) / 100

                orderDiscount = OrderDiscount.builder()
                        .order(order)
                        .discount(discountByCode)
                        .build();
                orderProductResponseWithPayloadDto.setGlobalDiscountCode(globalDiscountCode);
            }
            if (productDiscountCode != null && !productDiscountCode.isEmpty()) {
                productDiscountAmount = handleProductDiscountCode(productDiscountCode, productDiscountAmount, orderProductResponseWithPayloadDto, productResponseDTOS);
            }
            finalTotalPrice = totalPrice.get()
                    .subtract(globalDiscountAmount)
                    .subtract(productDiscountAmount);
            totalPrice.set(finalTotalPrice);
            order.setTotalPrice(finalTotalPrice);
            return orderDiscount;
        }
        return orderDiscount;
    }

    private BigDecimal handleProductDiscountCode(String productDiscountCode, BigDecimal productDiscountAmount, OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto, List<ProductResponseDTO> productResponseDTOS) {
        // Retrieve the product and global discount details
        ProductDiscount productDiscountByCode = productDiscountService.getProductDiscountByCode(productDiscountCode);

        // Calculate the product discount percentage
        BigDecimal productDiscountPercentage = productDiscountByCode.getDiscount();

        // Apply product-specific discounts (e.g., per applicable product)
        productDiscountAmount = addToProductDiscountAmount(productResponseDTOS, productDiscountPercentage, productDiscountAmount);

        orderProductResponseWithPayloadDto.setProductDiscountCode(productDiscountCode);
        return productDiscountAmount;
    }

    private void checkTheUserIsRegistered(OrderProductRequestWithPayloadDto requestDtoWithPayloadDto, Order order, OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto) {
        if (requestDtoWithPayloadDto.isUserRegistered()) {
            UUID userUUID;
            if ((userUUID = requestDtoWithPayloadDto.getUserUUID()) != null) {
                User user = userService.findByUUID(userUUID);
                order.setUser(user);
                orderProductResponseWithPayloadDto.setUserUUID(userUUID);
            }
        }
    }

    private void checkTheOrderIsInRestaurant(Order order, OrderProductRequestWithPayloadDto request, OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto) {
        if (request.isOrderInRestaurant()) {
            Table table = orderProductService.getByNumber(request.getTableRequestDTO().getNumber());
            order.setTable(table);
            orderProductResponseWithPayloadDto.setTableResponseDTO(tableMapper.toResponseDTO(table));
            if (order.hasUser()) {
                orderProductResponseWithPayloadDto.setUserUUID(request.getUserUUID());
            }
        } else {
            if (order.hasUser()) {
                User user = order.getUser();
                if (user.hasAddress()) {
                    Address address = user.getAddress();
                    AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
                    responseDto.setUserUUID(user.getUuid());
                    order.setAddress(address);
                    orderProductResponseWithPayloadDto.setAddressResponseDTO(responseDto);
                } else {
                    AddressRequestDTO addressRequestDTO = request.getAddressRequestDTO();
                    Address address = userAddressService.saveAddressToUser(addressRequestDTO, user.getUuid());
                    AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
                    order.setAddress(address);
                    orderProductResponseWithPayloadDto.setAddressResponseDTO(responseDto);
                }
            } else {
                AddressRequestDTO addressRequestDTO = request.getAddressRequestDTO();
                Address address = addressMapper.toEntity(addressRequestDTO);
                order.setAddress(address);
                addressService.save(address);
                AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
                orderProductResponseWithPayloadDto.setAddressResponseDTO(responseDto);
            }
        }
    }


    private BigDecimal addToProductDiscountAmount(List<ProductResponseDTO> productResponseDTOS, BigDecimal productDiscountPercentage, BigDecimal productDiscountAmount) {
        for (ProductResponseDTO productRequest : productResponseDTOS) {
            BigDecimal productPrice = productRequest.getPrice();
            BigDecimal productTotalPrice = productPrice.multiply(new BigDecimal(productRequest.getQuantity()));

            // Calculate the discount for this product
            BigDecimal productDiscount = productTotalPrice
                    .multiply(productDiscountPercentage)
                    .divide(new BigDecimal(100));
            productDiscountAmount = productDiscountAmount.add(productDiscount);
        }
        return productDiscountAmount;
    }


    private List<OrderProduct>
    getOrderProductsAndSetProductsForOrderAndCountTotalCookingTimeAndTotalPriceAndAddToProductResponseDTOList
            (List<OrderProductRequestDTO> requestDTOs,
             Order order,
             AtomicReference<BigDecimal> totalPrice,
             AtomicReference<LocalTime> totalCookingTime,
             List<ProductResponseDTO> productResponseDTOList,
             boolean existDiscountCodes,
             String productDiscountCode) {
        return requestDTOs.stream()
                .map(requestDTO -> {
                    Product product = productService.getSimpleById(requestDTO.getProductId());
                    OrderProduct orderProduct = createOrderProduct(order, requestDTO, product, existDiscountCodes, productDiscountCode);
                    countTotalPrice(totalPrice, product, requestDTO.getQuantity());
                    countTotalCookingTime(totalCookingTime, product);
                    ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(product);
                    countQuantity(requestDTO, productResponseDTO);
                    productResponseDTOList.add(productResponseDTO);
                    return orderProduct;
                })
                .collect(Collectors.toList());
    }

    private OrderProduct createOrderProduct(Order order,
                                            OrderProductRequestDTO requestDTO,
                                            Product product,
                                            boolean existDiscountCodes,
                                            String productDiscountCode) {
        OrderProduct orderProduct = orderProductMapper.toEntity(requestDTO);
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        if (existDiscountCodes) {
            ProductDiscount productDiscountByCode = productDiscountService.getProductDiscountByCode(productDiscountCode);
            if (productDiscountByCode.getProduct().equals(product) &&
                    productDiscountByCode.getValidTo().isAfter(productDiscountByCode.getValidFrom())
                    && !LocalDateTime.now().isAfter(productDiscountByCode.getValidTo())) {

                BigDecimal discountPercentage = productDiscountByCode.getDiscount();
                BigDecimal price = product.getPrice();

                BigDecimal discount = price.multiply(discountPercentage).divide(new BigDecimal(100), RoundingMode.HALF_UP);

                BigDecimal resultPrice = price.subtract(discount);
                orderProduct.setPriceWithDiscount(resultPrice);
            }
        }
        return orderProduct;
    }


    private LocalTime countTotalCookingTime(AtomicReference<LocalTime> totalCookingTime, Product product) {
        return totalCookingTime.updateAndGet(t -> t.plusMinutes(product.getCookingTime().getMinute())
                .plusSeconds(product.getCookingTime().getSecond()));
    }

    private BigDecimal countTotalPrice(AtomicReference<BigDecimal> totalPrice, Product product, Integer quantity) {
        return totalPrice.updateAndGet(v -> v.add(product.getPrice().multiply(new BigDecimal(quantity))));
    }

    private void countQuantity(OrderProductRequestDTO requestDTO, ProductResponseDTO productResponseDTO) {
        productResponseDTO.setQuantity(requestDTO.getQuantity());
    }
}
