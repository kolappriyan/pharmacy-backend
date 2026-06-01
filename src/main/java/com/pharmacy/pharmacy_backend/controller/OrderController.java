package com.pharmacy.pharmacy_backend.controller;

import com.pharmacy.pharmacy_backend.model.Order;
import com.pharmacy.pharmacy_backend.model.Medicine;
import com.pharmacy.pharmacy_backend.repository.OrderRepository;
import com.pharmacy.pharmacy_backend.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        order.setStatus("Pending");
        order.setOrderDate(LocalDateTime.now());

        // Stock குறைக்கும்
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                if (item.getMedicineId() != null) {
                    medicineRepository.findById(item.getMedicineId()).ifPresent(medicine -> {
                        int newStock = medicine.getStock() - item.getQuantity();
                        medicine.setStock(Math.max(newStock, 0));
                        medicineRepository.save(medicine);
                    });
                }
            }
        }

        return orderRepository.save(order);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return orderRepository.save(order);
    }
}