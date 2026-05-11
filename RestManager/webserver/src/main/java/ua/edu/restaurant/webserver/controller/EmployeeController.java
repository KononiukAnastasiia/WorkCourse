package ua.edu.restaurant.webserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.restaurant.webserver.model.Employee;
import ua.edu.restaurant.webserver.repository.EmployeeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    // GET /api/employees
    @GetMapping
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    // POST /api/employees
    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        employee.setId(null);
        return employeeRepository.save(employee);
    }

    // PUT /api/employees/1
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employee.setId(id);
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    // DELETE /api/employees/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
