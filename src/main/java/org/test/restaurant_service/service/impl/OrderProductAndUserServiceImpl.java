package org.test.restaurant_service.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.feats.Features;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadAndPrintRequestDto;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.dto.response.order.TotalOrders;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.*;
import org.test.restaurant_service.service.*;
import org.test.restaurant_service.service.impl.cache.TableCacheService;
import org.test.restaurant_service.service.impl.cache.TotalOrdersCacheService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private final OrderProductMapper orderProductMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final AddressService addressService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final TableMapper tableMapper;
    private final OrderDiscountService orderDiscountService;
    private final UserAddressService userAddressService;
    private final PrinterService printerService;
    private final WebSocketSender webSocketSender;
    private final TableCacheService tableCacheService;
    private final TableService tableService;
    private final TotalOrdersCacheService totalOrdersCacheService;
    private final FeatureService featureService;

    public OrderProductAndUserServiceImpl(OrderService orderService, OrderProductServiceImpl orderProductService, UserService userService, ProductDiscountService productDiscountService, DiscountService discountService, OrderProductMapper orderProductMapper, ProductMapper productMapper, @Qualifier("productServiceImpl") ProductService productService, AddressService addressService, OrderMapper orderMapper, AddressMapper addressMapper, TableMapper tableMapper, OrderDiscountService orderDiscountService, UserAddressService userAddressService, PrinterService printerService, WebSocketSender webSocketSender, TableCacheService tableCacheService, TableService tableService, TotalOrdersCacheService totalOrdersCacheService, FeatureService featureService, FeatureService featureService1) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.userService = userService;
        this.productDiscountService = productDiscountService;
        this.discountService = discountService;
        this.orderProductMapper = orderProductMapper;
        this.productMapper = productMapper;
        this.productService = productService;
        this.addressService = addressService;
        this.orderMapper = orderMapper;
        this.addressMapper = addressMapper;
        this.tableMapper = tableMapper;
        this.orderDiscountService = orderDiscountService;
        this.userAddressService = userAddressService;
        this.printerService = printerService;
        this.webSocketSender = webSocketSender;
        this.tableCacheService = tableCacheService;
        this.tableService = tableService;
        this.totalOrdersCacheService = totalOrdersCacheService;
        this.featureService = featureService1;
    }

    //1 check the user is register
    //2 check the request from restaurant/outside
    //3 check the discount codes
    //build a price based on discount
    //4 save request
    //5 return all data info
    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T extends OrderProductWithPayloadRequestDto> void createOrder(T requestDto) {
        Order order = Order.builder()
                .paymentMethod(requestDto.getPaymentMethod())
                .otp(requestDto.getOtp())
                .build();
        OrderProductWithPayloadAndPrintRequestDto requestDtoForPrint = null;
        Order.OrderStatus orderStatus = null;
        boolean isOrderOfAdmin = requestDto instanceof OrderProductWithPayloadAndPrintRequestDto;
        if (!isOrderOfAdmin && !featureService.getFeatureStatus(Features.ORDERING).isEnabled()) {
            throw new IllegalStateException("Заказы отключены");
        }

        if (!featureService.getFeatureStatus(Features.COUPONS).isEnabled()) {
            requestDto.setExistDiscountCodes(false);
        }

        if (isOrderOfAdmin) {
            requestDtoForPrint = (OrderProductWithPayloadAndPrintRequestDto) requestDto;
            orderStatus = requestDtoForPrint.getOrderStatus();
            if (orderStatus != null) {
                order.setStatus(orderStatus);
            }
        }

        boolean orderInRestaurant = requestDto.isOrderInRestaurant();
        boolean existDiscountCodes = requestDto.isExistDiscountCodes();
        String productDiscountCode = requestDto.getProductDiscountCode();
        String globalDiscountCode = requestDto.getGlobalDiscountCode();

        OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto =
                OrderProductResponseWithPayloadDto.builder()
                        .orderInRestaurant(orderInRestaurant)
                        .otp(requestDto.getOtp())
                        .existDiscountCodes(existDiscountCodes)
                        .build();


        if (requestDto.getPhoneNumber() != null) {
            order.setPhoneNumber(requestDto.getPhoneNumber());
            orderProductResponseWithPayloadDto.setPhoneNumber(order.getPhoneNumber());
        }


        checkTheUserIsRegistered(requestDto, order, orderProductResponseWithPayloadDto);


        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0));
        AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.of(0, 0, 0, 0));
        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
        List<OrderProductRequestDTO> orderProductRequestDTO = requestDto.getOrderProductRequestDTO();

        List<OrderProduct> orderProducts = getOrderProductsAndSetProductsForOrderAndCountTotalCookingTimeAndTotalPriceAndAddToProductResponseDTOList(orderProductRequestDTO, order, totalPrice, totalCookingTime, productResponseDTOS, existDiscountCodes, productDiscountCode);


        BigDecimal globalDiscountAmount = BigDecimal.ZERO;
        BigDecimal productDiscountAmount = BigDecimal.ZERO;
        order.setTotalPrice(totalPrice.get());

        boolean isInRestaurant = checkTheOrderIsInRestaurant(order, requestDto, orderProductResponseWithPayloadDto);

        OrderDiscount orderDiscount = handleDiscountCodes(existDiscountCodes, globalDiscountCode, productDiscountCode, globalDiscountAmount, productDiscountAmount, totalPrice, order, orderProductResponseWithPayloadDto, productResponseDTOS);

        Order savedOrder = orderService.create(order);

        TableOrderInfo tableOrderInfo = null;
        if (isInRestaurant) {
            Set<Integer> ids = tableCacheService.getOpenTables().getIds();
            if (ids != null) {
                Table table = tableService.getByNumber(requestDto.getTableRequestDTO().getNumber());
                Integer tableId = table.getId();
                if (ids.contains(tableId)) {
                    // update Redis sets
                    tableOrderInfo = tableCacheService.addOrderIdToTable(savedOrder.getId(),
                            savedOrder.getStatus(),
                            tableId);
                }
            }
        }


        if (orderDiscount != null) {
            orderDiscountService.save(orderDiscount);
        }
        OrderResponseDTO responseDTO = handleOrderResponse(savedOrder, totalCookingTime, totalPrice, productResponseDTOS);

        orderProductResponseWithPayloadDto.setOrderResponseDTO(responseDTO);

        orderProductService.createAll(orderProducts);

        TotalOrders totalOrders = null;
        if (orderStatus != null) {
            if (orderStatus.equals(Order.OrderStatus.COMPLETED) || orderStatus.equals(Order.OrderStatus.CONFIRMED)) {
                totalOrders = totalOrdersCacheService.addCompletedOrder(savedOrder::getId);
                printerService.sendOrderToPrinter(savedOrder.getId(), requestDtoForPrint.getProductsIdForPrint(), order);
            }
        } else {
            totalOrders = totalOrdersCacheService.addPendingOrder(savedOrder::getId);
        }

        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        if (tableOrderInfo != null) {
            ordersStatesCount.setTablesOrderInfo(List.of(tableOrderInfo));
        }
        ordersStatesCount.setTotalOrders(totalOrders);

        webSocketSender.sendOrdersStateCount(ordersStatesCount);
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

        BigDecimal finalTotalPrice;
        OrderDiscount orderDiscount = null;
        if (existDiscountCodes) {
            if (globalDiscountCode != null && !globalDiscountCode.isEmpty()) {
                Discount discountByCode = discountService.getDiscountByCode(globalDiscountCode);

                BigDecimal globalDiscountPercentage = discountByCode.getDiscount();

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
        ProductDiscount productDiscountByCode = productDiscountService.getProductDiscountByCode(productDiscountCode);

        BigDecimal productDiscountPercentage = productDiscountByCode.getDiscount();

        productDiscountAmount = addToProductDiscountAmount(productResponseDTOS, productDiscountPercentage, productDiscountAmount);

        orderProductResponseWithPayloadDto.setProductDiscountCode(productDiscountCode);
        return productDiscountAmount;
    }

    private void checkTheUserIsRegistered(OrderProductWithPayloadRequestDto requestDtoWithPayloadDto, Order order, OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto) {
        if (requestDtoWithPayloadDto.isUserRegistered()) {
            UUID userUUID;
            if ((userUUID = requestDtoWithPayloadDto.getUserUUID()) != null) {
                User user = userService.findByUUID(userUUID);
                order.setUser(user);
                orderProductResponseWithPayloadDto.setUserUUID(userUUID);
            }
        }
    }

    private boolean checkTheOrderIsInRestaurant(Order order, OrderProductWithPayloadRequestDto request, OrderProductResponseWithPayloadDto orderProductResponseWithPayloadDto) {
        if (request.isOrderInRestaurant()) {
            Table table = tableService.getByNumber(request.getTableRequestDTO().getNumber());
            order.setTable(table);
            orderProductResponseWithPayloadDto.setTableResponseDTO(tableMapper.toResponseDTO(table));
            if (order.hasUser()) {
                orderProductResponseWithPayloadDto.setUserUUID(request.getUserUUID());
            }
            return true;
        } else {
            if (order.hasUser()) {
                User user = order.getUser();
                if (user.hasAddress() && request.getAddressRequestDTO() == null) {
                    Address address = user.getAddress();
                    AddressResponseDTO responseDto = addressMapper.toResponseDto(address);
                    responseDto.setUserUUID(user.getUuid());
                    order.setAddress(address);
                    orderProductResponseWithPayloadDto.setAddressResponseDTO(responseDto);
                } else if (user.hasAddress() && request.getAddressRequestDTO() != null) {
                    AddressRequestDTO addressRequestDTO = request.getAddressRequestDTO();
                    Address address = addressMapper.toEntity(addressRequestDTO);
                    userAddressService.updateAddressToUser(user, address);
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
            return false;
        }
    }


    private BigDecimal addToProductDiscountAmount(List<ProductResponseDTO> productResponseDTOS, BigDecimal productDiscountPercentage, BigDecimal productDiscountAmount) {
        for (ProductResponseDTO productRequest : productResponseDTOS) {
            BigDecimal productPrice = productRequest.getPrice();
            BigDecimal productTotalPrice = productPrice.multiply(new BigDecimal(productRequest.getQuantity()));

            // Calculate the discount for this product
            BigDecimal productDiscount = productTotalPrice
                    .multiply(productDiscountPercentage)
                    .divide(new BigDecimal(100), RoundingMode.HALF_UP);
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
        if (existDiscountCodes && productDiscountCode != null && !productDiscountCode.isEmpty()) {
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


    private void countTotalCookingTime(AtomicReference<LocalTime> totalCookingTime, Product product) {
        totalCookingTime.updateAndGet(t -> t.plusMinutes(product.getCookingTime().getMinute())
                .plusSeconds(product.getCookingTime().getSecond()));
    }

    private void countTotalPrice(AtomicReference<BigDecimal> totalPrice, Product product, Integer quantity) {
        totalPrice.updateAndGet(v -> v.add(product.getPrice().multiply(new BigDecimal(quantity))));
    }

    private void countQuantity(OrderProductRequestDTO requestDTO, ProductResponseDTO productResponseDTO) {
        productResponseDTO.setQuantity(requestDTO.getQuantity());
    }
}
