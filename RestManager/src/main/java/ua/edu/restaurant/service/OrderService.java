package ua.edu.restaurant.service;

import ua.edu.restaurant.exception.OrderNotFoundException;
import ua.edu.restaurant.exception.ValidationException;
import ua.edu.restaurant.model.*;
import ua.edu.restaurant.util.ApiClient;
import ua.edu.restaurant.util.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    public Order createOrder(String clientName, String clientPhone) {
        if (clientName == null || clientName.trim().isEmpty())
            throw new ValidationException("Ім'я клієнта не може бути порожнім.");

        String body = "{\"clientName\":\"" + clientName + "\"," +
                "\"clientPhone\":\"" + (clientPhone != null ? clientPhone : "") + "\"," +
                "\"items\":[]}";
        String json = ApiClient.post("/orders", body);
        return parseOrder(json);
    }

    public Order addDishToOrder(int orderId, int dishId, int quantity) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CLOSED)
            throw new ValidationException("Не можна змінювати завершене або закрите замовлення.");

        String patchBody = "{\"dishId\":" + dishId + ",\"quantity\":" + quantity + "}";
        ApiClient.patch("/orders/" + orderId + "/add-item", patchBody);
        return getOrderById(orderId);
    }

    public Order removeDishFromOrder(int orderId, int dishId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CLOSED)
            throw new ValidationException("Не можна змінювати завершене або закрите замовлення.");

        ApiClient.patch("/orders/" + orderId + "/remove-item/" + dishId, null);
        return getOrderById(orderId);
    }

    public Order updateOrderStatus(int orderId, OrderStatus newStatus) {
        String body = "{\"status\":\"" + newStatus.name() + "\"}";
        String json = ApiClient.patch("/orders/" + orderId + "/status", body);
        return parseOrder(json);
    }

    public List<Order> getAllOrders() {
        String json = ApiClient.get("/orders");
        List<String> items = JsonParser.parseArray(json);
        List<Order> orders = new ArrayList<>();
        for (String item : items) orders.add(parseOrder(item));
        return orders;
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        List<Order> all = getAllOrders();
        List<Order> result = new ArrayList<>();
        for (Order o : all) if (o.getStatus() == status) result.add(o);
        return result;
    }

    public Order getOrderById(int orderId) {
        String json = ApiClient.get("/orders/" + orderId);
        if (json == null || json.isBlank() || json.equals("null"))
            throw new OrderNotFoundException("Замовлення з ID " + orderId + " не знайдено.");
        return parseOrder(json);
    }

    private Order parseOrder(String json) {
        int id = JsonParser.getInt(json, "id");
        String clientName = JsonParser.getString(json, "clientName");
        String clientPhone = JsonParser.getString(json, "clientPhone");
        String statusStr = JsonParser.getString(json, "status");

        Client client = new Client(0, clientName, clientPhone);
        Order order = new Order(id, client);

        try {
            order.setStatus(OrderStatus.valueOf(statusStr));
        } catch (Exception e) {
            order.setStatus(OrderStatus.CREATED);
        }

        int itemsStart = json.indexOf("\"items\"");
        if (itemsStart >= 0) {
            int arrStart = json.indexOf("[", itemsStart);
            int arrEnd = json.lastIndexOf("]");
            if (arrStart >= 0 && arrEnd > arrStart) {
                String itemsJson = json.substring(arrStart, arrEnd + 1);
                List<String> itemList = JsonParser.parseArray(itemsJson);
                for (String item : itemList) {
                    int dishId = JsonParser.getInt(item, "dishId");
                    String dishName = JsonParser.getString(item, "dishName");
                    double price = JsonParser.getDouble(item, "price");
                    int qty = JsonParser.getInt(item, "qty");
                    Dish dish = new Dish(dishId, dishName, price, "", "");
                    order.addItem(new OrderItem(dish, qty));
                }
            }
        }

        return order;
    }
}