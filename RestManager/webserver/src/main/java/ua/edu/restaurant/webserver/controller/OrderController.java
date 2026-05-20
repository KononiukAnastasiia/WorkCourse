package ua.edu.restaurant.webserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.restaurant.webserver.model.*;
import ua.edu.restaurant.webserver.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;

    record OrderResponse(
        Long id, String clientName, String clientPhone,
        String status, String createdAt,
        List<OrderItemDetail> items, Double total
    ) {}

    record OrderItemDetail(Long dishId, String dishName, Integer qty, Double price, Double subtotal) {}
    record CreateOrderRequest(String clientName, String clientPhone, List<ItemRequest> items) {}
    record ItemRequest(Long dishId, Integer qty) {}
    record StatusRequest(String status) {}
    record AddItemRequest(Long dishId, Integer quantity) {}

    // GET /api/orders
    @GetMapping
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    // GET /api/orders/1
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(o -> ResponseEntity.ok(toResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/orders — створити замовлення
    @PostMapping
    public OrderResponse create(@RequestBody CreateOrderRequest req) {
        Client client = new Client(null,
            req.clientName(),
            req.clientPhone() != null ? req.clientPhone() : "");
        client = clientRepository.save(client);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Order order = new Order(null, client.getId(), "CREATED", now);
        order = orderRepository.save(order);

        if (req.items() != null) {
            for (ItemRequest item : req.items()) {
                OrderItem oi = new OrderItem(null, order.getId(), item.dishId(), item.qty());
                orderItemRepository.save(oi);
            }
        }
        return toResponse(order);
    }

    // PATCH /api/orders/1/status — змінити статус
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id, @RequestBody StatusRequest req) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(req.status());
            return ResponseEntity.ok(toResponse(orderRepository.save(order)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/orders/1/add-item — додати страву до замовлення
    @PatchMapping("/{id}/add-item")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable Long id, @RequestBody AddItemRequest req) {
        return orderRepository.findById(id).map(order -> {
            // Перевіряємо чи страва вже є в замовленні
            List<OrderItem> existing = orderItemRepository.findByOrderId(id);
            Optional<OrderItem> found = existing.stream()
                    .filter(oi -> oi.getDishId().equals(req.dishId()))
                    .findFirst();

            if (found.isPresent()) {
                // Збільшуємо кількість
                OrderItem oi = found.get();
                oi.setQuantity(oi.getQuantity() + req.quantity());
                orderItemRepository.save(oi);
            } else {
                // Додаємо нову позицію
                OrderItem oi = new OrderItem(null, id, req.dishId(), req.quantity());
                orderItemRepository.save(oi);
            }
            return ResponseEntity.ok(toResponse(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/orders/1/remove-item — видалити страву із замовлення
    @PatchMapping("/{id}/remove-item/{dishId}")
    public ResponseEntity<OrderResponse> removeItem(
            @PathVariable Long id, @PathVariable Long dishId) {
        return orderRepository.findById(id).map(order -> {
            orderItemRepository.findByOrderId(id).stream()
                    .filter(oi -> oi.getDishId().equals(dishId))
                    .findFirst()
                    .ifPresent(oi -> orderItemRepository.deleteById(oi.getId()));
            return ResponseEntity.ok(toResponse(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/orders/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) return ResponseEntity.notFound().build();
        orderItemRepository.findByOrderId(id)
                .forEach(oi -> orderItemRepository.deleteById(oi.getId()));
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private OrderResponse toResponse(Order order) {
        Client client = clientRepository.findById(order.getClientId())
                .orElse(new Client(null, "Невідомо", ""));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDetail> details = items.stream().map(oi -> {
            Dish dish = dishRepository.findById(oi.getDishId()).orElse(null);
            String name = dish != null ? dish.getName() : "Видалена страва";
            double price = dish != null ? dish.getPrice() : 0;
            return new OrderItemDetail(oi.getDishId(), name, oi.getQuantity(), price, price * oi.getQuantity());
        }).toList();
        double total = details.stream().mapToDouble(OrderItemDetail::subtotal).sum();
        return new OrderResponse(order.getId(), client.getName(), client.getPhone(),
                order.getStatus(), order.getCreatedAt(), details, total);
    }
}
