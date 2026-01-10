# Implementation Plan

- [x] 1. Crear Test Data Builder y Configuración Base
  - Crear clase ClinicTestDataBuilder con métodos helper para generar datos de test válidos
  - Crear application-test.properties con configuración de H2 in-memory database
  - Verificar que las dependencias de testing están en pom.xml (spring-boot-starter-test, h2)
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.10, 1.11_

- [x] 2. Implementar Tests de Domain Layer - Modelo Clinic
  - Crear ClinicTest.java con tests unitarios del modelo de dominio
  - Implementar test shouldCreateClinicWithValidData para validar factory method
  - Implementar test shouldGenerateCreatedAtAndUpdatedAtOnCreation para validar timestamps
  - Implementar test shouldUpdateClinicFieldsCorrectly para validar actualización
  - Implementar test shouldUpdateUpdatedAtOnUpdate para validar actualización de timestamp
  - Implementar test shouldNotModifyCreatedAtOnUpdate para validar inmutabilidad de createdAt
  - _Requirements: 6.4, 6.5_

- [x] 3. Implementar Tests de Domain Events
  - Crear tests en ClinicTest.java para validar generación de eventos
  - Implementar test shouldGenerateClinicCreatedEventOnCreation
  - Implementar test shouldGenerateClinicUpdatedEventOnUpdate
  - Implementar test shouldGenerateClinicDeletedEventOnDelete
  - Implementar test shouldClearDomainEventsAfterRetrieval
  - Revisar y actualizar ClinicDomainEventsTest.java existente si es necesario
  - _Requirements: 3.1, 3.2, 3.3, 3.5_

- [x] 4. Implementar Tests de Excepciones de Dominio
  - Crear ClinicExceptionsTest.java para validar excepciones
  - Implementar test clinicNotFoundExceptionShouldContainClinicId
  - Implementar test clinicAlreadyExistsExceptionShouldContainFieldInfo
  - Implementar test clinicValidationExceptionShouldContainValidationResult
  - Implementar test clinicValidationExceptionShouldFormatErrorsCorrectly
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [x] 5. Implementar Tests de Validación - CreateClinicCommand
  - Crear CreateClinicCommandValidatorTest.java
  - Implementar test shouldPassValidationWithValidCommand
  - Implementar tests para validación de clinicName (nulo, vacío, excede longitud)
  - Implementar tests para validación de legalName (nulo, vacío, excede longitud)
  - Implementar tests para validación de legalNumber (nulo, vacío, excede longitud)
  - Implementar tests para validación de Value Objects (address, phone, email nulos)
  - Implementar tests para validación de logoUrl (excede longitud, acepta nulo)
  - Implementar test shouldCollectMultipleValidationErrors
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.10, 1.11_

- [x] 6. Implementar Tests de Validación - UpdateClinicCommand
  - Crear UpdateClinicCommandValidatorTest.java
  - Implementar test shouldPassValidationWithValidCommand
  - Implementar tests para validación de clinicId (nulo, negativo, cero)
  - Implementar todos los tests de validación de CreateClinicCommand aplicables
  - Implementar tests para validación de subscriptionStatus (excede longitud, acepta nulo)
  - _Requirements: 6.1, 6.2, 6.3_

- [x] 7. Implementar Tests de ClinicService - Operaciones de Creación
  - Crear ClinicServiceTest.java con configuración de mocks
  - Implementar test shouldCreateClinicSuccessfully
  - Implementar test shouldThrowExceptionWhenEmailAlreadyExists
  - Implementar test shouldThrowExceptionWhenLegalNumberAlreadyExists
  - Implementar test shouldThrowValidationExceptionForInvalidCommand
  - Implementar test shouldPublishDomainEventsOnCreation
  - Implementar test shouldClearDomainEventsAfterPublishing
  - Implementar test shouldSetSubscriptionStatusToActiveOnCreation
  - _Requirements: 2.1, 2.2, 3.4_

- [x] 8. Implementar Tests de ClinicService - Operaciones de Actualización
  - Implementar test shouldUpdateClinicSuccessfully en ClinicServiceTest.java
  - Implementar test shouldThrowExceptionWhenClinicNotFound
  - Implementar test shouldThrowExceptionWhenEmailBelongsToAnotherClinic
  - Implementar test shouldAllowSameEmailForSameClinic
  - Implementar test shouldPublishDomainEventsOnUpdate
  - Implementar test shouldUpdateUpdatedAtTimestamp
  - Implementar test shouldNotModifyCreatedAtTimestamp
  - _Requirements: 2.3, 2.4, 3.4, 6.4, 6.5_

