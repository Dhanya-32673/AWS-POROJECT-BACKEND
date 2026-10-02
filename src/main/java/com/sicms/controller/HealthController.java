package com.sicms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @RequestMapping(value = {"/", "/health", "/api/health", "/actuator/health", "/favicon.ico"}, method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("system", "SICMS Student Management System");
        response.put("message", "Backend Service Operational");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @Autowired(required = false)
    private com.sicms.service.S3StorageService s3StorageService;

    @GetMapping("/api/health/mail")
    public ResponseEntity<Map<String, Object>> mailHealthCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        if (mailSender instanceof JavaMailSenderImpl mailImpl) {
            try {
                mailImpl.testConnection();
                response.put("status", "UP");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("status", "DOWN");
                response.put("error", e.getMessage() != null ? e.getMessage() : "Authentication failed");
                return ResponseEntity.status(503).body(response);
            }
        }
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/health/s3")
    public ResponseEntity<Map<String, Object>> s3HealthCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        if (s3StorageService == null) {
            response.put("status", "DISABLED");
            response.put("message", "S3 Storage Service is not initialized.");
            return ResponseEntity.ok(response);
        }
        response.put("status", "UP");
        response.put("bucket", s3StorageService.getDefaultBucketName());
        response.put("provider", "AWS S3 Blob Storage");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/api/health/s3/test-upload", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> s3TestUpload() {
        if (s3StorageService == null) {
            return ResponseEntity.status(503).body(Map.of("status", "ERROR", "message", "S3StorageService unavailable"));
        }
        Map<String, Object> result = s3StorageService.testUploadVerificationFile();
        return ResponseEntity.ok(result);
    }
}

