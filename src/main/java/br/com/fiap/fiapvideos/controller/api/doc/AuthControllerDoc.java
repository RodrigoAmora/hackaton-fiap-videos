package br.com.fiap.fiapvideos.controller.api.doc;

import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.dto.request.LoginRequest;
import br.com.fiap.fiapvideos.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Endpoints de Autenticação")
public interface AuthControllerDoc {

    @Operation(summary = "Autenticação Usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro de Usuário.", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
    })
    ResponseEntity<LoginResponse> login(LoginRequest request);

    @Operation(summary = "Recuperar Usuário Logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recuperar as informações do usuário logado.", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
    })
    ResponseEntity<UsuarioDTO> getUsuarioLogado();

}