- [ ] 9. Implementar Tests de ClinicService - Operaciones de Consulta y Eliminación
  - Implementar test shouldGetClinicByIdSuccessfully en ClinicServiceTest.java
  - Implementar test shouldThrowExceptionWhenClinicNotFoundById
  - Implementar test shouldGetAllClinicsSuccessfully
  - Implementar test shouldReturnEmptyListWhenNoClinicsExist
  - Implementar test shouldDeleteClinicSuccessfully
  - Implementar test shouldThrowExceptionWhenDeletingNonExistentClinic
  - Implementar test shouldPublishDomainEventsOnDeletion
  - _Requirements: 4.1, 4.2, 4.3, 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 10. Implementar Tests de Repository - Operaciones Básicas
  - Crear ClinicRepositoryTest.java con @DataJpaTest
  - Implementar test shouldSaveClinicAndGenerateId
  - Implementar test shouldFindClinicById
  - Implementar test shouldReturnEmptyWhenClinicNotFound
  - Implementar test shouldFindAllClinics
  - Implementar test shouldDeleteClinicById
  - _Requirements: 5.1, 5.4, 5.5, 8.4_

- [x] 11. Implementar Tests de Repository - Persistencia de Value Objects
  - Implementar test shouldPersistAndRetrieveAddressCorrectly en ClinicRepositoryTest.java
  - Implementar test shouldPersistAndRetrieveEmailCorrectly
  - Implementar test shouldPersistAndRetrievePhoneCorrectly
  - Implementar test shouldHandleNullOptionalFields
  - _Requirements: 5.2, 5.3, 5.6_

- [x] 12. Implementar Tests de Repository - Consultas de Unicidad
  - Implementar test shouldReturnTrueWhenEmailExists en ClinicRepositoryTest.java
  - Implementar test shouldReturnFalseWhenEmailDoesNotExist
  - Implementar test shouldReturnTrueWhenLegalNumberExists
  - Implementar test shouldReturnFalseWhenLegalNumberDoesNotExist
  - Implementar test shouldExcludeCurrentClinicWhenCheckingEmailUniqueness
  - Implementar test shouldExcludeCurrentClinicWhenCheckingLegalNumberUniqueness
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 13. Implementar Tests de Repository - Operaciones de Actualización
  - Implementar test shouldUpdateAllFieldsCorrectly en ClinicRepositoryTest.java
  - Implementar test shouldUpdateValueObjectsCorrectly
  - Implementar test shouldMaintainIdAfterUpdate
  - _Requirements: 5.4_

- [x] 14. Implementar Tests de Mapeo entre Capas
  - Crear ClinicEntityMappingTest.java
  - Implementar test shouldMapDomainModelToEntity
  - Implementar test shouldMapEntityToDomainModel
  - Implementar test shouldPreserveAllFieldsDuringMapping
  - Implementar test shouldMapValueObjectsCorrectly
  - Implementar test shouldHandleNullOptionalFieldsInMapping
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 15. Expandir Tests de Controller - Operaciones de Creación
  - Revisar y expandir ClinicControllerIntegrationTest.java existente
  - Implementar test shouldCreateClinicWithValidData (POST /clinic)
  - Implementar test shouldReturnCreatedClinicWithGeneratedId
  - Implementar test shouldReturn400WhenCreatingWithInvalidData
  - Implementar test shouldReturn409WhenEmailAlreadyExists
  - Implementar test shouldReturn409WhenLegalNumberAlreadyExists
  - Implementar test shouldValidateValueObjectsOnCreation
  - _Requirements: 9.1, 9.5, 9.7_

- [x] 16. Expandir Tests de Controller - Operaciones de Actualización
  - Implementar test shouldUpdateClinicWithValidData en ClinicControllerIntegrationTest.java (PUT /clinic/{id})
  - Implementar test shouldReturn404WhenUpdatingNonExistentClinic
  - Implementar test shouldReturn400WhenUpdatingWithInvalidData
  - Implementar test shouldReturn409WhenUpdatingWithDuplicateEmail
  - Implementar test shouldAllowUpdatingWithSameEmail
  - _Requirements: 9.2, 9.5, 9.6, 9.7_

- [x] 17. Expandir Tests de Controller - Operaciones de Consulta y Eliminación
  - Implementar test shouldGetClinicById en ClinicControllerIntegrationTest.java (GET /clinic/{id})
  - Implementar test shouldReturn404WhenClinicNotFound
  - Implementar test shouldReturnAllFieldsIncludingValueObjects
  - Implementar test shouldGetAllClinics (GET /clinic)
  - Implementar test shouldReturnEmptyArrayWhenNoClinics
  - Implementar test shouldDeleteClinicSuccessfully (DELETE /clinic/{id})
  - Implementar test shouldReturn404WhenDeletingNonExistentClinic
  - Implementar test shouldNotBeAbleToGetDeletedClinic
  - _Requirements: 9.3, 9.4, 9.6_
