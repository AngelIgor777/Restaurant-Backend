package org.test.restaurant_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.request.table.TableOrdersPriceInfo;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.dto.response.order.ConcreteOrderId;
import org.test.restaurant_service.dto.response.order.OrderId;
import org.test.restaurant_service.dto.response.order.TotalOrders;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.*;
import org.test.restaurant_service.repository.OrderRepository;
import org.test.restaurant_service.service.OrderDiscountService;
import org.test.restaurant_service.service.OrderProductService;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.impl.cache.TableCacheService;
import org.test.restaurant_service.service.impl.cache.TotalOrdersCacheService;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderDiscountService orderDiscountService;
    private final OrderProductService orderProductService;
    private final AddressMapperImpl addressMapperImpl;
    private final ProductMapper productMapperImpl;
    private final TableMapperImpl tableMapperImpl;
    private final WebSocketSender webSocketSender;
    private final TableCacheService tableCacheService;
    private final TableOrderScoreService tableOrderScoreService;
    private final TotalOrdersCacheService totalOrdersCacheService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderDiscountService orderDiscountService, OrderProductService orderProductService, AddressMapperImpl addressMapperImpl, ProductMapperImpl productMapperImpl, TableMapperImpl tableMapperImpl, WebSocketSender webSocketSender, TableCacheService tableCacheService, TableOrderScoreService tableOrderScoreService, TotalOrdersCacheService totalOrdersCacheService, TotalOrdersCacheService totalOrdersCacheService1) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderDiscountService = orderDiscountService;
        this.orderProductService = orderProductService;
        this.addressMapperImpl = addressMapperImpl;
        this.productMapperImpl = productMapperImpl;
        this.tableMapperImpl = tableMapperImpl;
        this.webSocketSender = webSocketSender;
        this.tableCacheService = tableCacheService;
        this.tableOrderScoreService = tableOrderScoreService;
        this.totalOrdersCacheService = totalOrdersCacheService1;
    }

    @Override
    public boolean existsByOtp(String otp) {
        return orderRepository.existsByOtp(otp);
    }

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> createAll(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
    }

    @Override
    public void delete(int id, Integer tableId, Order.OrderStatus status) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id " + id);
        }
        orderRepository.deleteById(id);
        if (tableId != null) {
            tableCacheService.deleteOrderIdFromTable(tableId, id, status);
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto(Order.OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return orderRepository.findAllByStatusAndCreatedAtBetween(status, from, to, pageable)
                .stream()
                .map(order -> getOrderProductResponseWithPayloadDto(order, false))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto(Order.OrderStatus status, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findAllByStatusAndCreatedAtBetween(status, from, to)
                .stream()
                .map(order -> getOrderProductResponseWithPayloadDto(order, false))
                .toList();
    }

    @Override
    public OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Integer id) {
        return getOrderProductResponseWithPayloadDto(orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id)), false);
    }

    @Override
    public List<Order> getAllOrdersByPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepository.findAllByCreatedAtBetweenAndStatus(from, to, Order.OrderStatus.CONFIRMED);
    }

    @Override
    public void completeOrder(Integer orderId, Integer tableId) {
        Order orderById = getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.COMPLETED);
        orderRepository.save(orderById);
        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        TableOrderInfo tableOrderInfo;
        if (tableId != null) {
            tableOrderInfo = tableCacheService.
                    changeOrderStateForTable(orderId, tableId, Order.OrderStatus.PENDING, Order.OrderStatus.COMPLETED);
            ordersStatesCount.setTablesOrderInfo(List.of(tableOrderInfo));
        }
        TotalOrders totalOrders = totalOrdersCacheService.updateOrderStatus(() -> orderId, Order.OrderStatus.PENDING, Order.OrderStatus.COMPLETED);
        ordersStatesCount.setTotalOrders(totalOrders);

        webSocketSender.sendOrdersStateCount(ordersStatesCount);
    }

    @Override
    public void confirmOrder(Integer orderId, UUID sessionUUID, Order.OrderStatus from) {
        Order orderById = getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.CONFIRMED);
        orderById.setOtp(null);
        orderRepository.save(orderById);
        TableOrderInfo tableOrderInfo = null;
        if (sessionUUID != null) {
            tableOrderScoreService.save(orderById.getTable(), orderById, sessionUUID);
            tableOrderInfo = tableCacheService.changeOrderStateForTable(orderId, orderById.getTable().getId(), from, Order.OrderStatus.CONFIRMED);
        }
        TotalOrders totalOrders = totalOrdersCacheService.updateOrderStatus(() -> orderId, from, Order.OrderStatus.CONFIRMED);
        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        ordersStatesCount.setTotalOrders(totalOrders);
        if (tableOrderInfo != null) {
            ordersStatesCount.setTablesOrderInfo(List.of(tableOrderInfo));
        }

        webSocketSender.sendOrdersStateCount(ordersStatesCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderProductResponseWithPayloadDto> getAllUserOrdersProductResponseWithPayloadDto(UUID userUUID, Pageable pageable) {
        List<Order> ordersByUserUuid = orderRepository.findByUser_UuidOrderByCreatedAtDesc(userUUID, pageable);
        return new ArrayList<>(ordersByUserUuid.stream()
                .map(order -> {
                    return getOrderProductResponseWithPayloadDto(order, true);
                })
                .toList());
    }

    @Override
    public Integer getCountOrdersByUserChatId(Long chatId) {
        return orderRepository.countAllByUser_TelegramUserEntity_ChatId(chatId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus status, LocalDateTime from, LocalDateTime to) {
       return orderRepository.deleteByStatusAndCreatedAtBetweenReturningIds(status.name(), from, to);
    }

    @Override
    public OrderProductResponseWithPayloadDto searchOrderProductResponseWithPayloadDtoByValidationCode(String query) {
        Order order = orderRepository.findByOtp(query)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with OTP " + query));
        return getOrderProductResponseWithPayloadDto(order, false);
    }

    @Override
    public List<OrderProductResponseWithPayloadDto> searchOrdersWithPayloadDtoById(List<Integer> ids) {
        return orderRepository.findByIdIn(ids).stream()
                .map(order -> getOrderProductResponseWithPayloadDto(order, false))
                .toList();
    }

    @Override
    public OrdersStatesCount getOrdersStatesCount() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWorkDay = today.atTime(LocalTime.of(0, 0));

        LocalDateTime endOfWorkDay = today.atTime(LocalTime.of(23, 59));
        List<OrderId> pending = orderRepository.findAllIdsByCreatedAtBetweenAndStatus(startOfWorkDay, endOfWorkDay, Order.OrderStatus.PENDING.name());
        List<OrderId> confirmed = orderRepository.findAllIdsByCreatedAtBetweenAndStatus(startOfWorkDay, endOfWorkDay, Order.OrderStatus.CONFIRMED.name());
        List<OrderId> completed = orderRepository.findAllIdsByCreatedAtBetweenAndStatus(startOfWorkDay, endOfWorkDay, Order.OrderStatus.COMPLETED.name());

        List<ConcreteOrderId> pendingConverted = pending.stream().map(id -> new ConcreteOrderId(id.getId())).collect(Collectors.toList());
        List<ConcreteOrderId> confirmedConverted = confirmed.stream().map(id -> new ConcreteOrderId(id.getId())).collect(Collectors.toList());
        List<ConcreteOrderId> completedConverted = completed.stream().map(id -> new ConcreteOrderId(id.getId())).collect(Collectors.toList());

        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();

        TotalOrders totalOrders = new TotalOrders();
        totalOrders.setTotalPendingOrdersId(pendingConverted);
        totalOrders.setTotalConfirmedOrdersId(confirmedConverted);
        totalOrders.setTotalCompletedOrdersId(completedConverted);

        totalOrdersCacheService.setTotalOrders(totalOrders);

        List<TableOrderInfo> allTableOrderInfos = tableCacheService.getAllTableOrderInfos();

        ordersStatesCount.setTablesOrderInfo(allTableOrderInfos);
        ordersStatesCount.setTotalOrders(totalOrders);

        return ordersStatesCount;
    }

    public OrderProductResponseWithPayloadDto getOrderProductResponseWithPayloadDto(Order order, boolean useProductPhoto) {
        log.debug("Handle order: {}", order);
        OrderProductResponseWithPayloadDto response = new OrderProductResponseWithPayloadDto();
        response.setOtp(order.getOtp());
        if (order.hasPhoneNumber()) {
            response.setPhoneNumber(order.getPhoneNumber());
        }
        OrderResponseDTO orderResponseDTO = orderMapper.toResponseDTO(order);
        AtomicReference<LocalTime> totalCookingTime = new AtomicReference<>(LocalTime.MIN);

        List<ProductResponseDTO> productResponseDTOS = orderProductService.getOrderProductsByOrderId(order.getId())
                .stream()
                .map(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    totalCookingTime.updateAndGet(time -> time.plusMinutes((long) product.getCookingTime().getMinute() * orderProduct.getQuantity()));
                    ProductResponseDTO productResponseDTO;
                    if (!useProductPhoto) {
                        productResponseDTO = productMapperImpl.toResponseIgnorePhotos(product);
                    } else {
                        productResponseDTO = productMapperImpl.toResponseDTO(product);
                    }
                    productResponseDTO.setQuantity(orderProduct.getQuantity());
                    return productResponseDTO;
                }).toList();

        orderResponseDTO.setTotalCookingTime(totalCookingTime.get());
        orderResponseDTO.setProducts(productResponseDTOS);


        if (order.hasUser()) {
            response.setUserUUID(order.getUser().getUuid());
        }
        if (order.orderInRestaurant()) {
            response.setOrderInRestaurant(true);
            Table table = order.getTable();
            TableResponseDTO responseDTO = tableMapperImpl.toResponseDTO(table);
            response.setTableResponseDTO(responseDTO);

        } else if (order.orderOutRestaurant()) {
            Address address = order.getAddress();
            AddressResponseDTO responseDto = addressMapperImpl.toResponseDto(address);
            response.setAddressResponseDTO(responseDto);
        }
        if (orderDiscountService.existsByOrderId(order.getId())) {
            OrderDiscount orderDiscount = orderDiscountService.findByOrderId(order.getId());

            response.setExistDiscountCodes(true);
            response.setGlobalDiscountCode(orderDiscount.getDiscount().getCode());
        }
        response.setOrderResponseDTO(orderResponseDTO);
        return response;
    }

    public TableOrdersPriceInfo countPriceForTable(Integer tableId) {
        TableOrderInfo orders = tableCacheService.getTableOrders(tableId);
        BigDecimal totalPrice = orders.getConfirmedOrders().stream()
                .map(this::getOrderById)
                .filter(Objects::nonNull)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        tableCacheService.deleteTableOrders(tableId);
        tableCacheService.deleteTableFromClosed(tableId);

        OpenTables openTables = tableCacheService.getOpenTables();
        openTables.getIds().remove(tableId);
        tableCacheService.saveOpenTables(openTables);

        webSocketSender.sendOpenTables(openTables);
        UUID sessionUUID = tableCacheService.deleteSessionUUID(tableId);
        return new TableOrdersPriceInfo(totalPrice, sessionUUID);
    }
}
