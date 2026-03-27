package com.datavet.auth.infrastructure.persistence.document;

import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.domain.model.UserStatus;
import com.datavet.shared.domain.valueobject.Email;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@CompoundIndexes({
        @CompoundIndex(name = "email_idx",    def = "{'email.value': 1}", unique = true),
        @CompoundIndex(name = "clinic_idx",   def = "{'clinic_id': 1}"),
        @CompoundIndex(name = "employee_idx", def = "{'employee_id': 1}"),
        @CompoundIndex(name = "status_idx",   def = "{'status': 1}"),
        @CompoundIndex(
                name   = "verification_token_idx",
                def    = "{'email_verification_token': 1}",
                sparse = true
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {

    @Id
    private String id;

    @Field("employee_id")
    private String employeeId;

    @Field("clinic_id")
    private String clinicId;

    private String email;

    @Field("password_hash")
    private String passwordHash;

    private UserRole   role;
    private UserStatus status;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("email_verification_token")
    private String emailVerificationToken;

    @Field("email_verification_expiry")
    private LocalDateTime emailVerificationExpiry;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}