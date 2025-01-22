package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDtoWithPayloadDto;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.dto.response.OrderProductResponseDtoWithPayloadDto;
import org.test.restaurant_service.dto.response.OrderResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.*;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.*;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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


    //TODO finish it
    //1 check the user is register
    //2 check the request from restaurant/outside
    //3 check the discount codes
    //build a price based on discount
    //4 save request
    //5 return all data info
    @Override
    public OrderProductResponseDtoWithPayloadDto createBulk(OrderProductRequestDtoWithPayloadDto requestDtoWithPayloadDto) {

        AddressRequestDTO addressRequestDTO = requestDtoWithPayloadDto.getAddressRequestDTO();
        TableRequestDTO tableRequestDTO = requestDtoWithPayloadDto.getTableRequestDTO();
        Order.PaymentMethod paymentMethod = requestDtoWithPayloadDto.getPaymentMethod();
        boolean orderInRestaurant = requestDtoWithPayloadDto.isOrderInRestaurant();
        boolean existDiscountCodes = requestDtoWithPayloadDto.isExistDiscountCodes();
        String productDiscountCode = requestDtoWithPayloadDto.getProductDiscountCode();
        String globalDiscountCode = requestDtoWithPayloadDto.getGlobalDiscountCode();

        OrderProductResponseDtoWithPayloadDto orderProductResponseDtoWithPayloadDto =
                OrderProductResponseDtoWithPayloadDto.builder()
                        .orderInRestaurant(orderInRestaurant)
                        .existDiscountCodes(existDiscountCodes)
                        .build();


        Order order = Order.builder()
                .paymentMethod(paymentMethod)
                .build();


        checkTheUserIsRegistered(addressRequestDTO, order);


        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0));
        AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.of(0, 0, 0, 0));
        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
        List<OrderProductRequestDTO> orderProductRequestDTO = requestDtoWithPayloadDto.getOrderProductRequestDTO();

        List<OrderProduct> orderProducts = getOrderProductsAndSetProductsForOrderAndCountTotalCookingTimeAndTotalPriceAndAddToProductResponseDTOList(orderProductRequestDTO, order, totalPrice, totalCookingTime, productResponseDTOS);


        BigDecimal globalDiscountAmount = BigDecimal.ZERO;
        BigDecimal productDiscountAmount = BigDecimal.ZERO;
        if (existDiscountCodes) {
            if (globalDiscountCode != null && !globalDiscountCode.isEmpty()) {
                Discount discountByCode = discountService.getDiscountByCode(globalDiscountCode);

                // Calculate the global discount percentage
                BigDecimal globalDiscountPercentage = discountByCode.getDiscount();

                // Calculate the global discount amount
                globalDiscountAmount = totalPrice.get()
                        .multiply(globalDiscountPercentage)
                        .divide(new BigDecimal(100)); // (total price * discount%) / 100

                orderProductResponseDtoWithPayloadDto.setGlobalDiscountCode(globalDiscountCode);
            }
            if (productDiscountCode != null && !productDiscountCode.isEmpty()) {
                // Retrieve the product and global discount details
                ProductDiscount productDiscountByCode = productDiscountService.getProductDiscountByCode(productDiscountCode);

                // Calculate the product discount percentage
                BigDecimal productDiscountPercentage = productDiscountByCode.getDiscount();

                // Apply product-specific discounts (e.g., per applicable product)
                productDiscountAmount = addToProductDiscountAmount(productResponseDTOS, productDiscountPercentage, productDiscountAmount);

                orderProductResponseDtoWithPayloadDto.setProductDiscountCode(productDiscountCode);
            }
            BigDecimal finalTotalPrice = totalPrice.get()
                    .subtract(globalDiscountAmount)
                    .subtract(productDiscountAmount);
            totalPrice.set(finalTotalPrice);
        }
        order.setTotalPrice(totalPrice.get());
        Order savedOrder = orderService.create(order);

        OrderResponseDTO responseDTO = orderMapper.toResponseDTO(savedOrder);
        responseDTO.setTotalCookingTime(totalCookingTime.get());
        responseDTO.setTotalPrice(totalPrice.get());
        responseDTO.setProducts(productResponseDTOS);

        orderProductResponseDtoWithPayloadDto.setPayload(responseDTO);

        theOrderInRestaurant(orderInRestaurant, tableRequestDTO, order, addressRequestDTO, orderProductResponseDtoWithPayloadDto);

        orderProductService.createAll(orderProducts);

        orderProductService.sendOrdersFromWebsocket();


        return orderProductResponseDtoWithPayloadDto;
    }

    private void checkTheUserIsRegistered(AddressRequestDTO addressRequestDTO, Order order) {
        if (addressRequestDTO.isRegisterUser()) {
            User user = userService.findById(addressRequestDTO.getUserId());
            order.setUser(user);
        }
    }

    private boolean theOrderInRestaurant(boolean orderInRestaurant, TableRequestDTO tableRequestDTO, Order order, AddressRequestDTO addressRequestDTO, OrderProductResponseDtoWithPayloadDto orderProductResponseDtoWithPayloadDto) {
        if (orderInRestaurant) {
            Table table = orderProductService.getByNumber(tableRequestDTO.getNumber());
            order.setTable(table);
            orderProductResponseDtoWithPayloadDto.getPayload().setTableResponseDTO(tableMapper.toResponseDTO(table));
            return true;
        } else {
            Address address = Address.builder()
                    .city(addressRequestDTO.getCity())
                    .street(addressRequestDTO.getStreet())
                    .homeNumber(addressRequestDTO.getHomeNumber())
                    .apartmentNumber(addressRequestDTO.getApartmentNumber())
                    .build();

            if (order.getUser() != null) {
                address.setUser(order.getUser());
            }
            Address savedAddress = addressService.save(address);
            AddressResponseDTO responseDto = addressMapper.toResponseDto(savedAddress);
            responseDto.setUserId(addressRequestDTO.getUserId());
            responseDto.setRegisterUser(addressRequestDTO.isRegisterUser());


            orderProductResponseDtoWithPayloadDto.setAddressResponseDTO(responseDto);
            return false;
        }
    }

    private BigDecimal addToProductDiscountAmount(List<ProductResponseDTO> productResponseDTOS, BigDecimal productDiscountPercentage, BigDecimal productDiscountAmount) {
        for (ProductResponseDTO productRequest : productResponseDTOS) {
            BigDecimal productPrice = productRequest.getPrice();
            BigDecimal productTotal = productPrice.multiply(new BigDecimal(productRequest.getQuantity()));

            // Calculate the discount for this product
            BigDecimal productDiscount = productTotal
                    .multiply(productDiscountPercentage)
                    .divide(new BigDecimal(100));
            productDiscountAmount = productDiscountAmount.add(productDiscount);
        }
        return productDiscountAmount;
    }


    private List<OrderProduct> getOrderProductsAndSetProductsForOrderAndCountTotalCookingTimeAndTotalPriceAndAddToProductResponseDTOList(List<OrderProductRequestDTO> requestDTOs, Order order, AtomicReference<BigDecimal> totalPrice, AtomicReference<LocalTime> totalCookingTime, List<ProductResponseDTO> productResponseDTOList) {
        return requestDTOs.stream()
                .map(requestDTO -> {
                    Product product = productRepository.findById(requestDTO.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
                    OrderProduct orderProduct = createOrderProduct(order, requestDTO, product);
                    countTotalPrice(totalPrice, product);
                    countTotalCookingTime(totalCookingTime, product);
                    ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(product);
                    countQuantity(requestDTO, productResponseDTO);
                    productResponseDTOList.add(productResponseDTO);
                    return orderProduct;
                })
                .collect(Collectors.toList());
    }

    private OrderProduct createOrderProduct(Order order, OrderProductRequestDTO requestDTO, Product product) {
        OrderProduct orderProduct = orderProductMapper.toEntity(requestDTO);
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        return orderProduct;
    }


    private LocalTime countTotalCookingTime(AtomicReference<LocalTime> totalCookingTime, Product product) {
        return totalCookingTime.updateAndGet(t -> t.plusMinutes(product.getCookingTime().getMinute())
                .plusSeconds(product.getCookingTime().getSecond()));
    }

    private BigDecimal countTotalPrice(AtomicReference<BigDecimal> totalPrice, Product product) {
        return totalPrice.updateAndGet(v -> v.add(product.getPrice()));
    }

    private void countQuantity(OrderProductRequestDTO requestDTO, ProductResponseDTO productResponseDTO) {
        productResponseDTO.setQuantity(requestDTO.getQuantity());
    }
}
