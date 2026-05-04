package ua.edu.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<Dish> dishes;

    public Menu() {
        this.dishes = new ArrayList<>();
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
    }

    public List<Dish> getDishes() {
        return new ArrayList<>(dishes);
    }

    public List<Dish> getDishesByCategory(String category) {
        List<Dish> result = new ArrayList<>();
        for (Dish dish : dishes) {
            if (dish.getCategory().equalsIgnoreCase(category)) {
                result.add(dish);
            }
        }
        return result;
    }
}
