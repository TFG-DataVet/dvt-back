package com.datavet.datavet.shared.infrastructure.event;

import com.datavet.datavet.shared.domain.event.DomainEvent;
import com.datavet.datavet.shared.domain.event.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Simple implementation of DomainEventPublisher that logs events.
 * In a real application, this would publish to a message broker or event store.
 */
@Slf4j
@Component
public class LoggingDomainEventPublisher implements DomainEventPublisher {
    
    public LoggingDomainEventPublisher() {
        System.out.println("🔍 DEBUG: LoggingDomainEventPublisher created!");
    }
    
    @Override
    public void publish(DomainEvent event) {
        log.info("🚀 DOMAIN EVENT PUBLISHED: {} at {}", 
                event.getClass().getSimpleName(), 
                event.occurredOn());
        log.info("📋 Event details: {}", event.toString());
    }
}