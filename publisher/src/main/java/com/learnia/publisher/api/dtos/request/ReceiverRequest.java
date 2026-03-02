package com.learnia.publisher.api.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReceiverRequest(
        @NotNull(message = "uuidUser is required")
        UUID uuidUser,
        @Size(max = 200, message = "description must have at most 200 characters")
        String description) {

    public static final long MAX_PDF_SIZE_BYTES = 100L * 1024 * 1024;
}
