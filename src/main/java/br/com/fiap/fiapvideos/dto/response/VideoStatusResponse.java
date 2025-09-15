package br.com.fiap.fiapvideos.dto.response;

public record VideoStatusResponse(
        Long id,
        String status,
        String resultZipPath,
        String errorMessage
) {}
