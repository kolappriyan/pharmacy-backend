package com.pharmacy.pharmacy_backend.controller;

import com.pharmacy.pharmacy_backend.model.Prescription;
import com.pharmacy.pharmacy_backend.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // Upload folder path
    private final Path uploadDir = Paths.get("uploads");

    // ✅ Upload prescription — file disk-ல save ஆகும்
    @PostMapping("/upload")
    public Prescription uploadPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("customerName") String customerName,
            @RequestParam("customerEmail") String customerEmail) throws IOException {

        // uploads folder இல்லன்னா create பண்ணும்
        Files.createDirectories(uploadDir);

        // File save பண்ணும்
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Database-ல save பண்ணும்
        Prescription prescription = new Prescription();
        prescription.setCustomerName(customerName);
        prescription.setCustomerEmail(customerEmail);
        prescription.setFileName(fileName);
        prescription.setStatus("PENDING");
        prescription.setUploadedAt(LocalDateTime.now());

        return prescriptionRepository.save(prescription);
    }
    @GetMapping("/file/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) 
        throws MalformedURLException {
    Path filePath = uploadDir.resolve(fileName);
    Resource resource = new UrlResource(filePath.toUri());

    String contentType = "application/octet-stream";
    if (fileName.endsWith(".pdf")) {
        contentType = "application/pdf";
    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
        contentType = "image/jpeg";
    } else if (fileName.endsWith(".png")) {
        contentType = "image/png";
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "inline; filename=\"" + fileName + "\"")
            .body(resource);
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