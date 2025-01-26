package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.response.StatisticsResultResponseDto;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.mapper.ProductMapperImpl;
import org.test.restaurant_service.service.OrderProductService;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.StatisticsService;
import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ProductService productService;
    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductMapperImpl productMapper;

    @Override
    public StatisticsResultResponseDto getStatistics(LocalDateTime from, LocalDateTime to) {
        // Fetch orders for the specified period
        List<Order> orders = orderService.getAllOrdersByPeriod(from, to);

        if (orders.isEmpty()) {
            log.warn("No orders found");
            throw new BadRequestException("No orders found");
        }

        // Initialize result DTOs
        StatisticsResultResponseDto statisticsResult = StatisticsResultResponseDto.create();
        statisticsResult.setFrom(from);
        statisticsResult.setTo(to);
        StatisticsResultResponseDto.TotalRevenueBasedOrdersDto revenueOrdersDto = StatisticsResultResponseDto.TotalRevenueBasedOrdersDto.create();

        // Compute aggregate values
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        int totalOrders = orders.size();
        long totalDays = calculateDaysBetween(from, to);
        int ordersPerDay = totalDays > 0 ? (int) (totalOrders / totalDays) : 0;

        AtomicInteger ordersInRestaurant = new AtomicInteger(0);
        AtomicInteger ordersOutRestaurant = new AtomicInteger(0);

        ConcurrentHashMap<ProductResponseDTO, Integer> productSales = calculateProductSales(orders, ordersInRestaurant, ordersOutRestaurant);

        // Build Product Sales DTOs
        List<StatisticsResultResponseDto.ProductSalesResponseDto> productSalesResponseDtos = productSales.entrySet().stream()
                .map(entry -> buildProductSalesResponseDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Set results in DTOs
        revenueOrdersDto.setTotalOrders(totalOrders);
        revenueOrdersDto.setTotalRevenue(totalRevenue);
        revenueOrdersDto.setAvgRevenuePerOrder(totalRevenue.divide(BigDecimal.valueOf(totalOrders), BigDecimal.ROUND_HALF_UP));

        statisticsResult.setTotalRevenueBasedOrdersDto(revenueOrdersDto);
        statisticsResult.setProductSalesResponseDto(productSalesResponseDtos);

        return statisticsResult;
    }

    private BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ConcurrentHashMap<ProductResponseDTO, Integer> calculateProductSales(List<Order> orders, AtomicInteger inRestaurant, AtomicInteger outRestaurant) {
        ConcurrentHashMap<ProductResponseDTO, Integer> productSales = new ConcurrentHashMap<>();

        orders.forEach(order -> {
            if (order.isOrderInRestaurant()) {
                inRestaurant.incrementAndGet();
            } else if (order.isOrderOutRestaurant()) {
                outRestaurant.incrementAndGet();
            }

            orderProductService.getOrderProductsByOrderId(order.getId()).forEach(orderProduct -> {
                ProductResponseDTO productDTO = productMapper.toResponseDTO(orderProduct.getProduct());
                productSales.merge(productDTO, orderProduct.getQuantity(), Integer::sum);
            });
        });

        return productSales;
    }

    private StatisticsResultResponseDto.ProductSalesResponseDto buildProductSalesResponseDto(ProductResponseDTO productResponseDTO, int totalQuantitySold) {
        StatisticsResultResponseDto.ProductSalesResponseDto dto = StatisticsResultResponseDto.ProductSalesResponseDto.create();
        dto.setProductResponseDTO(productResponseDTO);
        dto.setTotalQuantitySold(totalQuantitySold);
        return dto;
    }

    private long calculateDaysBetween(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate());
    }
}
