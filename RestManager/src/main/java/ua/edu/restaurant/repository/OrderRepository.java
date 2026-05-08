package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.*;
import ua.edu.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    // Зберегти клієнта і повернути його id
    public int saveClient(Client client) {
        String sql = "INSERT INTO clients (name, phone) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getPhone());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження клієнта: " + e.getMessage());
        }
    }

    // Зберегти замовлення (без позицій)
    public Order save(Order order) {
        if (existsById(order.getId())) {
            updateStatus(order.getId(), order.getStatus().name());
        } else {
            insertOrder(order);
        }
        saveItems(order);
        return order;
    }

    private void insertOrder(Order order) {
        String sql = "INSERT INTO orders (id, client_id, status, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order.getId());
            ps.setInt(2, order.getClient().getId());
            ps.setString(3, order.getStatus().name());
            ps.setString(4, order.getCreatedAt().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Помилка вставки замовлення: " + e.getMessage());
        }
    }

    private void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення статусу: " + e.getMessage());
        }
    }

    private void saveItems(Order order) {
        // Видаляємо старі позиції і записуємо нові
        String del = "DELETE FROM order_items WHERE order_id=?";
        String ins = "INSERT INTO order_items (order_id, dish_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement d = conn.prepareStatement(del);
            d.setInt(1, order.getId());
            d.executeUpdate();

            PreparedStatement i = conn.prepareStatement(ins);
            for (OrderItem item : order.getItems()) {
                i.setInt(1, order.getId());
                i.setInt(2, item.getDish().getId());
                i.setInt(3, item.getQuantity());
                i.addBatch();
            }
            i.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження позицій: " + e.getMessage());
        }
    }

    public int getNextId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM orders";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка генерації ID: " + e.getMessage());
        }
    }

    public Order findById(int id) {
        String sql = "SELECT o.*, c.name as cname, c.phone as cphone " +
                     "FROM orders o JOIN clients c ON o.client_id = c.id WHERE o.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            return mapOrder(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку замовлення: " + e.getMessage());
        }
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name as cname, c.phone as cphone " +
                     "FROM orders o JOIN clients c ON o.client_id = c.id ORDER BY o.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapOrder(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання замовлень: " + e.getMessage());
        }
        return list;
    }

    public List<Order> findByStatus(OrderStatus status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name as cname, c.phone as cphone " +
                     "FROM orders o JOIN clients c ON o.client_id = c.id WHERE o.status=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapOrder(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Помилка фільтрації замовлень: " + e.getMessage());
        }
        return list;
    }

    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM orders WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        int clientId = rs.getInt("client_id");
        Client client = new Client(clientId, rs.getString("cname"), rs.getString("cphone"));
        int orderId = rs.getInt("id");
        Order order = new Order(orderId, client);
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        loadItems(order);
        return order;
    }

    private void loadItems(Order order) {
        String sql = "SELECT oi.*, d.name, d.price, d.category, d.description " +
                     "FROM order_items oi JOIN dishes d ON oi.dish_id = d.id WHERE oi.order_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Dish dish = new Dish(
                    rs.getInt("dish_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("category"),
                    rs.getString("description")
                );
                order.addItem(new OrderItem(dish, rs.getInt("quantity")));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Помилка завантаження позицій: " + e.getMessage());
        }
    }
}
