package ua.edu.restaurant.app;

import ua.edu.restaurant.repository.DishRepository;
import ua.edu.restaurant.repository.EmployeeRepository;
import ua.edu.restaurant.repository.OrderRepository;
import ua.edu.restaurant.service.EmployeeService;
import ua.edu.restaurant.service.MenuService;
import ua.edu.restaurant.service.OrderService;
import ua.edu.restaurant.report.ReportService;
import ua.edu.restaurant.ui.ConsoleMenu;
import ua.edu.restaurant.util.DatabaseConnection;
import ua.edu.restaurant.util.DatabaseInitializer;
import ua.edu.restaurant.util.DataInitializer;

public class Main {
    static void main() {
        // 1. Ініціалізація бази даних (створення таблиць якщо їх немає)
        DatabaseInitializer.init();

        // 2. Ініціалізація репозиторіїв (тепер працюють з SQLite)
        DishRepository dishRepository = new DishRepository();
        OrderRepository orderRepository = new OrderRepository();
        EmployeeRepository employeeRepository = new EmployeeRepository();

        // 3. Наповнення тестовими даними (тільки якщо БД порожня)
        DataInitializer.initIfEmpty(dishRepository, employeeRepository);

        // 4. Ініціалізація сервісів
        MenuService menuService = new MenuService(dishRepository);
        OrderService orderService = new OrderService(orderRepository, dishRepository);
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        ReportService reportService = new ReportService(orderRepository);

        // 5. Запуск консольного інтерфейсу
        ConsoleMenu consoleMenu = new ConsoleMenu(menuService, orderService, employeeService, reportService);
        consoleMenu.start();

        // 6. Закриття з'єднання з БД при виході
        DatabaseConnection.close();
    }
}
