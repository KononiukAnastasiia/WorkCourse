package ua.edu.restaurant.app;

import ua.edu.restaurant.report.ReportService;
import ua.edu.restaurant.service.EmployeeService;
import ua.edu.restaurant.service.MenuService;
import ua.edu.restaurant.service.OrderService;
import ua.edu.restaurant.ui.ConsoleMenu;

public class Main {
    static void main() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║      RestManager v1.0 — UA Restaurant    ║");
        System.out.println("║   Система управління закладом харчування  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Підключення до сервера http://localhost:8080...");

        // Сервіси тепер працюють через HTTP API
        MenuService menuService = new MenuService();
        OrderService orderService = new OrderService();
        EmployeeService employeeService = new EmployeeService();
        ReportService reportService = new ReportService(orderService);

        System.out.println("  [✓] Підключено до Spring Boot сервера\n");

        ConsoleMenu consoleMenu = new ConsoleMenu(menuService, orderService, employeeService, reportService);
        consoleMenu.start();
    }
}
