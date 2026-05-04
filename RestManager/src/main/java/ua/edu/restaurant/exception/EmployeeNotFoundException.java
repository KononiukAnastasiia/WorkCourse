package ua.edu.restaurant.exception;

public class EmployeeNotFoundException extends RestaurantException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
