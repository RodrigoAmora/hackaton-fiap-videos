package br.com.fiap.fiapvideos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuarioDTO(
        @JsonProperty("id")
        String id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("email")
        String email,

        @JsonProperty("cpf")
        String cpf,

        @JsonProperty("data_nascimento")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,

        @JsonProperty("data_cadastro")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dataCadastro,

        @JsonProperty("role")
        Role role
) {}
