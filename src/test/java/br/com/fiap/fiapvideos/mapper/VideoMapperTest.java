package br.com.fiap.fiapvideos.mapper;

import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoMapperTest {

    private VideoMapper videoMapper;

    @BeforeEach
    void setUp() {
        videoMapper = new VideoMapper();
    }

    @Test
    @DisplayName("Deve mapear Video para VideoStatusResponse com sucesso")
    void deveMapearVideoParaVideoStatusResponse() {
        // Arrange
        Video video = Video.builder()
                .id(1L)
                .ownerId("563565")
                .status(VideoStatus.PENDING)
                .filename("video-teste.mp4")
                .resultZipPath("/caminho/do/arquivo.zip")
                .errorMessage(null)
                .build();

        // Act
        VideoStatusResponse response = videoMapper.mapVideoParaVideoStatusResponse(video);

        // Assert
        assertNotNull(response);
        assertEquals(video.getId(), response.id());
        assertEquals(video.getOwnerId(), response.ownerId());
        assertEquals(video.getStatus().name(), response.status());
        assertEquals(video.getFilename(), response.fileName());
        assertEquals(video.getResultZipPath(), response.resultZipPath());
        assertEquals(video.getErrorMessage(), response.errorMessage());
    }

    @Test
    @DisplayName("Deve mapear Video com erro para VideoStatusResponse")
    void deveMapearVideoComErroParaVideoStatusResponse() {
        // Arrange
        Video video = Video.builder()
                .id(1L)
                .ownerId("563565")
                .status(VideoStatus.FAILED)
                .filename("video-com-erro.mp4")
                .resultZipPath("")
                .errorMessage("Erro no processamento do vídeo")
                .build();

        // Act
        VideoStatusResponse response = videoMapper.mapVideoParaVideoStatusResponse(video);

        // Assert
        assertNotNull(response);
        assertEquals(VideoStatus.FAILED.name(), response.status());
        assertEquals("Erro no processamento do vídeo", response.errorMessage());
    }

    @Test
    @DisplayName("Deve mapear Video processado com sucesso para VideoStatusResponse")
    void deveMapearVideoProcessadoParaVideoStatusResponse() {
        // Arrange
        Video video = Video.builder()
                .id(1L)
                .ownerId("563565")
                .status(VideoStatus.DONE)
                .filename("video-processado.mp4")
                .resultZipPath("/path/to/processed/video.zip")
                .errorMessage(null)
                .build();

        // Act
        VideoStatusResponse response = videoMapper.mapVideoParaVideoStatusResponse(video);

        // Assert
        assertNotNull(response);
        assertEquals(VideoStatus.DONE.name(), response.status());
        assertEquals("/path/to/processed/video.zip", response.resultZipPath());
    }

    @Test
    @DisplayName("Deve lidar com Video nulo e lançar NullPointerException")
    void deveLancarExcecaoQuandoVideoForNulo() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            // Act
            videoMapper.mapVideoParaVideoStatusResponse(null);
        });
    }
}
