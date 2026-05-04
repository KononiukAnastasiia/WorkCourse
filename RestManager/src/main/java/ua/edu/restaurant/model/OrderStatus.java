package ua.edu.restaurant.model;

public enum OrderStatus {
    CREATED("Створено"),
    IN_PROGRESS("В процесі"),
    COMPLETED("Виконано"),
    CLOSED("Закрито");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
