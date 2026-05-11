package ua.edu.restaurant.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.restaurant.webserver.model.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
}
