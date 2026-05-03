package com.pharmacy.pharmacy_backend.repository;

import com.pharmacy.pharmacy_backend.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}