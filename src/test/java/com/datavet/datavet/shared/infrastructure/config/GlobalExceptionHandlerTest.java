package com.datavet.datavet.shared.infrastructure.config;

import com.datavet.datavet.shared.infrastructure.dto.ErrorResponse;
import com.datavet.datavet.shared.infrastructure.config.GlobalExceptionHandler;
import com.datavet.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.datavet.clinic.domain.exception.ClinicNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @Mock
    private Path propertyPath;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/clinic");
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithValidationDetails() {
        // Given
        FieldError fieldError = new FieldError("createClinicCommand", "email", "invalid@", false, null, null, "must be a well-formed email address");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).hasSize(1);
        assertThat(response.getBody().getDetails().get(0).getField()).isEqualTo("email");
        assertThat(response.getBody().getDetails().get(0).getMessage()).isEqualTo("must be a well-formed email address");
        assertThat(response.getBody().getDetails().get(0).getRejectedValue()).isEqualTo("invalid@");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequestWithConstraintDetails() {
        // Given
        when(propertyPath.toString()).thenReturn("email");
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(constraintViolation.getMessage()).thenReturn("must be a well-formed email address");
        when(constraintViolation.getInvalidValue()).thenReturn("invalid@");
        when(constraintViolationException.getConstraintViolations()).thenReturn(Set.of(constraintViolation));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolationException(constraintViolationException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).hasSize(1);
        assertThat(response.getBody().getDetails().get(0).getField()).isEqualTo("email");
        assertThat(response.getBody().getDetails().get(0).getMessage()).isEqualTo("must be a well-formed email address");
        assertThat(response.getBody().getDetails().get(0).getRejectedValue()).isEqualTo("invalid@");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundWithMessage() {
        // Given
        ClinicNotFoundException exception = new ClinicNotFoundException("Clinic not found with id: 1");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleEntityNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Clinic not found with id: 1");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).isEmpty();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleEntityNotFoundException_WithIdConstructor_ShouldReturnNotFoundWithFormattedMessage() {
        // Given
        ClinicNotFoundException exception = new ClinicNotFoundException(1L);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleEntityNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Clinic not found with id: 1");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).isEmpty();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleEntityAlreadyExistsException_ShouldReturnConflictWithMessage() {
        // Given
        ClinicAlreadyExistsException exception = new ClinicAlreadyExistsException("Clinic already exists with email: test@example.com");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleEntityAlreadyExistsException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage()).isEqualTo("Clinic already exists with email: test@example.com");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).isEmpty();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleEntityAlreadyExistsException_WithFieldConstructor_ShouldReturnConflictWithFormattedMessage() {
        // Given
        ClinicAlreadyExistsException exception = new ClinicAlreadyExistsException("email", "test@example.com");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleEntityAlreadyExistsException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage()).isEqualTo("Clinic already exists with email: test@example.com");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).isEmpty();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorWithGenericMessage() {
        // Given
        Exception exception = new RuntimeException("Unexpected database error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getPath()).isEqualTo("/clinic");
        assertThat(response.getBody().getDetails()).isEmpty();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleValidationException_WithMultipleFieldErrors_ShouldReturnAllErrors() {
        // Given
        FieldError emailError = new FieldError("createClinicCommand", "email", "invalid@", false, null, null, "must be a well-formed email address");
        FieldError nameError = new FieldError("createClinicCommand", "clinicName", "", false, null, null, "must not be blank");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(emailError, nameError));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetails()).hasSize(2);
        
        // Check email error
        ErrorResponse.ValidationErrorDetail emailDetail = response.getBody().getDetails().stream()
                .filter(detail -> "email".equals(detail.getField()))
                .findFirst()
                .orElseThrow();
        assertThat(emailDetail.getMessage()).isEqualTo("must be a well-formed email address");
        assertThat(emailDetail.getRejectedValue()).isEqualTo("invalid@");
        
        // Check name error
        ErrorResponse.ValidationErrorDetail nameDetail = response.getBody().getDetails().stream()
                .filter(detail -> "clinicName".equals(detail.getField()))
                .findFirst()
                .orElseThrow();
        assertThat(nameDetail.getMessage()).isEqualTo("must not be blank");
        assertThat(nameDetail.getRejectedValue()).isEqualTo("");
    }
}