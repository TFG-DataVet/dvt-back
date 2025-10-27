# Implementation Plan

- [x] 1. Fix command DTOs and add validations
  - Correct typo in CreateClinicCommand (legalNumer → legalNumber)
  - Add Bean Validation annotations to CreateClinicCommand fields
  - Add Bean Validation annotations to UpdateClinicCommand fields
  - Ensure field name consistency between commands and domain model
  - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [x] 2. Create error handling infrastructure
  - [x] 2.1 Create custom exception classes
    - Implement ClinicNotFoundException for 404 scenarios
    - Implement ClinicAlreadyExistsException for 409 scenarios
    - _Requirements: 3.1, 3.4_
  
  - [x] 2.2 Create ErrorResponse DTO
    - Design ErrorResponse class with timestamp, status, error, message, details, and path fields
    - Include validation error details structure
    - _Requirements: 3.2_
  
  - [x] 2.3 Implement GlobalExceptionHandler
    - Handle ValidationException and return 400 with field-specific errors
    - Handle ClinicNotFoundException and return 404
    - Handle ClinicAlreadyExistsException and return 409
    - Handle generic exceptions and return 500
    - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 3. Update service layer to throw appropriate exceptions
  - Modify ClinicService to throw ClinicNotFoundException when clinic not found
  - Add validation for duplicate clinic data and throw ClinicAlreadyExistsException
  - _Requirements: 3.1, 3.4_

- [x] 4. Refactor ClinicController to use DTOs consistently
  - [x] 4.1 Update GET /clinic/{id} endpoint
    - Change return type to ResponseEntity<ClinicResponse>
    - Add @Valid annotation and proper error handling
    - _Requirements: 1.1, 4.3_
  
  - [x] 4.2 Update GET /clinic endpoint
    - Change return type to ResponseEntity<List<ClinicResponse>>
    - Map all entities to ClinicResponse DTOs
    - _Requirements: 1.2, 4.3_
  
  - [x] 4.3 Update PUT /clinic/{id} endpoint
    - Change return type to ResponseEntity<ClinicResponse>
    - Remove redundant command reconstruction logic
    - Add path ID validation against command ID
    - Add @Valid annotation for request body validation
    - _Requirements: 1.3, 1.4, 4.4_
  
  - [x] 4.4 Update POST /clinic endpoint
    - Change return type to ResponseEntity<ClinicResponse> with 201 status
    - Add @Valid annotation for request body validation
    - _Requirements: 4.1, 4.3_

- [x] 5. Add validation dependency to pom.xml
  - Ensure spring-boot-starter-validation is properly configured
  - _Requirements: 2.1, 2.2_

- [x] 6. Create comprehensive tests
  - [x] 6.1 Write unit tests for validation annotations
    - Test CreateClinicCommand validation with valid and invalid data
    - Test UpdateClinicCommand validation with valid and invalid data
    - _Requirements: 2.1, 2.2, 2.3, 2.4_
  
  - [x] 6.2 Write integration tests for controller endpoints
    - Test all CRUD operations with valid data return correct DTOs
    - Test validation errors return 400 with proper error details
    - Test not found scenarios return 404
    - Test duplicate data scenarios return 409
    - _Requirements: 1.1, 1.2, 1.3, 3.1, 3.2, 3.4_
  
  - [x] 6.3 Write unit tests for exception handling
    - Test GlobalExceptionHandler handles all exception types correctly
    - Test custom exceptions are thrown in appropriate scenarios
    - _Requirements: 3.1, 3.2, 3.3, 3.4_