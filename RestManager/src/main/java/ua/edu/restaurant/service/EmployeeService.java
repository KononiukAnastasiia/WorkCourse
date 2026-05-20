package ua.edu.restaurant.service;

import ua.edu.restaurant.exception.EmployeeNotFoundException;
import ua.edu.restaurant.exception.ValidationException;
import ua.edu.restaurant.model.*;
import ua.edu.restaurant.util.ApiClient;
import ua.edu.restaurant.util.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    public Employee addWaiter(String name, double salary, int tableCount) {
        validateEmployeeData(name, salary);
        String body = "{\"name\":\"" + name + "\",\"role\":\"Офіціант\"," +
                      "\"salary\":" + salary + ",\"extra\":\"" + tableCount + " столиків\"}";
        String json = ApiClient.post("/employees", body);
        return parseEmployee(json);
    }

    public Employee addCook(String name, double salary, String specialization) {
        validateEmployeeData(name, salary);
        String body = "{\"name\":\"" + name + "\",\"role\":\"Кухар\"," +
                      "\"salary\":" + salary + ",\"extra\":\"" + specialization + "\"}";
        String json = ApiClient.post("/employees", body);
        return parseEmployee(json);
    }

    public Employee addAdministrator(String name, double salary, String department) {
        validateEmployeeData(name, salary);
        String body = "{\"name\":\"" + name + "\",\"role\":\"Адміністратор\"," +
                      "\"salary\":" + salary + ",\"extra\":\"" + department + "\"}";
        String json = ApiClient.post("/employees", body);
        return parseEmployee(json);
    }

    public void removeEmployee(int id) {
        if (!existsById(id))
            throw new EmployeeNotFoundException("Працівника з ID " + id + " не знайдено.");
        ApiClient.delete("/employees/" + id);
    }

    public List<Employee> getAllEmployees() {
        String json = ApiClient.get("/employees");
        List<String> items = JsonParser.parseArray(json);
        List<Employee> employees = new ArrayList<>();
        for (String item : items) employees.add(parseEmployee(item));
        return employees;
    }

    public Employee getEmployeeById(int id) {
        List<Employee> all = getAllEmployees();
        for (Employee e : all) {
            if (e.getId() == id) return e;
        }
        throw new EmployeeNotFoundException("Працівника з ID " + id + " не знайдено.");
    }

    private boolean existsById(int id) {
        try {
            getEmployeeById(id);
            return true;
        } catch (EmployeeNotFoundException e) {
            return false;
        }
    }

    private void validateEmployeeData(String name, double salary) {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Ім'я працівника не може бути порожнім.");
        if (salary < 0)
            throw new ValidationException("Зарплата не може бути від'ємною.");
    }

    private Employee parseEmployee(String json) {
        int id = JsonParser.getInt(json, "id");
        String name = JsonParser.getString(json, "name");
        String role = JsonParser.getString(json, "role");
        double salary = JsonParser.getDouble(json, "salary");
        String extra = JsonParser.getString(json, "extra");

        return switch (role) {
            case "Кухар" -> new Cook(id, name, salary, extra);
            case "Адміністратор" -> new Administrator(id, name, salary, extra);
            default -> {
                int tables = 0;
                try { tables = Integer.parseInt(extra.replaceAll("\\D", "")); } catch (Exception ignored) {}
                yield new Waiter(id, name, salary, tables);
            }
        };
    }
}
