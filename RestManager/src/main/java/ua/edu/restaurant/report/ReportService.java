package ua.edu.restaurant.report;

import ua.edu.restaurant.model.Order;
import ua.edu.restaurant.model.OrderItem;
import ua.edu.restaurant.model.OrderStatus;
import ua.edu.restaurant.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public double calculateTotalRevenue() {
        double total = 0;
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CLOSED) {
                total += order.getTotalPrice();
            }
        }
        return total;
    }

    public void printOrdersReport() {
        List<Order> orders = orderRepository.findAll();
        System.out.println("\n========== ЗВІТ ПО ЗАМОВЛЕННЯХ ==========");
        if (orders.isEmpty()) {
            System.out.println("Замовлень немає.");
            return;
        }
        for (Order order : orders) {
            System.out.println(order);
            for (OrderItem item : order.getItems()) {
                System.out.println(item);
            }
            System.out.printf("  Разом: %.2f грн%n", order.getTotalPrice());
            System.out.println("  ------------------------------------------");
        }
        System.out.printf("Загальна кількість замовлень: %d%n", orders.size());
    }

    public void printRevenueReport() {
        List<Order> allOrders = orderRepository.findAll();
        int total = allOrders.size();
        int completed = orderRepository.findByStatus(OrderStatus.COMPLETED).size()
                + orderRepository.findByStatus(OrderStatus.CLOSED).size();

        System.out.println("\n========== ФІНАНСОВИЙ ЗВІТ ==========");
        System.out.printf("Всього замовлень:     %d%n", total);
        System.out.printf("Виконаних замовлень:  %d%n", completed);
        System.out.printf("Загальний дохід:      %.2f грн%n", calculateTotalRevenue());
    }

    public void printDishPopularityReport() {
        Map<String, Integer> dishCount = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem item : order.getItems()) {
                String name = item.getDish().getName();
                dishCount.put(name, dishCount.getOrDefault(name, 0) + item.getQuantity());
            }
        }

        System.out.println("\n========== ПОПУЛЯРНІСТЬ СТРАВ ==========");
        if (dishCount.isEmpty()) {
            System.out.println("Даних немає.");
            return;
        }
        dishCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.printf("  %-25s — %d порц.%n", e.getKey(), e.getValue()));
    }
}
