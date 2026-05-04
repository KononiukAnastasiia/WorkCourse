package ua.edu.restaurant.repository;

import ua.edu.restaurant.model.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DishRepository {
    private final Map<Integer, Dish> storage = new HashMap<>();
    private int nextId = 1;

    public Dish save(Dish dish) {
        if (dish.getId() == 0) {
            // Нова страва — призначаємо ID
            Dish newDish = new Dish(nextId++, dish.getName(), dish.getPrice(),
                    dish.getCategory(), dish.getDescription());
            storage.put(newDish.getId(), newDish);
            return newDish;
        }
        storage.put(dish.getId(), dish);
        return dish;
    }

    public Dish saveNew(String name, double price, String category, String description) {
        Dish dish = new Dish(nextId++, name, price, category, description);
        storage.put(dish.getId(), dish);
        return dish;
    }

    public Dish findById(int id) {
        return storage.get(id);
    }

    public List<Dish> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean deleteById(int id) {
        return storage.remove(id) != null;
    }

    public boolean existsById(int id) {
        return storage.containsKey(id);
    }
}
