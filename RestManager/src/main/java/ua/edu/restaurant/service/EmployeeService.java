package ua.edu.restaurant.service;

import ua.edu.restaurant.exception.EmployeeNotFoundException;
import ua.edu.restaurant.exception.ValidationException;
import ua.edu.restaurant.model.Administrator;
import ua.edu.restaurant.model.Cook;
import ua.edu.restaurant.model.Employee;
import ua.edu.restaurant.model.Waiter;
import ua.edu.restaurant.repository.EmployeeRepository;

import java.util.List;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee addWaiter(String name, double salary, int tableCount) {
        validateEmployeeData(name, salary);
        Waiter waiter = new Waiter(employeeRepository.getNextId(), name, salary, tableCount);
        return employeeRepository.save(waiter);
    }

    public Employee addCook(String name, double salary, String specialization) {
        validateEmployeeData(name, salary);
        Cook cook = new Cook(employeeRepository.getNextId(), name, salary, specialization);
        return employeeRepository.save(cook);
    }

    public Employee addAdministrator(String name, double salary, String department) {
        validateEmployeeData(name, salary);
        Administrator admin = new Administrator(employeeRepository.getNextId(), name, salary, department);
        return employeeRepository.save(admin);
    }

    public void removeEmployee(int id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException("Працівника з ID " + id + " не знайдено.");
        }
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(int id) {
        Employee employee = employeeRepository.findById(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("Працівника з ID " + id + " не знайдено.");
        }
        return employee;
    }

    private void validateEmployeeData(String name, double salary) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Ім'я працівника не може бути порожнім.");
        }
        if (salary < 0) {
            throw new ValidationException("Зарплата не може бути від'ємною.");
        }
    }
}
