package com.datavet.auth.infrastructure.adapter.output;

import com.datavet.auth.application.port.out.UserRepositoryPort;
import com.datavet.auth.domain.model.User;
import com.datavet.auth.domain.valueobject.HashedPassword;
import com.datavet.auth.infrastructure.persistence.document.UserDocument;
import com.datavet.auth.infrastructure.persistence.repository.MongoUserRepository;
import com.datavet.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final MongoUserRepository repository;

    // -------------------------------------------------------------------------
    // Mappers internos
    // -------------------------------------------------------------------------

    private UserDocument toDocument(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .clinicId(user.getClinicId())
                .email(user.getEmail().getValue())
                .passwordHash(user.getPassword().getValue())
                .role(user.getRole())
                .status(user.getStatus())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailVerificationToken(user.getEmailVerificationToken())
                .emailVerificationExpiry(user.getEmailVerificationExpiry())
                .build();
    }

    private User toDomain(UserDocument doc) {
        return User.reconstitute(
                doc.getId(),
                doc.getEmployeeId(),
                doc.getClinicId(),
                new Email(doc.getEmail()),
                HashedPassword.ofHash(doc.getPasswordHash()),
                doc.getRole(),
                doc.getStatus(),
                doc.getFirstName(),
                doc.getLastName(),
                doc.getEmailVerificationToken(),
                doc.getEmailVerificationExpiry(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    // -------------------------------------------------------------------------
    // Repository base
    // -------------------------------------------------------------------------

    @Override
    public User save(User user) {
        return toDomain(repository.save(toDocument(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        throw new UnsupportedOperationException(
                "User no soporta hard delete. Usa deactivateUser()");
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    // -------------------------------------------------------------------------
    // Domain-specific
    // -------------------------------------------------------------------------

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, String userId) {
        return repository.existsByEmailAndIdNot(email, userId);
    }

    @Override
    public Optional<User> findByEmailVerificationToken(String token) {
        return repository.findByEmailVerificationToken(token).map(this::toDomain);
    }
}