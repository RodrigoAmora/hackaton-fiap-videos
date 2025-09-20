package br.com.fiap.fiapvideos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Test
    void notifyError_QuandoChamado_DeveEnviarEmailCorretamente() {
        // Arrange
        String userEmail = "usuario@test.com";
        Long videoId = 123L;
        String error = "Erro de processamento";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.notifyError(userEmail, videoId);

        // Assert
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(userEmail, message.getTo()[0]);
        assertEquals("Erro no processamento do vídeo", message.getSubject());
        assertEquals(
                String.format("Ocorreu um erro no processamento do vídeo %d", videoId),
                message.getText()
        );
    }

    @Test
    void notifyError_QuandoEmailNulo_DeveLancarException() {
        // Arrange
        String userEmail = null;
        Long videoId = 123L;
        String error = "Erro de processamento";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifyError(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void notifyError_QuandoVideoIdNulo_DeveLancarException() {
        // Arrange
        String userEmail = "usuario@test.com";
        Long videoId = null;
        String error = "Erro de processamento";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifyError(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void notifyError_QuandoErrorNulo_DeveLancarException() {
        // Arrange
        String userEmail = "usuario@test.com";
        Long videoId = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifyError(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void notifyError_QuandoEmailInvalido_DeveLancarException() {
        // Arrange
        String userEmail = "email-invalido";
        Long videoId = 123L;
        String error = "Erro de processamento";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifyError(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void notifySuccess_QuandoChamado_DeveEnviarEmailCorretamente() {
        // Arrange
        String userEmail = "usuario@test.com";
        Long videoId = 123L;

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.notifySuccess(userEmail, videoId);

        // Assert
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(userEmail, message.getTo()[0]);
        assertEquals("Processamento do vídeo concluído com sucesso", message.getSubject());
        assertEquals(
                String.format("Processamento do vídeo %d concluído com sucesso!", videoId),
                message.getText()
        );
    }

    @Test
    void notifySuccess_QuandoEmailNulo_DeveLancarException() {
        // Arrange
        String userEmail = null;
        Long videoId = 123L;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifySuccess(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void notifySuccess_QuandoVideoIdNulo_DeveLancarException() {
        // Arrange
        String userEmail = "usuario@test.com";
        Long videoId = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifySuccess(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void notifySuccess_QuandoEmailInvalido_DeveLancarException() {
        // Arrange
        String userEmail = "email-invalido";
        Long videoId = 123L;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.notifySuccess(userEmail, videoId)
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

}
