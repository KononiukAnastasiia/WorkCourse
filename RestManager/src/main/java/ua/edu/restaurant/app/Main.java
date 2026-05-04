package ua.edu.restaurant.app;

import ua.edu.restaurant.repository.DishRepository;
import ua.edu.restaurant.repository.EmployeeRepository;
import ua.edu.restaurant.repository.OrderRepository;
import ua.edu.restaurant.service.EmployeeService;
import ua.edu.restaurant.service.MenuService;
import ua.edu.restaurant.service.OrderService;
import ua.edu.restaurant.report.ReportService;
import ua.edu.restaurant.ui.ConsoleMenu;
import ua.edu.restaurant.util.DataInitializer;

public class Main {
     static void main(String[] args) {
        // Ініціалізація репозиторіїв
        DishRepository dishRepository = new DishRepository();
        OrderRepository orderRepository = new OrderRepository();
        EmployeeRepository employeeRepository = new EmployeeRepository();

        // Ініціалізація сервісів
        MenuService menuService = new MenuService(dishRepository);
        OrderService orderService = new OrderService(orderRepository, dishRepository);
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        ReportService reportService = new ReportService(orderRepository);

        // Наповнення тестовими даними
        DataInitializer.init(dishRepository, employeeRepository);

        // Запуск консольного інтерфейсу
        ConsoleMenu consoleMenu = new ConsoleMenu(menuService, orderService, employeeService, reportService);
        consoleMenu.start();
    }
}
