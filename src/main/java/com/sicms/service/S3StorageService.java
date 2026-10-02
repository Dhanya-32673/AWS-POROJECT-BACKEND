package com.sicms.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class S3StorageService {

    private static final Logger log = Logger.getLogger(S3StorageService.class.getName());

    private final S3Client s3Client;

    @Value("${AWS_S3_BUCKET:${aws.s3.bucket.name:sicms-storage}}")
    private String defaultBucketName;

    @Value("${AWS_REGION:${aws.s3.region:us-east-1}}")
    private String region;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void init() {
        log.info("==========================================================");
        log.info("AWS S3 STORAGE INITIALIZATION");
        log.info("Target Bucket: " + defaultBucketName);
        log.info("AWS Region   : " + region);
        log.info("==========================================================");
    }

    public String getDefaultBucketName() {
        return defaultBucketName;
    }

    /**
     * Upload raw bytes to S3 bucket.
     */
    public boolean uploadFile(String bucketName, String key, byte[] bytes, String contentType) {
        if (bucketName == null || bucketName.isBlank()) {
            bucketName = defaultBucketName;
        }
        try {
            PutObjectRequest.Builder builder = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key);

            if (contentType != null && !contentType.isBlank()) {
                builder.contentType(contentType);
            } else {
                builder.contentType("application/octet-stream");
            }

            s3Client.putObject(builder.build(), RequestBody.fromBytes(bytes));
            log.info(">>> [AWS S3] Upload successful to bucket [" + bucketName + "] key [" + key + "]");
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, ">>> [AWS S3] Upload failed for key [" + key + "]: " + e.getMessage());
            return false;
        }
    }

    public boolean uploadFile(String key, byte[] bytes, String contentType) {
        return uploadFile(defaultBucketName, key, bytes, contentType);
    }

    /**
     * Download file bytes from S3.
     */
    public byte[] downloadFile(String bucketName, String key) {
        if (bucketName == null || bucketName.isBlank()) {
            bucketName = defaultBucketName;
        }
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(getRequest);
            return response.asByteArray();
        } catch (NoSuchKeyException e) {
            log.fine(">>> [AWS S3] Key not found in S3: " + key);
            return null;
        } catch (Exception e) {
            log.log(Level.WARNING, ">>> [AWS S3] Download failed for key [" + key + "]: " + e.getMessage());
            return null;
        }
    }

    public byte[] downloadFile(String key) {
        return downloadFile(defaultBucketName, key);
    }

    /**
     * Delete an object from S3.
     */
    public boolean deleteFile(String bucketName, String key) {
        if (bucketName == null || bucketName.isBlank()) {
            bucketName = defaultBucketName;
        }
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info(">>> [AWS S3] Deleted object from S3: " + key);
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, ">>> [AWS S3] Delete failed for key [" + key + "]: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFile(String key) {
        return deleteFile(defaultBucketName, key);
    }

    /**
     * Check if an object exists in S3.
     */
    public boolean doesFileExist(String bucketName, String key) {
        if (bucketName == null || bucketName.isBlank()) {
            bucketName = defaultBucketName;
        }
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean doesFileExist(String key) {
        return doesFileExist(defaultBucketName, key);
    }

    /**
     * Get direct S3 HTTPS URL.
     */
    public String getS3Url(String bucketName, String key) {
        if (bucketName == null || bucketName.isBlank()) {
            bucketName = defaultBucketName;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    public String getS3Url(String key) {
        return getS3Url(defaultBucketName, key);
    }

    /**
     * Diagnostic method to test connection and add a verification file to S3.
     */
    public Map<String, Object> testUploadVerificationFile() {
        Map<String, Object> result = new LinkedHashMap<>();
        String testKey = "system-verification/test-connection-" + System.currentTimeMillis() + ".txt";
        String content = "SICMS Student Management System - AWS S3 Storage Verification File\n"
                + "Timestamp: " + LocalDateTime.now() + "\n"
                + "Bucket: " + defaultBucketName + "\n"
                + "Region: " + region + "\n"
                + "Status: S3 Blob Storage Connected Successfully\n";

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        result.put("bucket", defaultBucketName);
        result.put("key", testKey);
        result.put("region", region);

        boolean uploaded = uploadFile(defaultBucketName, testKey, bytes, "text/plain");
        if (uploaded) {
            result.put("status", "SUCCESS");
            result.put("message", "File uploaded successfully to AWS S3 bucket: " + defaultBucketName);
            result.put("url", getS3Url(testKey));
        } else {
            result.put("status", "FAILED");
            result.put("message", "Unable to upload to S3. Verify AWS IAM permissions or credentials.");
        }
        return result;
    }
}
