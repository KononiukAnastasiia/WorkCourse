package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.Dish;
import ua.edu.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishRepository {

    public Dish saveNew(String name, double price, String category, String description) {
        String sql = "INSERT INTO dishes (name, price, category, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;
            return new Dish(id, name, price, category, description);

        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження страви: " + e.getMessage());
        }
    }

    public Dish save(Dish dish) {
        String sql = "UPDATE dishes SET name=?, price=?, category=?, description=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dish.getName());
            ps.setDouble(2, dish.getPrice());
            ps.setString(3, dish.getCategory());
            ps.setString(4, dish.getDescription());
            ps.setInt(5, dish.getId());
            ps.executeUpdate();
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення страви: " + e.getMessage());
        }
    }

    public Dish findById(int id) {
        String sql = "SELECT * FROM dishes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку страви: " + e.getMessage());
        }
    }

    public List<Dish> findAll() {
        List<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM dishes ORDER BY category, name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання страв: " + e.getMessage());
        }
        return list;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM dishes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення страви: " + e.getMessage());
        }
    }

    public boolean existsById(int id) {
        return findById(id) != null;
    }

    private Dish mapRow(ResultSet rs) throws SQLException {
        return new Dish(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getString("category"),
            rs.getString("description")
        );
    }
}
