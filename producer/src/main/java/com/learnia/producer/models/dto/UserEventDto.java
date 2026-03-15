package com.learnia.producer.models.dto;

import java.util.UUID;

import com.learnia.producer.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEventDto {

    private UUID uuid;
    private UUID uuidRequest;
    private String description;
    private String createdAt;

    public static UserEventDto from(User user) {
        return new UserEventDto(
            user.getUuid(),
            user.getUuidRequest(),
            user.getDescription(),
            user.getCreatedAt() != null ? user.getCreatedAt().toString() : null
        );
    }
}
