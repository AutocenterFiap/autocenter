package br.com.autocenterfiap.security.adapter.in.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshToken (@NotBlank String refreshToken){
}
