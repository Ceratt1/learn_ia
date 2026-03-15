package com.learnia.producer.models;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.learnia.models.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User extends BaseEntity {
    private UUID uuidRequest;
    private String description;


    public static User toDomain(UUID uuidUser, UUID uuidRequest ,String description) {
        User user = new User();
        user.setUuid(uuidRequest);
        user.setUuidRequest(uuidRequest);
        user.setDescription(description);
        user.setCreatedAt(OffsetDateTime.now());
        return user;
    }

}
