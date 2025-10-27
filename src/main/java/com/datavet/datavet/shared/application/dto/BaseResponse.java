package com.datavet.datavet.shared.application.dto;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Base class for response DTOs.
 * Provides common fields and functionality for all response objects.
 */
@Getter
public abstract class BaseResponse {
    
    private final LocalDateTime timestamp;
    
    protected BaseResponse() {
        this.timestamp = LocalDateTime.now();
    }
}