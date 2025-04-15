package org.test.restaurant_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.request.OrderRequestDTO;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.request.table.TableOrderInfo;
import org.test.restaurant_service.dto.request.table.TableOrdersPriceInfo;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.mapper.AddressMapperImpl;
import org.test.restaurant_service.mapper.OrderMapper;
import org.test.restaurant_service.mapper.ProductMapperImpl;
import org.test.restaurant_service.mapper.TableMapperImpl;
import org.test.restaurant_service.repository.OrderRepository;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.OrderDiscountService;
import org.test.restaurant_service.service.OrderProductService;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.impl.cache.TableCacheService;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;
    private final OrderDiscountService orderDiscountService;
    private final OrderProductService orderProductService;
    private final AddressMapperImpl addressMapperImpl;
    private final ProductMapperImpl productMapperImpl;
    private final TableMapperImpl tableMapperImpl;
    private final PhotoService photoService;
    private final WebSocketSender webSocketSender;
    private final TableCacheService tableCacheService;

    public OrderServiceImpl(OrderRepository orderRepository, TableRepository tableRepository, OrderMapper orderMapper, OrderDiscountService orderDiscountService, OrderProductService orderProductService, AddressMapperImpl addressMapperImpl, ProductMapperImpl productMapperImpl, TableMapperImpl tableMapperImpl, @Qualifier("photoServiceImplS3") PhotoService photoService, WebSocketSender webSocketSender, TableCacheService tableCacheService) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.orderMapper = orderMapper;
        this.orderDiscountService = orderDiscountService;
        this.orderProductService = orderProductService;
        this.addressMapperImpl = addressMapperImpl;
        this.productMapperImpl = productMapperImpl;
        this.tableMapperImpl = tableMapperImpl;
        this.photoService = photoService;
        this.webSocketSender = webSocketSender;
        this.tableCacheService = tableCacheService;
    }

    @Override
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        Table table = tableRepository.findById(requestDTO.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id " + requestDTO.getTableId()));
        Order order = orderMapper.toEntity(requestDTO);
        order.setTable(table);
        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
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
    public OrderResponseDTO update(Integer id, OrderRequestDTO requestDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
        orderMapper.updateEntityFromRequestDTO(requestDTO, order);
        if (requestDTO.getTableId() != null) {
            Table table = tableRepository.findById(requestDTO.getTableId())
                    .orElseThrow(() -> new EntityNotFoundException("Table not found with id " + requestDTO.getTableId()));
            order.setTable(table);
        }
        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public void delete(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id " + id);
        }
        orderRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    @Override
    public List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto(Order.OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        List<OrderProductResponseWithPayloadDto> list = orderRepository.findAllByStatusAndCreatedAtBetween(status, from, to, pageable)
                .stream()
                .map(order -> getOrderProductResponseWithPayloadDto(order, false))
                .toList();
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderProductResponseWithPayloadDto> getAllOrdersProductResponseWithPayloadDto(Order.OrderStatus status, LocalDateTime from, LocalDateTime to) {
        List<OrderProductResponseWithPayloadDto> list = orderRepository.findAllByStatusAndCreatedAtBetween(status, from, to)
                .stream()
                .map(order -> getOrderProductResponseWithPayloadDto(order, false))
                .toList();
        return list;
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
    public void completeOrder(Integer orderId) {
        Order orderById = getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.COMPLETED);
        orderRepository.save(orderById);
        webSocketSender.sendPendingOrderIncrement(-1);
    }

    @Override
    public void confirmOrder(Integer orderId) {
        Order orderById = getOrderById(orderId);
        orderById.setStatus(Order.OrderStatus.CONFIRMED);
        orderById.setOtp(null);
        orderRepository.save(orderById);
        webSocketSender.sendPendingOrderIncrement(-1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderProductResponseWithPayloadDto> getAllUserOrdersProductResponseWithPayloadDto(UUID userUUID, Pageable pageable) {
        List<Order> ordersByUserUuid = orderRepository.findByUser_UuidOrderByCreatedAtDesc(userUUID, pageable);
        List<OrderProductResponseWithPayloadDto> list = new java.util.ArrayList<>(ordersByUserUuid.stream()
                .map(order -> {
                    OrderProductResponseWithPayloadDto response = getOrderProductResponseWithPayloadDto(order, false);
                    return response;
                })
                .toList());
        return list;
    }

    @Override
    public Integer getCountOrdersByUserChatId(Long chatId) {
        return orderRepository.countAllByUser_TelegramUserEntity_ChatId(chatId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByStatusAndCreatedAtBetween(Order.OrderStatus status, LocalDateTime from, LocalDateTime to) {
        orderRepository.deleteAllByStatusAndCreatedAtBetween(status, from, to);
    }

    @Override
    public OrderProductResponseWithPayloadDto searchOrderProductResponseWithPayloadDtoByValidationCode(String query) {
        Order order = orderRepository.findByOtp(query)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with OTP " + query));
        return getOrderProductResponseWithPayloadDto(order, false);
    }

    @Override
    public List<OrderProductResponseWithPayloadDto> searchOrdersWithPayloadDtoById(List<Integer> ids) {
        return ids.stream()
                .map(id -> {
                    Order order = getOrderById(id);
                    return getOrderProductResponseWithPayloadDto(order, false);
                })
                .toList();
    }

    @Override
    public OrdersStatesCount getOrdersStatesCount() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWorkDay = today.atTime(LocalTime.of(7, 0));

        LocalDateTime endOfWorkDay = today.atTime(LocalTime.of(23, 59));
        int pendingCount = orderRepository.countAllByCreatedAtBetweenAndStatus(startOfWorkDay, endOfWorkDay, Order.OrderStatus.PENDING);
        int confirmedCount = orderRepository.countAllByCreatedAtBetweenAndStatus(startOfWorkDay, endOfWorkDay, Order.OrderStatus.CONFIRMED);
        int completedCount = orderRepository.countAllByCreatedAtBetweenAndStatus(startOfWorkDay, endOfWorkDay, Order.OrderStatus.COMPLETED);
        OrdersStatesCount ordersStatesCount = new OrdersStatesCount();
        ordersStatesCount.setPendingOrders(pendingCount);
        ordersStatesCount.setConfirmedOrders(confirmedCount);
        ordersStatesCount.setCompletedOrders(completedCount);
        ordersStatesCount.setOpenTables(tableCacheService.getOpenTables());
        List<TableOrderInfo> allTableOrderInfos = tableCacheService.getAllTableOrderInfos();

        for (TableOrderInfo tableOrderInfo : allTableOrderInfos) {
            if (tableOrderInfo != null) {
                setTableMetaData(tableOrderInfo);
            }
        }
        ordersStatesCount.setTableOrderInfos(allTableOrderInfos);
        return ordersStatesCount;
    }

    @Override
    public void setTableMetaData(TableOrderInfo tableOrderInfo) {
        TableStateOrders pendingOrders = new TableStateOrders();
        List<OrderProductResponseWithPayloadDto> pendingOrdersList = new ArrayList<>();
        TableStateOrders completedOrders = new TableStateOrders();
        List<OrderProductResponseWithPayloadDto> completedOrdersList = new ArrayList<>();
        TableStateOrders confirmedOrders = new TableStateOrders();
        List<OrderProductResponseWithPayloadDto> confirmedOrdersList = new ArrayList<>();

        int tableId = tableOrderInfo.getTableId();
        Set<Integer> tableOrders = tableCacheService.getTableOrders(tableId);
        if (tableOrders != null && !tableOrders.isEmpty()) {
            for (Integer orderId : tableOrders) {
                Order order = getOrderById(orderId);
                OrderProductResponseWithPayloadDto orderWithPayload = getOrderProductResponseWithPayloadDto(order, false);
                if (order.getStatus().equals(Order.OrderStatus.PENDING)) {
                    pendingOrdersList.add(orderWithPayload);
                    pendingOrders.setCount(pendingOrders.getCount() + 1);
                } else if (order.getStatus().equals(Order.OrderStatus.COMPLETED)) {
                    completedOrdersList.add(orderWithPayload);
                    completedOrders.setCount(confirmedOrders.getCount() + 1);
                } else if (order.getStatus().equals(Order.OrderStatus.CONFIRMED)) {
                    confirmedOrdersList.add(orderWithPayload);
                    confirmedOrders.setCount(confirmedOrders.getCount() + 1);
                }
            }
        }

        pendingOrders.setOrders(pendingOrdersList);
        completedOrders.setOrders(completedOrdersList);
        confirmedOrders.setOrders(confirmedOrdersList);

        tableOrderInfo.setPendingOrders(pendingOrders);
        tableOrderInfo.setCompletedOrders(completedOrders);
        tableOrderInfo.setConfirmedOrders(confirmedOrders);
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
        if (order.isOrderInRestaurant()) {
            response.setOrderInRestaurant(true);
            Table table = order.getTable();
            TableResponseDTO responseDTO = tableMapperImpl.toResponseDTO(table);
            response.setTableResponseDTO(responseDTO);

        } else if (order.isOrderOutRestaurant()) {
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
        Set<Integer> tableOrders = tableCacheService.getTableOrders(tableId);
        BigDecimal totalPrice = tableOrders.stream()
                .map(id -> getOrderById(id).getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        tableCacheService.deleteTableOrders(tableId);
        tableCacheService.deleteTableFromClosed(tableId);

        OpenTables openTables = tableCacheService.getOpenTables();
        openTables.getIds().remove(tableId);
        tableCacheService.saveOpenTables(openTables);

        webSocketSender.sendOpenTables(openTables);
        return new TableOrdersPriceInfo(totalPrice);
    }
}
