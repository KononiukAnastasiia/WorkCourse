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

    // DTO для повної інформації про замовлення
    record OrderResponse(
        Long id, String clientName, String clientPhone,
        String status, String createdAt,
        List<OrderItemDetail> items, Double total
    ) {}

    record OrderItemDetail(Long dishId, String dishName, Integer qty, Double price, Double subtotal) {}

    record CreateOrderRequest(String clientName, String clientPhone, List<ItemRequest> items) {}
    record ItemRequest(Long dishId, Integer qty) {}
    record StatusRequest(String status) {}

    // GET /api/orders — всі замовлення з деталями
    @GetMapping
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
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
        // Зберегти клієнта
        Client client = new Client(null, req.clientName(), req.clientPhone() != null ? req.clientPhone() : "");
        client = clientRepository.save(client);

        // Зберегти замовлення
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Order order = new Order(null, client.getId(), "CREATED", now);
        order = orderRepository.save(order);

        // Зберегти позиції
        for (ItemRequest item : req.items()) {
            OrderItem oi = new OrderItem(null, order.getId(), item.dishId(), item.qty());
            orderItemRepository.save(oi);
        }

        return toResponse(order);
    }

    // PATCH /api/orders/1/status — змінити статус
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestBody StatusRequest req) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(req.status());
            return ResponseEntity.ok(toResponse(orderRepository.save(order)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/orders/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) return ResponseEntity.notFound().build();
        orderItemRepository.findByOrderId(id).forEach(oi -> orderItemRepository.deleteById(oi.getId()));
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Перетворити Order у повну відповідь з деталями
    private OrderResponse toResponse(Order order) {
        Client client = clientRepository.findById(order.getClientId()).orElse(new Client(null, "Невідомо", ""));
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
