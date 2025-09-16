package br.com.fiap.fiapvideos.service;

import br.com.fiap.fiapvideos.api.dto.ERole;
import br.com.fiap.fiapvideos.api.dto.Role;
import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.dto.VideoMessage;
import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import br.com.fiap.fiapvideos.exception.VideoException;
import br.com.fiap.fiapvideos.mapper.VideoMapper;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import br.com.fiap.fiapvideos.repository.VideoRepository;
import br.com.fiap.fiapvideos.util.VideoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AuthService authService;

    @Mock
    private VideoUtil videoUtil;

    @Mock
    private VideoMapper videoMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VideoService videoService;

    private UsuarioDTO usuarioMock;
    private Video videoMock;
    private MultipartFile fileMock;

    @BeforeEach
    void setUp() {
        ERole eRole = ERole.ROLE_USER;
        Role role = new Role();
        role.setName(eRole);
        usuarioMock = new UsuarioDTO("123", "Fulano Silva", "usuario@test.com", "11122233344", LocalDate.now(), LocalDateTime.now(), role);

        videoMock = Video.builder()
                .id(1L)
                .filename("test.mp4")
                .ownerId("123")
                .status(VideoStatus.PENDING)
                .build();

        fileMock = new MockMultipartFile(
                "video",
                "test.mp4",
                "video/mp4",
                "test video content".getBytes()
        );
    }

    @Test
    void enqueueVideo_QuandoSucesso_DeveRetornarVideoStatusResponse() {
        // Arrange
        when(authService.getUsuarioLogado()).thenReturn(usuarioMock);
        when(videoRepository.saveAndFlush(any(Video.class))).thenReturn(videoMock);
        when(videoUtil.uploadVideo(any(), any())).thenReturn(true);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(VideoMessage.class));

        // Act
        VideoStatusResponse response = videoService.enqueueVideo(fileMock);

        // Assert
        assertNotNull(response);
        assertEquals(videoMock.getId(), response.id());
        assertEquals(VideoStatus.PENDING.name(), response.status());
        verify(videoRepository, times(1)).saveAndFlush(any(Video.class));
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(VideoMessage.class));
    }

    @Test
    void enqueueVideo_QuandoArquivoVazio_DeveLancarException() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "video",
                "empty.mp4",
                "video/mp4",
                new byte[0]
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> videoService.enqueueVideo(emptyFile),
                "Arquivo vazio");
    }

    @Test
    void enqueueVideo_QuandoFalhaUpload_DeveLancarVideoException() {
        // Arrange
        when(authService.getUsuarioLogado()).thenReturn(usuarioMock);
        when(videoRepository.saveAndFlush(any(Video.class))).thenReturn(videoMock);
        when(videoUtil.uploadVideo(any(), any())).thenReturn(false);

        // Act & Assert
        assertThrows(VideoException.class,
                () -> videoService.enqueueVideo(fileMock),
                "Falha ao salvar arquivo");

        verify(videoRepository, times(1)).save(any(Video.class)); // Salva inicial e atualização do erro
    }

    @Test
    void getStatus_QuandoVideoExiste_DeveRetornarVideoStatusResponse() {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.of(videoMock));

        // Act
        VideoStatusResponse response = videoService.getStatus(1L);

        // Assert
        assertNotNull(response);
        assertEquals(videoMock.getStatus().name(), response.status());
        verify(videoRepository, times(1)).findById(1L);
    }

    @Test
    void getStatus_QuandoVideoNaoExiste_DeveLancarException() {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> videoService.getStatus(1L),
                "Video not found");
    }

    @Test
    void buscarVideosDoUsuario_QuandoExistemVideos_DeveRetornarPaginaComVideos() {
        // Arrange
        String usuarioId = "123";
        int page = 0;
        int size = 10;

        List<Video> videos = List.of(
                Video.builder()
                        .id(1L)
                        .ownerId(usuarioId)
                        .status(VideoStatus.PENDING)
                        .build(),
                Video.builder()
                        .id(2L)
                        .ownerId(usuarioId)
                        .status(VideoStatus.DONE)
                        .build()
        );

        Page<Video> videoPage = new PageImpl<>(videos);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");

        when(authService.getUsuarioLogado()).thenReturn(usuarioMock);
        when(videoRepository.findByOwnerId(usuarioId, pageRequest)).thenReturn(videoPage);
        when(videoMapper.mapVideoParaVideoStatusResponse(any(Video.class)))
                .thenReturn(new VideoStatusResponse(1L, "1111", "PENDING", "aaaaa", "", null))
                .thenReturn(new VideoStatusResponse(2L, "1111", "COMPLETED", "eeeee", "", null));

        // Act
        Page<VideoStatusResponse> result = videoService.buscarVideosDoUsuario(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id());
        assertEquals("PENDING", result.getContent().get(0).status());
        assertEquals(2L, result.getContent().get(1).id());
        assertEquals("COMPLETED", result.getContent().get(1).status());
        verify(videoRepository).findByOwnerId(eq(usuarioId), any(PageRequest.class));
        verify(videoMapper, times(2)).mapVideoParaVideoStatusResponse(any(Video.class));
    }

    @Test
    void buscarVideosDoUsuario_QuandoNaoExistemVideos_DeveRetornarPaginaVazia() {
        // Arrange
        String usuarioId = "123";
        int page = 0;
        int size = 10;

        Page<Video> emptyPage = new PageImpl<>(Collections.emptyList());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");

        when(authService.getUsuarioLogado()).thenReturn(usuarioMock);
        when(videoRepository.findByOwnerId(usuarioId, pageRequest)).thenReturn(emptyPage);

        // Act
        Page<VideoStatusResponse> result = videoService.buscarVideosDoUsuario(page, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(videoRepository).findByOwnerId(eq(usuarioId), any(PageRequest.class));
        verify(videoMapper, never()).mapVideoParaVideoStatusResponse(any(Video.class));
    }

    @Test
    void buscarVideosDoUsuario_QuandoPaginacaoInvalida_DeveLancarException() {
        // Arrange
        String usuarioId = "123";
        int invalidPage = -1;
        int invalidSize = -1;

        // Act & Assert
        assertThrows(VideoException.class, () ->
                videoService.buscarVideosDoUsuario(invalidPage, invalidSize)
        );

        verify(videoRepository, never()).findByOwnerId(anyString(), any(PageRequest.class));
    }

}
