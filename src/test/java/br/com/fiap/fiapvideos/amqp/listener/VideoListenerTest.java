package br.com.fiap.fiapvideos.amqp.listener;

import br.com.fiap.fiapvideos.dto.VideoMessage;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import br.com.fiap.fiapvideos.repository.VideoRepository;
import br.com.fiap.fiapvideos.service.NotificationService;
import br.com.fiap.fiapvideos.util.VideoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoUtil videoUtil;

    @InjectMocks
    private VideoListener videoListener;

    private Video video;
    private VideoMessage videoMessage;

    @BeforeEach
    void setUp() {
        video = Video.builder()
                .id(1L)
                .filename("test.mp4")
                .status(VideoStatus.PENDING)
                .build();

        videoMessage = new VideoMessage(1L, "/path/to/video", "prefix", "fulano@email.com");
    }

    @Test
    void receive_QuandoProcessamentoFalha_DeveAtualizarStatusParaFailed() {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoUtil.compactVideo(anyString(), anyString(), anyString())).thenReturn(false);
        when(videoRepository.saveAndFlush(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        videoListener.receive(videoMessage);

        // Assert
        verify(videoRepository).findById(1L);
        verify(videoUtil).compactVideo("1", "/path/to/video", "prefix");

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository, times(2)).saveAndFlush(videoCaptor.capture());

        Video processedVideo = videoCaptor.getAllValues().get(1);
        assertAll(
                () -> assertEquals(VideoStatus.FAILED, processedVideo.getStatus()),
                () -> assertEquals("Erro ao processar o vídeo", processedVideo.getErrorMessage()),
                () -> assertNull(processedVideo.getResultZipPath())
        );
    }

    @Test
    void receive_QuandoProcessamentoSucesso_DeveAtualizarStatusParaDone() {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoUtil.compactVideo("1", "/path/to/video", "prefix")).thenReturn(true);
        when(videoRepository.saveAndFlush(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        videoListener.receive(videoMessage);

        // Assert
        verify(videoRepository).findById(1L);
        verify(videoUtil).compactVideo("1", "/path/to/video", "prefix");
        verify(notificationService).notifySuccess("fulano@email.com", 1L);

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository, times(2)).saveAndFlush(videoCaptor.capture());

        Video processedVideo = videoCaptor.getAllValues().get(1);
        assertAll(
                () -> assertEquals(VideoStatus.DONE, processedVideo.getStatus()),
                () -> assertEquals(VideoUtil.VIDEO_FILE_OUTPUT_DIR + "/1.zip", processedVideo.getResultZipPath()),
                () -> assertNull(processedVideo.getErrorMessage())
        );
    }

    @Test
    void updatedStatusVideo_QuandoVideoExiste_DeveAtualizarStatus() {
        // Arrange
        video.setStatus(VideoStatus.PROCESSING);
        when(videoRepository.saveAndFlush(any(Video.class))).thenReturn(video);

        // Act
        videoListener.updatedStatusVideo(video);

        // Assert
        verify(videoRepository).saveAndFlush(video);
        assertEquals(VideoStatus.PROCESSING, video.getStatus());
    }

    @Test
    void updatedStatusVideo_QuandoVideoNaoExiste_DeveLancarException() {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.empty());
        video.setStatus(VideoStatus.PROCESSING);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> videoListener.receive(videoMessage)
        );
        assertEquals("Video not found", exception.getMessage());

        verify(videoRepository).findById(1L);
        verify(videoRepository, never()).saveAndFlush(any(Video.class));
        verify(videoUtil, never()).compactVideo(anyString(), anyString(), anyString());
    }

}
