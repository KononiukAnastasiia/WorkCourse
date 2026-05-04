package ua.edu.restaurant.model;

public class Administrator extends Employee {
    private String department;

    public Administrator(int id, String name, double salary, String department) {
        super(id, name, salary);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getRole() { return "Адміністратор"; }

    @Override
    public String getDuties() {
        return "Керує відділом: " + department;
    }
}
