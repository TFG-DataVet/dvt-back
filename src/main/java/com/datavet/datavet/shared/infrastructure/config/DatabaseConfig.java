package com.datavet.datavet.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for the application.
 * Enables JPA auditing, repositories, and transaction management.
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.datavet.datavet")
@EnableTransactionManagement
public class DatabaseConfig {
}