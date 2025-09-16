package br.com.fiap.fiapvideos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void notifyError(String userEmail, Long videoId, String error) {
        if (userEmail == null || videoId == null || error == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!userEmail.matches(emailRegex)) {
            throw new IllegalArgumentException("Email inválido");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Erro no processamento do vídeo");
        message.setText(String.format("Ocorreu um erro no processamento do vídeo %d: %s", videoId, error));
        mailSender.send(message);

    }

}
