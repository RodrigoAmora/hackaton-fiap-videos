package br.com.fiap.fiapvideos.dto.request;

public record LoginRequest(
        String email,
        String senha
) {}
