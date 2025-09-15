package br.com.fiap.fiapvideos.dto.response;

import br.com.fiap.fiapvideos.api.dto.Role;

public record LoginResponse(
        String token,
        String tipo,
        String nome,
        String email,
        Role role
) {}
