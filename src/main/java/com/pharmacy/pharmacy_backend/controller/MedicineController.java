package com.pharmacy.pharmacy_backend.controller;

import com.pharmacy.pharmacy_backend.model.Medicine;
import com.pharmacy.pharmacy_backend.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @PostMapping
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @DeleteMapping("/{id}")
    public void deleteMedicine(@PathVariable Long id) {
        medicineRepository.deleteById(id);
    }

    // Stock reduce endpoint
    @PutMapping("/{id}/reduce-stock")
    public Medicine reduceStock(@PathVariable Long id, @RequestParam int quantity) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        int newStock = medicine.getStock() - quantity;
        medicine.setStock(Math.max(newStock, 0));
        return medicineRepository.save(medicine);
    }
}