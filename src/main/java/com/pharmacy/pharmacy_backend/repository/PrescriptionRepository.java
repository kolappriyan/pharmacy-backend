package com.pharmacy.pharmacy_backend.repository;

import com.pharmacy.pharmacy_backend.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Prescription findByFileName(String fileName);
}