package ua.edu.restaurant.model;

public class Waiter extends Employee {
    private int tableCount;

    public Waiter(int id, String name, double salary, int tableCount) {
        super(id, name, salary);
        this.tableCount = tableCount;
    }

    public int getTableCount() { return tableCount; }
    public void setTableCount(int tableCount) { this.tableCount = tableCount; }

    @Override
    public String getRole() { return "Офіціант"; }

    @Override
    public String getDuties() {
        return "Приймає замовлення, обслуговує " + tableCount + " столиків";
    }
}
