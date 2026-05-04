package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeRepository {
    private final Map<Integer, Employee> storage = new HashMap<>();
    private int nextId = 1;

    public Employee save(Employee employee) {
        storage.put(employee.getId(), employee);
        return employee;
    }

    public int getNextId() {
        return nextId++;
    }

    public Employee findById(int id) {
        return storage.get(id);
    }

    public List<Employee> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean deleteById(int id) {
        return storage.remove(id) != null;
    }

    public boolean existsById(int id) {
        return storage.containsKey(id);
    }
}
