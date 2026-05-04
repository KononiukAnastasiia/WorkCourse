# RestManager — Система управління закладом харчування

**Курсова робота** | Ужгородський національний університет  
**Студентка:** Кононюк Анастасія Юріївна  
**Мова:** Java SE | **IDE:** IntelliJ IDEA

---

## Як відкрити проєкт в IntelliJ IDEA

1. Запустіть **IntelliJ IDEA**
2. Оберіть **File → Open...**
3. Вкажіть папку `RestManager` (де лежить `RestManager.iml`)
4. Натисніть **OK** → проєкт відкриється автоматично

> Якщо IDE попросить налаштувати SDK — оберіть **JDK 11** (або будь-який JDK 8+).

## Запуск програми

Відкрийте клас `ua.edu.restaurant.app.Main` → натисніть зелену кнопку **▶ Run**.

---

## Структура проєкту

```
src/main/java/ua/edu/restaurant/
│
├── app/
│   └── Main.java                   — точка входу
│
├── model/                          — моделі предметної області
│   ├── Dish.java                   — страва
│   ├── Menu.java                   — меню
│   ├── Order.java                  — замовлення
│   ├── OrderItem.java              — позиція у замовленні
│   ├── OrderStatus.java            — статуси замовлення (enum)
│   ├── Client.java                 — клієнт
│   ├── Employee.java               — абстрактний клас «Працівник»
│   ├── Waiter.java                 — офіціант (extends Employee)
│   ├── Cook.java                   — кухар (extends Employee)
│   └── Administrator.java          — адміністратор (extends Employee)
│
├── service/                        — бізнес-логіка
│   ├── MenuService.java
│   ├── OrderService.java
│   └── EmployeeService.java
│
├── repository/                     — зберігання даних у пам'яті (HashMap)
│   ├── DishRepository.java
│   ├── OrderRepository.java
│   └── EmployeeRepository.java
│
├── report/
│   └── ReportService.java          — формування звітів
│
├── ui/
│   └── ConsoleMenu.java            — консольний інтерфейс
│
├── util/
│   ├── InputHelper.java            — безпечне зчитування вводу
│   └── DataInitializer.java        — тестові дані
│
└── exception/                      — власні винятки
    ├── RestaurantException.java    — базовий виняток
    ├── DishNotFoundException.java
    ├── OrderNotFoundException.java
    ├── EmployeeNotFoundException.java
    └── ValidationException.java
```

---

## Принципи ООП у проєкті

| Принцип | Де використано |
|---|---|
| **Інкапсуляція** | Усі поля — `private`, доступ через геттери/сеттери |
| **Наслідування** | `Employee` → `Waiter`, `Cook`, `Administrator` |
| **Поліморфізм** | `getRole()`, `getDuties()` — перевизначені в кожному підкласі |
| **Абстракція** | `Employee` — абстрактний клас з абстрактними методами |

---

## Функціональні можливості

- ✅ Управління меню (додавання, редагування, видалення, перегляд)
- ✅ Управління замовленнями (створення, додавання/видалення страв, статуси)
- ✅ Автоматичний розрахунок вартості замовлення
- ✅ Управління персоналом (офіціанти, кухарі, адміністратори)
- ✅ Звіти: по замовленнях, фінансовий, популярність страв
- ✅ Обробка помилок через власні винятки
- ✅ Зберігання даних у пам'яті (`HashMap`, `ArrayList`)
