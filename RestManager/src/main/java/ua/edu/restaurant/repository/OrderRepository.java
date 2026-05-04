package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.Order;
import ua.edu.restaurant.model.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {
    private final Map<Integer, Order> storage = new HashMap<>();
    private int nextId = 1;

    public Order save(Order order) {
        storage.put(order.getId(), order);
        return order;
    }

    public int getNextId() {
        return nextId++;
    }

    public Order findById(int id) {
        return storage.get(id);
    }

    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Order> findByStatus(OrderStatus status) {
        List<Order> result = new ArrayList<>();
        for (Order order : storage.values()) {
            if (order.getStatus() == status) {
                result.add(order);
            }
        }
        return result;
    }

    public boolean existsById(int id) {
        return storage.containsKey(id);
    }
}
