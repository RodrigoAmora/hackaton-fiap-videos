package br.com.fiap.fiapvideos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void notifyError(String userEmail, Long videoId) {
        var subject = "Erro no processamento do vídeo";
        var text = String.format("Ocorreu um erro no processamento do vídeo %d", videoId);

        sendEmail(userEmail, videoId, subject, text);
    }

    public void notifySuccess(String userEmail, Long videoId) {
        var subject = "Processamento do vídeo concluído com sucesso";
        var text = String.format("Processamento do vídeo %d concluído com sucesso!", videoId);

        sendEmail(userEmail, videoId, subject, text);
    }

    private void sendEmail(String userEmail, Long videoId, String subject, String text) {
        if (userEmail == null || videoId == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }

        if (!isValidEmail(userEmail)) {
            throw new IllegalArgumentException("Email inválido");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

}
