package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.*;
import ua.edu.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public int saveClient(Client client) {
        String sql = "INSERT INTO clients (name, phone) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, client.getName());
            ps.setString(2, client.getPhone());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;
            keys.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження клієнта: " + e.getMessage());
        }
    }

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
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, order.getId());
            ps.setInt(2, order.getClient().getId());
            ps.setString(3, order.getStatus().name());
            ps.setString(4, order.getCreatedAt().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка вставки замовлення: " + e.getMessage());
        }
    }

    private void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення статусу: " + e.getMessage());
        }
    }

    private void saveItems(Order order) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement del = conn.prepareStatement("DELETE FROM order_items WHERE order_id=?");
            del.setInt(1, order.getId());
            del.executeUpdate();
            del.close();

            PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO order_items (order_id, dish_id, quantity) VALUES (?, ?, ?)");
            for (OrderItem item : order.getItems()) {
                ins.setInt(1, order.getId());
                ins.setInt(2, item.getDish().getId());
                ins.setInt(3, item.getQuantity());
                ins.addBatch();
            }
            ins.executeBatch();
            ins.close();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження позицій: " + e.getMessage());
        }
    }

    public int getNextId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM orders";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int id = rs.next() ? rs.getInt(1) : 1;
            rs.close();
            stmt.close();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка генерації ID: " + e.getMessage());
        }
    }

    public Order findById(int id) {
        String sql = "SELECT o.*, c.name as cname, c.phone as cphone " +
                     "FROM orders o JOIN clients c ON o.client_id = c.id WHERE o.id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Order order = null;
            if (rs.next()) order = mapOrder(rs);
            rs.close();
            ps.close();
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку замовлення: " + e.getMessage());
        }
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name as cname, c.phone as cphone " +
                     "FROM orders o JOIN clients c ON o.client_id = c.id ORDER BY o.id DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapOrder(rs));
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання замовлень: " + e.getMessage());
        }
        return list;
    }

    public List<Order> findByStatus(OrderStatus status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name as cname, c.phone as cphone " +
                     "FROM orders o JOIN clients c ON o.client_id = c.id WHERE o.status=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapOrder(rs));
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка фільтрації замовлень: " + e.getMessage());
        }
        return list;
    }

    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM orders WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close();
            ps.close();
            return exists;
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
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
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
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка завантаження позицій: " + e.getMessage());
        }
    }
}
