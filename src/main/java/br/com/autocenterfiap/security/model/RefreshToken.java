package br.com.autocenterfiap.security.model;

import jakarta.validation.constraints.NotBlank;

public record RefreshToken (@NotBlank String refreshToken){
}
