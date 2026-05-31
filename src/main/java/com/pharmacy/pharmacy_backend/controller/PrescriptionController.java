package com.pharmacy.pharmacy_backend.controller;

import com.pharmacy.pharmacy_backend.model.Prescription;
import com.pharmacy.pharmacy_backend.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // ✅ Upload prescription — Base64-ஆ database-ல save
    @PostMapping("/upload")
    public Prescription uploadPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("customerName") String customerName,
            @RequestParam("customerEmail") String customerEmail) throws IOException {

        // File-ஐ Base64-ஆ convert பண்ணும்
        String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
        String fileType = file.getContentType();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Database-ல save பண்ணும்
        Prescription prescription = new Prescription();
        prescription.setCustomerName(customerName);
        prescription.setCustomerEmail(customerEmail);
        prescription.setFileName(fileName);
        prescription.setFileData(base64Data);
        prescription.setFileType(fileType);
        prescription.setStatus("PENDING");
        prescription.setUploadedAt(LocalDateTime.now());

        return prescriptionRepository.save(prescription);
    }

    // ✅ Get file from database
    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {

        Prescription prescription = prescriptionRepository.findByFileName(fileName);

        if (prescription == null || prescription.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileBytes = Base64.getDecoder().decode(prescription.getFileData());

        String contentType = prescription.getFileType() != null
                ? prescription.getFileType()
                : "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\"")
                .body(fileBytes);
    }

    // ✅ Get all prescriptions
    @GetMapping
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    // ✅ Verify
    @PutMapping("/{id}/verify")
    public Prescription verifyPrescription(@PathVariable Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        p.setStatus("VERIFIED");
        return prescriptionRepository.save(p);
    }

    // ✅ Reject
    @PutMapping("/{id}/reject")
    public Prescription rejectPrescription(@PathVariable Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        p.setStatus("REJECTED");
        return prescriptionRepository.save(p);
    }
}