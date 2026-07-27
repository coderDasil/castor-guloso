package com.castor.guloso.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Castor Guloso — lunch order automation system.
 * <p>
 * Scans all com.castor.guloso packages for entities and repositories
 * across the multi-module project structure.
 */
@SpringBootApplication(scanBasePackages = "com.castor.guloso")
@EntityScan(basePackages = "com.castor.guloso")
@EnableJpaRepositories(basePackages = "com.castor.guloso")
@EnableScheduling
public class CastorGulosoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CastorGulosoApplication.class, args);
    }
}
