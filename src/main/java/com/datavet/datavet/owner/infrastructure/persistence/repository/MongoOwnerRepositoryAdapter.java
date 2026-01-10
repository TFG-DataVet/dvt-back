package com.datavet.datavet.owner.infrastructure.persistence.repository;

import com.datavet.datavet.owner.infrastructure.persistence.document.OwnerDocument;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository interface for Owner persistence operations.
 * Extends MongoRepository to provide CRUD operations and custom query methods.
 */
public interface MongoOwnerRepositoryAdapter extends MongoRepository<OwnerDocument, String> {

    /**
     * Checks if an owner exists with the given email.
     *
     * @param email the email to check
     * @return true if an owner with the email exists, false otherwise
     */
    boolean existsByEmail(Email email);

    /**
     * Checks if an owner exists with the given DNI.
     *
     * @param dni the DNI to check
     * @return true if an owner with the DNI exists, false otherwise
     */
    boolean existsByDni(String dni);

    /**
     * Checks if an owner exists with the given email and DNI.
     *
     * @param email the email to check
     * @param dni the DNI to check
     * @return true if an owner with both email and DNI exists, false otherwise
     */
    boolean existsByEmailAndDni(Email email, String dni);

    /**
     * Checks if an owner exists with the given phone.
     *
     * @param phone the phone to check
     * @return true if an owner with the phone exists, false otherwise
     */
    boolean existsByPhone(Phone phone);

    /**
     * Checks if an owner exists with the given DNI excluding a specific owner ID.
     * Useful for update operations to check uniqueness while excluding the current owner.
     *
     * @param dni the DNI to check
     * @param id the owner ID to exclude from the check
     * @return true if another owner with the DNI exists, false otherwise
     */
    boolean existsByDniAndIdNot(String dni, String id);
}
