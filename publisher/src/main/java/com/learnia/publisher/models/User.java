package com.learnia.publisher.models;

import java.util.UUID;

import com.learnia.models.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseEntity {
    private UUID uuidRequest;
    private String description;


    public static User toDomain(UUID uuidUser, UUID uuidRequest ,String description) {
        User user = new User();
        user.setUuid(uuidRequest);
        user.setUuidRequest(uuidRequest);
        user.setDescription(description);
        return user;
    }

}
