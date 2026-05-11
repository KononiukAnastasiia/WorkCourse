package ua.edu.restaurant.webserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.restaurant.webserver.model.Dish;
import ua.edu.restaurant.webserver.repository.DishRepository;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishRepository dishRepository;

    // GET /api/dishes — отримати всі страви
    @GetMapping
    public List<Dish> getAll() {
        return dishRepository.findAll();
    }

    // GET /api/dishes/1 — отримати одну страву
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getById(@PathVariable Long id) {
        return dishRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/dishes — додати нову страву
    @PostMapping
    public Dish create(@RequestBody Dish dish) {
        dish.setId(null); // щоб БД сама призначила ID
        return dishRepository.save(dish);
    }

    // PUT /api/dishes/1 — оновити страву
    @PutMapping("/{id}")
    public ResponseEntity<Dish> update(@PathVariable Long id, @RequestBody Dish dish) {
        if (!dishRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dish.setId(id);
        return ResponseEntity.ok(dishRepository.save(dish));
    }

    // DELETE /api/dishes/1 — видалити страву
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!dishRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dishRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
