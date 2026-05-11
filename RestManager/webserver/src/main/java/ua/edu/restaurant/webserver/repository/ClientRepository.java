package ua.edu.restaurant.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.restaurant.webserver.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
