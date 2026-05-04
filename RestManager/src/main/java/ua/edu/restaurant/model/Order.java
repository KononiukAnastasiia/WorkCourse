package ua.edu.restaurant.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private int id;
    private Client client;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public Order(int id, Client client) {
        this.id = id;
        this.client = client;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        // Якщо страва вже є — збільшуємо кількість
        for (OrderItem existing : items) {
            if (existing.getDish().getId() == item.getDish().getId()) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public boolean removeItem(int dishId) {
        return items.removeIf(item -> item.getDish().getId() == dishId);
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Замовлення #%d | Клієнт: %-15s | %s | Сума: %.2f грн | %s",
                id, client.getName(), status, getTotalPrice(), createdAt.format(FORMATTER));
    }
}
