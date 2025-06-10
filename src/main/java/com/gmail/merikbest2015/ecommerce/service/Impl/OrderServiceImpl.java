package com.gmail.merikbest2015.ecommerce.service.Impl;

import com.gmail.merikbest2015.ecommerce.domain.Order;
import com.gmail.merikbest2015.ecommerce.domain.OrderItem;
import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.exception.ApiRequestException;
import com.gmail.merikbest2015.ecommerce.repository.OrderItemRepository;
import com.gmail.merikbest2015.ecommerce.repository.OrderRepository;
import com.gmail.merikbest2015.ecommerce.repository.PerfumeRepository;
import com.gmail.merikbest2015.ecommerce.service.OrderService;
import com.gmail.merikbest2015.ecommerce.service.email.MailSender;

import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PerfumeRepository perfumeRepository;
    private final MailSender mailSender;

    @Override
    public Order getOrderById(Long orderId) {
        logger.info("Fetching order with id {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id {}", orderId);
                    return new ApiRequestException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        logger.info("Getting order items for order id {}", orderId);
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        logger.info("Fetching all orders with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findAllByOrderByIdAsc(pageable);
    }

    @Override
    public Page<Order> getUserOrders(String email, Pageable pageable) {
        logger.info("Fetching orders for user {}", email);
        return orderRepository.findOrderByEmail(email, pageable);
    }

    @Override
    @Transactional
    public Order postOrder(Order order, Map<Long, Long> perfumesId) {
        logger.info("Creating new order for user {}", order.getEmail());

        List<OrderItem> orderItemList = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : perfumesId.entrySet()) {
            Long perfumeId = entry.getKey();
            Long quantity = entry.getValue();

            Perfume perfume = perfumeRepository.findById(perfumeId)
                    .orElseThrow(() -> {
                        logger.warn("Perfume not found with id {}", perfumeId);
                        return new ApiRequestException("Perfume not found", HttpStatus.NOT_FOUND);
                    });

            OrderItem orderItem = new OrderItem();
            orderItem.setPerfume(perfume);
            orderItem.setQuantity(quantity);
            orderItem.setAmount(perfume.getPrice() * quantity);

            orderItemList.add(orderItem);
            orderItemRepository.save(orderItem);
        }

        order.getOrderItems().addAll(orderItemList);
        orderRepository.save(order);

        // Send confirmation email
        String subject = "Order #" + order.getId();
        String template = "order-template";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("order", order);

        mailSender.sendMessageHtml(order.getEmail(), subject, template, attributes);

        logger.info("Order {} created successfully for user {}", order.getId(), order.getEmail());
        return order;
    }

    @Override
    @Transactional
    public String deleteOrder(Long orderId) {
        logger.info("Deleting order with id {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id {}", orderId);
                    return new ApiRequestException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        orderRepository.delete(order);
        logger.info("Order with id {} deleted successfully", orderId);
        return "Order deleted successfully";
    }

    @Override
    public DataFetcher<List<Order>> getAllOrdersByQuery() {
        return dataFetchingEnvironment -> {
            logger.info("Fetching all orders via GraphQL query");
            return orderRepository.findAllByOrderByIdAsc();
        };
    }

    @Override
    public DataFetcher<List<Order>> getUserOrdersByEmailQuery() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email").toString();
            logger.info("Fetching orders for user {} via GraphQL query", email);
            return orderRepository.findOrderByEmail(email);
        };
    }
}