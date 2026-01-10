package com.datavet.datavet.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB configuration for the application.
 * Enables MongoDB auditing for automatic timestamp management.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Configures custom converters for MongoDB.
     * MongoDB automatically serializes Value Objects as embedded documents,
     * so custom converters are only needed for special serialization requirements.
     *
     * @return MongoCustomConversions with registered converters
     */
    @Bean
    public MongoCustomConversions customConversions() {
        List<Object> converters = new ArrayList<>();
        // Add custom converters here if needed in the future
        return new MongoCustomConversions(converters);
    }
}
