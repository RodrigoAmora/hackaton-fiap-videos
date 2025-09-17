package br.com.fiap.fiapvideos.controller.api.doc;

import br.com.fiap.fiapvideos.dto.response.VideoResponse;
import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Endpoints de Vídeo")
public interface VideoControllerDoc {

    @Operation(summary = "Upload de vídeo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload de vídeos", content = @Content(schema = @Schema(implementation = VideoResponse.class))),
    })
    ResponseEntity<VideoResponse> upload(MultipartFile file);

    @Operation(summary = "Consulta de status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Concultar o status do vídeo pelo", content = @Content(schema = @Schema(implementation = VideoStatusResponse.class))),
    })
    ResponseEntity<VideoStatusResponse> status(Long id);

    @Operation(summary = "Buscar de vídeos do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buscar de vídeos do usuário pelo id do usuário", content = @Content(schema = @Schema(implementation = VideoResponse.class))),
    })
    ResponseEntity<VideoResponse> buscarVideoPeloId(Long id);

    @Operation(summary = "Buscar de vídeo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buscar de vídeo pelo id", content = @Content(schema = @Schema(implementation = VideoResponse.class))),
    })
    ResponseEntity<Page<VideoResponse>> buscarVideosDoUsuario(int page, int size);

    @Operation(summary = "Remover vídeo")
    HttpStatus removerVideo(@PathVariable Long id);

}
