package ua.edu.restaurant.service;

import ua.edu.restaurant.exception.OrderNotFoundException;
import ua.edu.restaurant.exception.ValidationException;
import ua.edu.restaurant.model.*;
import ua.edu.restaurant.repository.DishRepository;
import ua.edu.restaurant.repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private int clientIdCounter = 1;

    public OrderService(OrderRepository orderRepository, DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
    }

    public Order createOrder(String clientName, String clientPhone) {
        if (clientName == null || clientName.trim().isEmpty()) {
            throw new ValidationException("Ім'я клієнта не може бути порожнім.");
        }
        Client client = new Client(clientIdCounter++, clientName.trim(), clientPhone);
        Order order = new Order(orderRepository.getNextId(), client);
        return orderRepository.save(order);
    }

    public Order addDishToOrder(int orderId, int dishId, int quantity) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CLOSED) {
            throw new ValidationException("Не можна змінювати завершене або закрите замовлення.");
        }
        Dish dish = dishRepository.findById(dishId);
        if (dish == null) {
            throw new ValidationException("Страву з ID " + dishId + " не знайдено в меню.");
        }
        if (quantity <= 0) {
            throw new ValidationException("Кількість повинна бути більше нуля.");
        }
        order.addItem(new OrderItem(dish, quantity));
        return orderRepository.save(order);
    }

    public Order removeDishFromOrder(int orderId, int dishId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CLOSED) {
            throw new ValidationException("Не можна змінювати завершене або закрите замовлення.");
        }
        boolean removed = order.removeItem(dishId);
        if (!removed) {
            throw new ValidationException("Страву з ID " + dishId + " не знайдено у замовленні.");
        }
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Order getOrderById(int orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Замовлення з ID " + orderId + " не знайдено.");
        }
        return order;
    }
}
