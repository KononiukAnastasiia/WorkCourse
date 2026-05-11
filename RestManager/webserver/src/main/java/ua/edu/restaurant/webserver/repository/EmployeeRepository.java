package ua.edu.restaurant.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.restaurant.webserver.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
