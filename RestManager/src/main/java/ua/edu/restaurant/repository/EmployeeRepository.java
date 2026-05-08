package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.Administrator;
import ua.edu.restaurant.model.Cook;
import ua.edu.restaurant.model.Employee;
import ua.edu.restaurant.model.Waiter;
import ua.edu.restaurant.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    public Employee save(Employee employee) {
        String sql = "INSERT INTO employees (id, name, role, salary, extra) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employee.getId());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getRole());
            ps.setDouble(4, employee.getSalary());
            ps.setString(5, getExtra(employee));
            ps.executeUpdate();
            return employee;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження працівника: " + e.getMessage());
        }
    }

    public int getNextId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM employees";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка генерації ID: " + e.getMessage());
        }
    }

    public Employee findById(int id) {
        String sql = "SELECT * FROM employees WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку працівника: " + e.getMessage());
        }
    }

    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY role, name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання персоналу: " + e.getMessage());
        }
        return list;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення працівника: " + e.getMessage());
        }
    }

    public boolean existsById(int id) {
        return findById(id) != null;
    }

    // Перетворює рядок БД у відповідний підклас Employee
    private Employee mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String role = rs.getString("role");
        double salary = rs.getDouble("salary");
        String extra = rs.getString("extra");

        return switch (role) {
            case "Кухар"         -> new Cook(id, name, salary, extra != null ? extra : "");
            case "Адміністратор" -> new Administrator(id, name, salary, extra != null ? extra : "");
            default              -> new Waiter(id, name, salary, extra != null ? Integer.parseInt(extra.replaceAll("\\D", "")) : 0);
        };
    }

    private String getExtra(Employee e) {
        if (e instanceof Cook c)          return c.getSpecialization();
        if (e instanceof Administrator a) return a.getDepartment();
        if (e instanceof Waiter w)        return String.valueOf(w.getTableCount());
        return "";
    }
}
