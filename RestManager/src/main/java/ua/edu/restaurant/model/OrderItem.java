package ua.edu.restaurant.model;

public class OrderItem {
    private Dish dish;
    private int quantity;

    public OrderItem(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    public Dish getDish() { return dish; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() {
        return dish.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("  %-25s x%d = %.2f грн", dish.getName(), quantity, getSubtotal());
    }
}
