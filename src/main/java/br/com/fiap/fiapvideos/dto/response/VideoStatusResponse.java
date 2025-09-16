package br.com.fiap.fiapvideos.dto.response;

public record VideoStatusResponse(
        Long id,
        String ownerId,
        String status,
        String fileName,
        String resultZipPath,
        String errorMessage
) {}
