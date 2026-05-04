package ua.edu.restaurant.service;

import ua.edu.restaurant.exception.DishNotFoundException;
import ua.edu.restaurant.exception.ValidationException;
import ua.edu.restaurant.model.Dish;
import ua.edu.restaurant.repository.DishRepository;

import java.util.List;

public class MenuService {
    private final DishRepository dishRepository;

    public MenuService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public Dish addDish(String name, double price, String category, String description) {
        validateDishData(name, price, category);
        return dishRepository.saveNew(name, price, category, description);
    }

    public Dish updateDish(int id, String name, double price, String category, String description) {
        Dish dish = getDishById(id);
        validateDishData(name, price, category);
        dish.setName(name);
        dish.setPrice(price);
        dish.setCategory(category);
        dish.setDescription(description);
        return dishRepository.save(dish);
    }

    public void deleteDish(int id) {
        if (!dishRepository.existsById(id)) {
            throw new DishNotFoundException("Страву з ID " + id + " не знайдено.");
        }
        dishRepository.deleteById(id);
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Dish getDishById(int id) {
        Dish dish = dishRepository.findById(id);
        if (dish == null) {
            throw new DishNotFoundException("Страву з ID " + id + " не знайдено.");
        }
        return dish;
    }

    private void validateDishData(String name, double price, String category) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Назва страви не може бути порожньою.");
        }
        if (price <= 0) {
            throw new ValidationException("Ціна страви повинна бути більше нуля.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Категорія страви не може бути порожньою.");
        }
    }
}
