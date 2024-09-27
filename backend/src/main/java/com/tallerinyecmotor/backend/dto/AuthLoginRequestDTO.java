package com.tallerinyecmotor.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;

//el record nos sirve para crear un DTO sencillo
public record AuthLoginRequestDTO(
        @NotBlank
        String username,
        @NotBlank
        String password){

}
