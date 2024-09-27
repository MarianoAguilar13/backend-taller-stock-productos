package com.tallerinyecmotor.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username","messagge","jwt","status"})
public record AuthResponseDTO(
        String username,
        String message,
        String jwt,
        boolean status
) {
}
