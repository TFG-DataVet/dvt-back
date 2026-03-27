package com.datavet.auth.infrastructure.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "refresh_tokens")
@CompoundIndexes({
        @CompoundIndex(name = "token_hash_idx", def = "{'token_hash': 1}", unique = true),
        @CompoundIndex(name = "user_id_idx",    def = "{'user_id': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDocument {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("token_hash")
    private String tokenHash;

    @Field("expires_at")
    private LocalDateTime expiresAt;

    @Field("created_at")
    private LocalDateTime createdAt;
}