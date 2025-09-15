package br.com.fiap.fiapvideos.dto.request;

public record VideoUploadRequest(
        String filename,
        String base64Content
) {}
