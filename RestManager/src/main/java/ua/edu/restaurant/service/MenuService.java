package ua.edu.restaurant.service;

import ua.edu.restaurant.exception.DishNotFoundException;
import ua.edu.restaurant.exception.ValidationException;
import ua.edu.restaurant.model.Dish;
import ua.edu.restaurant.util.ApiClient;
import ua.edu.restaurant.util.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MenuService {

    public List<Dish> getAllDishes() {
        String json = ApiClient.get("/dishes");
        List<String> items = JsonParser.parseArray(json);
        List<Dish> dishes = new ArrayList<>();
        for (String item : items) {
            dishes.add(parseDish(item));
        }
        return dishes;
    }

    public Dish getDishById(int id) {
        String json = ApiClient.get("/dishes/" + id);
        if (json == null || json.isBlank() || json.equals("null")) {
            throw new DishNotFoundException("Страву з ID " + id + " не знайдено.");
        }
        return parseDish(json);
    }

    public Dish addDish(String name, double price, String category, String description) {
        validateDishData(name, price, category);
        String body = "{\"name\":\"" + name + "\",\"price\":" + price +
                      ",\"category\":\"" + category + "\",\"description\":\"" + description + "\"}";
        String json = ApiClient.post("/dishes", body);
        return parseDish(json);
    }

    public Dish updateDish(int id, String name, double price, String category, String description) {
        validateDishData(name, price, category);
        String body = "{\"name\":\"" + name + "\",\"price\":" + price +
                      ",\"category\":\"" + category + "\",\"description\":\"" + description + "\"}";
        String json = ApiClient.put("/dishes/" + id, body);
        return parseDish(json);
    }

    public void deleteDish(int id) {
        ApiClient.delete("/dishes/" + id);
    }

    private void validateDishData(String name, double price, String category) {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Назва страви не може бути порожньою.");
        if (price <= 0)
            throw new ValidationException("Ціна страви повинна бути більше нуля.");
        if (category == null || category.trim().isEmpty())
            throw new ValidationException("Категорія страви не може бути порожньою.");
    }

    private Dish parseDish(String json) {
        return new Dish(
            JsonParser.getInt(json, "id"),
            JsonParser.getString(json, "name"),
            JsonParser.getDouble(json, "price"),
            JsonParser.getString(json, "category"),
            JsonParser.getString(json, "description")
        );
    }
}
