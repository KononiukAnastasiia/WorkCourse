package ua.edu.restaurant.ui;

import ua.edu.restaurant.exception.RestaurantException;
import ua.edu.restaurant.model.*;
import ua.edu.restaurant.report.ReportService;
import ua.edu.restaurant.service.EmployeeService;
import ua.edu.restaurant.service.MenuService;
import ua.edu.restaurant.service.OrderService;
import ua.edu.restaurant.util.InputHelper;

import java.util.List;

public class ConsoleMenu {
    private final MenuService menuService;
    private final OrderService orderService;
    private final EmployeeService employeeService;
    private final ReportService reportService;

    private boolean running = true;

    public ConsoleMenu(MenuService menuService, OrderService orderService,
                       EmployeeService employeeService, ReportService reportService) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.employeeService = employeeService;
        this.reportService = reportService;
    }

    public void start() {
        printWelcome();
        while (running) {
            printMainMenu();
            int choice = InputHelper.readInt("  Ваш вибір: ");
            handleMainMenu(choice);
        }
        System.out.println("\n  До побачення! Дякуємо за використання RestManager.");
    }

    // ===================== ГОЛОВНЕ МЕНЮ =====================

    private void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         RestManager — Головне меню   ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Управління меню                  ║");
        System.out.println("║  2. Управління замовленнями          ║");
        System.out.println("║  3. Управління персоналом            ║");
        System.out.println("║  4. Звіти                            ║");
        System.out.println("║  0. Вихід                            ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private void handleMainMenu(int choice) {
        switch (choice) {
            case 1: menuManagement(); break;
            case 2: orderManagement(); break;
            case 3: employeeManagement(); break;
            case 4: reportsMenu(); break;
            case 0: running = false; break;
            default: System.out.println("  [!] Невідомий вибір. Спробуйте ще раз.");
        }
    }

    // ===================== МЕНЮ =====================

    private void menuManagement() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Управління меню ---");
            System.out.println("  1. Переглянути всі страви");
            System.out.println("  2. Додати страву");
            System.out.println("  3. Редагувати страву");
            System.out.println("  4. Видалити страву");
            System.out.println("  0. Назад");
            int choice = InputHelper.readInt("  Ваш вибір: ");
            try {
                switch (choice) {
                    case 1: viewAllDishes(); break;
                    case 2: addDish(); break;
                    case 3: editDish(); break;
                    case 4: deleteDish(); break;
                    case 0: back = true; break;
                    default: System.out.println("  [!] Невідомий вибір.");
                }
            } catch (RestaurantException e) {
                System.out.println("  [!] Помилка: " + e.getMessage());
                InputHelper.pause();
            }
        }
    }

    private void viewAllDishes() {
        List<Dish> dishes = menuService.getAllDishes();
        System.out.println("\n========== МЕНЮ ==========");
        if (dishes.isEmpty()) {
            System.out.println("  Меню порожнє.");
            return;
        }
        dishes.stream()
                .sorted((a, b) -> a.getCategory().compareTo(b.getCategory()))
                .forEach(System.out::println);
    }

    private void addDish() {
        System.out.println("\n--- Додавання страви ---");
        String name = InputHelper.readString("  Назва страви: ");
        double price = InputHelper.readDouble("  Ціна (грн): ");
        String category = InputHelper.readString("  Категорія (напр. Салати): ");
        String description = InputHelper.readOptionalString("  Опис (необов'язково): ");
        Dish dish = menuService.addDish(name, price, category, description);
        System.out.println("  [✓] Страву додано: " + dish);
        InputHelper.pause();
    }

    private void editDish() {
        viewAllDishes();
        int id = InputHelper.readInt("\n  ID страви для редагування: ");
        Dish existing = menuService.getDishById(id);
        System.out.println("  Поточна інформація: " + existing);
        System.out.println("  (Залиште порожнім, щоб не змінювати)");
        String name = InputHelper.readOptionalString("  Нова назва [" + existing.getName() + "]: ");
        if (name.isEmpty()) name = existing.getName();
        String priceStr = InputHelper.readOptionalString("  Нова ціна [" + existing.getPrice() + "]: ");
        double price = priceStr.isEmpty() ? existing.getPrice() : Double.parseDouble(priceStr.replace(",", "."));
        String category = InputHelper.readOptionalString("  Нова категорія [" + existing.getCategory() + "]: ");
        if (category.isEmpty()) category = existing.getCategory();
        String description = InputHelper.readOptionalString("  Новий опис [" + existing.getDescription() + "]: ");
        if (description.isEmpty()) description = existing.getDescription();
        Dish updated = menuService.updateDish(id, name, price, category, description);
        System.out.println("  [✓] Страву оновлено: " + updated);
        InputHelper.pause();
    }

    private void deleteDish() {
        viewAllDishes();
        int id = InputHelper.readInt("\n  ID страви для видалення: ");
        menuService.deleteDish(id);
        System.out.println("  [✓] Страву видалено.");
        InputHelper.pause();
    }

    // ===================== ЗАМОВЛЕННЯ =====================

    private void orderManagement() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Управління замовленнями ---");
            System.out.println("  1. Переглянути всі замовлення");
            System.out.println("  2. Створити замовлення");
            System.out.println("  3. Додати страву до замовлення");
            System.out.println("  4. Видалити страву із замовлення");
            System.out.println("  5. Змінити статус замовлення");
            System.out.println("  6. Деталі замовлення");
            System.out.println("  0. Назад");
            int choice = InputHelper.readInt("  Ваш вибір: ");
            try {
                switch (choice) {
                    case 1: viewAllOrders(); break;
                    case 2: createOrder(); break;
                    case 3: addDishToOrder(); break;
                    case 4: removeDishFromOrder(); break;
                    case 5: changeOrderStatus(); break;
                    case 6: viewOrderDetails(); break;
                    case 0: back = true; break;
                    default: System.out.println("  [!] Невідомий вибір.");
                }
            } catch (RestaurantException e) {
                System.out.println("  [!] Помилка: " + e.getMessage());
                InputHelper.pause();
            }
        }
    }

    private void viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        System.out.println("\n========== ЗАМОВЛЕННЯ ==========");
        if (orders.isEmpty()) {
            System.out.println("  Замовлень немає.");
            return;
        }
        orders.forEach(System.out::println);
    }

    private void createOrder() {
        System.out.println("\n--- Нове замовлення ---");
        String name = InputHelper.readString("  Ім'я клієнта: ");
        String phone = InputHelper.readOptionalString("  Телефон клієнта (необов'язково): ");
        Order order = orderService.createOrder(name, phone);
        System.out.println("  [✓] Замовлення #" + order.getId() + " створено для " + order.getClient().getName());
        InputHelper.pause();
    }

    private void addDishToOrder() {
        viewAllOrders();
        int orderId = InputHelper.readInt("\n  ID замовлення: ");
        viewAllDishes();
        int dishId = InputHelper.readInt("\n  ID страви: ");
        int qty = InputHelper.readInt("  Кількість: ");
        Order order = orderService.addDishToOrder(orderId, dishId, qty);
        System.out.printf("  [✓] Додано до замовлення #%d. Поточна сума: %.2f грн%n",
                order.getId(), order.getTotalPrice());
        InputHelper.pause();
    }

    private void removeDishFromOrder() {
        viewAllOrders();
        int orderId = InputHelper.readInt("\n  ID замовлення: ");
        viewOrderDetails(orderId);
        int dishId = InputHelper.readInt("  ID страви для видалення: ");
        orderService.removeDishFromOrder(orderId, dishId);
        System.out.println("  [✓] Страву видалено із замовлення.");
        InputHelper.pause();
    }

    private void changeOrderStatus() {
        viewAllOrders();
        int orderId = InputHelper.readInt("\n  ID замовлення: ");
        System.out.println("  Оберіть новий статус:");
        System.out.println("  1. " + OrderStatus.CREATED.getDisplayName());
        System.out.println("  2. " + OrderStatus.IN_PROGRESS.getDisplayName());
        System.out.println("  3. " + OrderStatus.COMPLETED.getDisplayName());
        System.out.println("  4. " + OrderStatus.CLOSED.getDisplayName());
        int choice = InputHelper.readInt("  Статус: ");
        OrderStatus[] statuses = OrderStatus.values();
        if (choice < 1 || choice > statuses.length) {
            System.out.println("  [!] Невірний вибір статусу.");
            return;
        }
        Order order = orderService.updateOrderStatus(orderId, statuses[choice - 1]);
        System.out.println("  [✓] Статус замовлення #" + order.getId() + " змінено на: " + order.getStatus());
        InputHelper.pause();
    }

    private void viewOrderDetails() {
        viewAllOrders();
        int orderId = InputHelper.readInt("\n  ID замовлення для деталей: ");
        viewOrderDetails(orderId);
        InputHelper.pause();
    }

    private void viewOrderDetails(int orderId) {
        Order order = orderService.getOrderById(orderId);
        System.out.println("\n========== ДЕТАЛІ ЗАМОВЛЕННЯ #" + orderId + " ==========");
        System.out.println("  Клієнт:  " + order.getClient().getName() + "  " + order.getClient().getPhone());
        System.out.println("  Статус:  " + order.getStatus());
        System.out.println("  Страви:");
        if (order.getItems().isEmpty()) {
            System.out.println("    (порожнє замовлення)");
        } else {
            order.getItems().forEach(System.out::println);
        }
        System.out.printf("  РАЗОМ:   %.2f грн%n", order.getTotalPrice());
    }

    // ===================== ПЕРСОНАЛ =====================

    private void employeeManagement() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Управління персоналом ---");
            System.out.println("  1. Переглянути всіх працівників");
            System.out.println("  2. Додати офіціанта");
            System.out.println("  3. Додати кухаря");
            System.out.println("  4. Додати адміністратора");
            System.out.println("  5. Видалити працівника");
            System.out.println("  0. Назад");
            int choice = InputHelper.readInt("  Ваш вибір: ");
            try {
                switch (choice) {
                    case 1: viewAllEmployees(); InputHelper.pause(); break;
                    case 2: addWaiter(); break;
                    case 3: addCook(); break;
                    case 4: addAdministrator(); break;
                    case 5: removeEmployee(); break;
                    case 0: back = true; break;
                    default: System.out.println("  [!] Невідомий вибір.");
                }
            } catch (RestaurantException e) {
                System.out.println("  [!] Помилка: " + e.getMessage());
                InputHelper.pause();
            }
        }
    }

    private void viewAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        System.out.println("\n========== ПЕРСОНАЛ ==========");
        if (employees.isEmpty()) {
            System.out.println("  Список персоналу порожній.");
            return;
        }
        employees.forEach(e -> {
            System.out.println(e);
            System.out.println("    Обов'язки: " + e.getDuties());
        });
    }

    private void addWaiter() {
        System.out.println("\n--- Додавання офіціанта ---");
        String name = InputHelper.readString("  Ім'я: ");
        double salary = InputHelper.readDouble("  Зарплата (грн/міс): ");
        int tables = InputHelper.readInt("  Кількість столиків: ");
        Employee e = employeeService.addWaiter(name, salary, tables);
        System.out.println("  [✓] Додано: " + e);
        InputHelper.pause();
    }

    private void addCook() {
        System.out.println("\n--- Додавання кухаря ---");
        String name = InputHelper.readString("  Ім'я: ");
        double salary = InputHelper.readDouble("  Зарплата (грн/міс): ");
        String spec = InputHelper.readString("  Спеціалізація: ");
        Employee e = employeeService.addCook(name, salary, spec);
        System.out.println("  [✓] Додано: " + e);
        InputHelper.pause();
    }

    private void addAdministrator() {
        System.out.println("\n--- Додавання адміністратора ---");
        String name = InputHelper.readString("  Ім'я: ");
        double salary = InputHelper.readDouble("  Зарплата (грн/міс): ");
        String dept = InputHelper.readString("  Відділ: ");
        Employee e = employeeService.addAdministrator(name, salary, dept);
        System.out.println("  [✓] Додано: " + e);
        InputHelper.pause();
    }

    private void removeEmployee() {
        viewAllEmployees();
        int id = InputHelper.readInt("\n  ID працівника для видалення: ");
        employeeService.removeEmployee(id);
        System.out.println("  [✓] Працівника видалено.");
        InputHelper.pause();
    }

    // ===================== ЗВІТИ =====================

    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Звіти ---");
            System.out.println("  1. Звіт по замовленнях");
            System.out.println("  2. Фінансовий звіт");
            System.out.println("  3. Популярність страв");
            System.out.println("  0. Назад");
            int choice = InputHelper.readInt("  Ваш вибір: ");
            switch (choice) {
                case 1: reportService.printOrdersReport(); InputHelper.pause(); break;
                case 2: reportService.printRevenueReport(); InputHelper.pause(); break;
                case 3: reportService.printDishPopularityReport(); InputHelper.pause(); break;
                case 0: back = true; break;
                default: System.out.println("  [!] Невідомий вибір.");
            }
        }
    }

    // ===================== ВІТАННЯ =====================

    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║      RestManager v1.0 — UA Restaurant    ║");
        System.out.println("║   Система управління закладом харчування  ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
