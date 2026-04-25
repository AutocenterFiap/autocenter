package br.com.autocenterfiap.security.controller.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshToken (@NotBlank String refreshToken){
}
