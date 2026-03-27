package com.datavet.auth.application.port.out;

import com.datavet.auth.domain.model.User;
import com.datavet.shared.application.port.Repository;
import com.datavet.shared.domain.valueobject.Email;

import java.util.Optional;

public interface UserRepositoryPort extends Repository<User, String> {

    Optional<User> findByEmail              (String email);
    boolean        existsByEmail            (String email);
    boolean        existsByEmailAndIdNot    (String email, String userId);
    Optional<User> findByEmailVerificationToken (String token);
}