package ua.edu.restaurant.util;

import ua.edu.restaurant.model.Administrator;
import ua.edu.restaurant.model.Cook;
import ua.edu.restaurant.model.Waiter;
import ua.edu.restaurant.repository.DishRepository;
import ua.edu.restaurant.repository.EmployeeRepository;

public class DataInitializer {

    // Наповнює БД тільки якщо вона порожня — щоб не дублювати дані при кожному запуску
    public static void initIfEmpty(DishRepository dishRepository, EmployeeRepository employeeRepository) {
        if (!dishRepository.findAll().isEmpty()) {
            System.out.println("[DB] Дані вже існують, пропускаємо ініціалізацію.");
            return;
        }

        System.out.println("[DB] Наповнення тестовими даними...");

        // Меню — страви
        dishRepository.saveNew("Борщ українських",     55.00, "Перші страви",  "Класичний борщ зі сметаною");
        dishRepository.saveNew("Суп-крем грибний",     65.00, "Перші страви",  "Вершковий суп з печерицями");
        dishRepository.saveNew("Курка-гриль",         120.00, "Другі страви",  "Ціла курка з травами");
        dishRepository.saveNew("Стейк із свинини",    145.00, "Другі страви",  "З картопляним пюре та соусом");
        dishRepository.saveNew("Вареники з картоплею", 70.00, "Другі страви",  "З цибулею та сметаною");
        dishRepository.saveNew("Цезар з куркою",       85.00, "Салати",        "Класичний салат Цезар");
        dishRepository.saveNew("Грецький салат",       75.00, "Салати",        "Із фетою та маслинами");
        dishRepository.saveNew("Тірамісу",             60.00, "Десерти",       "Класичний італійський десерт");
        dishRepository.saveNew("Чізкейк",              55.00, "Десерти",       "З ягідним соусом");
        dishRepository.saveNew("Лимонад домашній",     35.00, "Напої",         "Свіжий лимонад з м'ятою");
        dishRepository.saveNew("Кава американо",       40.00, "Напої",         "Еспресо з водою");
        dishRepository.saveNew("Апельсиновий фреш",   50.00, "Напої",         "Свіжовичавлений сік");

        // Персонал
        employeeRepository.save(new Administrator(employeeRepository.getNextId(), "Петренко Ірина",  35000, "Загальне управління"));
        employeeRepository.save(new Cook(employeeRepository.getNextId(),          "Коваленко Михайло", 28000, "Гаряча кухня"));
        employeeRepository.save(new Cook(employeeRepository.getNextId(),          "Бондаренко Олена",  25000, "Десерти та випічка"));
        employeeRepository.save(new Waiter(employeeRepository.getNextId(),        "Лисенко Андрій",   18000, 5));
        employeeRepository.save(new Waiter(employeeRepository.getNextId(),        "Мороз Наталія",    18000, 4));

        System.out.println("[DB] Тестові дані додано.");
    }
}
