package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.OrderProductRequestDtoWithPayloadDto;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.OrderProductMapper;
import org.test.restaurant_service.mapper.ProductMapper;
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


    //TODO finish it
    //1 check the user is register
    //2 check the request from restaurant/outside
    //3 check the discount codes
    //build a price based on discount
    //4 save request
    //5 return all data info
    @Override
    public void createBulk(OrderProductRequestDtoWithPayloadDto requestDtoWithPayloadDto) {

        AddressRequestDTO addressRequestDTO = requestDtoWithPayloadDto.getAddressRequestDTO();
        TableRequestDTO tableRequestDTO = requestDtoWithPayloadDto.getTableRequestDTO();
        Order.PaymentMethod paymentMethod = requestDtoWithPayloadDto.getPaymentMethod();
        boolean orderInRestaurant = requestDtoWithPayloadDto.isOrderInRestaurant();
        boolean existDiscountCodes = requestDtoWithPayloadDto.isExistDiscountCodes();
        String productDiscountCode = requestDtoWithPayloadDto.getProductDiscountCode();
        String globalDiscountCode = requestDtoWithPayloadDto.getGlobalDiscountCode();


        if (addressRequestDTO.isRegisterUser() && orderInRestaurant) {
            User user = userService.findById(addressRequestDTO.getUserId());
            Table table = orderProductService.existTable(tableRequestDTO.getNumber());


            AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0));
            AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.of(0, 0, 0, 0));
            List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
            List<OrderProductRequestDTO> orderProductRequestDTO = requestDtoWithPayloadDto.getOrderProductRequestDTO();

            List<OrderProduct> orderProducts = getOrderProductsAndSetProductsForOrderAndCountTotalCookingTimeAndTotalPriceAndAddToProductResponseDTOList(orderProductRequestDTO, order, totalPrice, totalCookingTime, productResponseDTOS);
            if (existDiscountCodes) {
                // Retrieve the product and global discount details
                ProductDiscount productDiscountByCode = productDiscountService.getProductDiscountByCode(productDiscountCode);
                Discount discountByCode = discountService.getDiscountByCode(globalDiscountCode);

                // Calculate the global discount percentage
                BigDecimal globalDiscountPercentage = discountByCode.getDiscount();

                // Calculate the global discount amount
                BigDecimal globalDiscountAmount = totalPrice.get()
                        .multiply(globalDiscountPercentage)
                        .divide(new BigDecimal(100)); // (total price * discount%) / 100

                // Calculate the product discount percentage
                BigDecimal productDiscountPercentage = productDiscountByCode.getDiscount();

                // Apply product-specific discounts (e.g., per applicable product)
                BigDecimal productDiscountAmount = BigDecimal.ZERO;
                productDiscountAmount = addToProductDiscountAmount(productResponseDTOS, productDiscountPercentage, productDiscountAmount);

                // Calculate the final total price after discounts
                BigDecimal finalTotalPrice = totalPrice.get()
                        .subtract(globalDiscountAmount)
                        .subtract(productDiscountAmount);

                // Update the total price
                totalPrice.set(finalTotalPrice);
            }
            order.setTotalPrice(totalPrice.get());
            Order savedOrder = orderService.create(order);
            orderProductService.createAll(orderProducts);

            orderProductService.sendOrdersFromWebsocket();

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
