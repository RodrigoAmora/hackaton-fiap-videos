package br.com.fiap.fiapvideos.amqp.listener;

import br.com.fiap.fiapvideos.dto.VideoMessage;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import br.com.fiap.fiapvideos.repository.VideoRepository;
import br.com.fiap.fiapvideos.service.NotificationService;
import br.com.fiap.fiapvideos.util.VideoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoListener {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoUtil videoUtil;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RabbitListener(queues = "video.process.queue")
    public void receive(VideoMessage message) {
        String processKey = "video_process:" + message.videoId();

        if (redisTemplate.opsForValue().setIfAbsent(processKey, "processing", 1, TimeUnit.HOURS)) {
            Long videoId = message.videoId();
            String userEmail = message.userEmail();

            Video video = findVideoById(videoId);
            video.setStatus(VideoStatus.PROCESSING);

            updatedStatusVideo(video);

            // Processa o vídeo
            boolean success = processVideo(String.valueOf(videoId), message.path(), message.prefixFileName());
            System.out.println("Result ---> " + success);
            if (success) {
                var resultZipPath = VideoUtil.VIDEO_FILE_OUTPUT_DIR + "/" + videoId + ".zip";
                video.setResultZipPath(resultZipPath);
                video.setStatus(VideoStatus.DONE);
                updatedStatusVideo(video);

                System.out.println("Worker: updated status to DONE for video " + videoId);

                notificationService.notifySuccess(userEmail, videoId);
            } else {
                video.setErrorMessage("Erro ao processar o vídeo");
                video.setStatus(VideoStatus.FAILED);
                updatedStatusVideo(video);

                System.out.println("Worker: updated status to FAILED for video " + videoId);

                notificationService.notifyError(userEmail, videoId);
            }
        } else {
            log.warn("Mensagem duplicada recebida para o vídeo: {}", message.videoId());
        }
    }

    private boolean processVideo(String videoId, String inputPath, String prefixFileName) {
        return videoUtil.compactVideo(videoId, inputPath, prefixFileName);
    }

    private Video findVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                              .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    @Transactional
    public void updatedStatusVideo(Video video) {
        videoRepository.saveAndFlush(video);
    }
}
