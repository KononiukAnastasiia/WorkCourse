package ua.edu.restaurant.exception;

public class OrderNotFoundException extends RestaurantException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
