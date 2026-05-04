package ua.edu.restaurant.model;

public abstract class Employee {
    private int id;
    private String name;
    private double salary;

    public Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getSalary() { return salary; }

    public void setName(String name) { this.name = name; }
    public void setSalary(double salary) { this.salary = salary; }

    // Абстрактний метод — кожен тип персоналу описує свої обов'язки
    public abstract String getRole();
    public abstract String getDuties();

    @Override
    public String toString() {
        return String.format("[%d] %-20s | %-15s | %.2f грн/міс", id, name, getRole(), salary);
    }
}
