package com.sicms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.util.logging.Logger;

@Configuration
public class S3Config {

    private static final Logger log = Logger.getLogger(S3Config.class.getName());

    @Value("${AWS_REGION:${aws.s3.region:us-east-1}}")
    private String region;

    @Value("${AWS_ACCESS_KEY_ID:${aws.s3.access-key-id:}}")
    private String accessKeyId;

    @Value("${AWS_SECRET_ACCESS_KEY:${aws.s3.secret-access-key:}}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        try {
            S3ClientBuilder builder = S3Client.builder()
                    .region(Region.of(region != null && !region.isBlank() ? region : "us-east-1"));

            if (accessKeyId != null && !accessKeyId.isBlank() && secretAccessKey != null && !secretAccessKey.isBlank()) {
                log.info(">>> S3Client: Initializing with explicit static credentials for region: " + region);
                builder.credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId.trim(), secretAccessKey.trim())
                ));
            } else {
                log.info(">>> S3Client: Initializing with DefaultCredentialsProvider (IAM role / Env / Profiles) for region: " + region);
                builder.credentialsProvider(DefaultCredentialsProvider.create());
            }

            return builder.build();
        } catch (Exception e) {
            log.warning(">>> S3Client Initialization Warning: " + e.getMessage());
            // Fallback default client
            return S3Client.builder()
                    .region(Region.of("us-east-1"))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }
}
