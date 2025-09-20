package br.com.fiap.fiapvideos.dto;

public record VideoMessage(
        Long videoId,
        String path,
        String prefixFileName,
        String userEmail
) {}
