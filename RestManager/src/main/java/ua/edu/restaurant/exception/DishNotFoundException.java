package ua.edu.restaurant.exception;

public class DishNotFoundException extends RestaurantException {
    public DishNotFoundException(String message) {
        super(message);
    }
}
