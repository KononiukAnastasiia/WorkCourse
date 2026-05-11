package ua.edu.restaurant.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.restaurant.webserver.model.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {
}
