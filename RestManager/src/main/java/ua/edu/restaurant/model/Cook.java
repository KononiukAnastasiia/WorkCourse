package ua.edu.restaurant.model;

public class Cook extends Employee {
    private String specialization;

    public Cook(int id, String name, double salary, String specialization) {
        super(id, name, salary);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    @Override
    public String getRole() { return "Кухар"; }

    @Override
    public String getDuties() {
        return "Готує страви, спеціалізація: " + specialization;
    }
}
