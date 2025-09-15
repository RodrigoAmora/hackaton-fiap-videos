package br.com.fiap.fiapvideos.amqp.listener;

import br.com.fiap.fiapvideos.dto.VideoMessage;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import br.com.fiap.fiapvideos.repository.VideoRepository;
import br.com.fiap.fiapvideos.util.VideoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VideoListener {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoUtil videoUtil;

    @RabbitListener(queues = "video.process.queue")
    public void receive(VideoMessage message) {
        Long videoId = message.videoId();

        updatedStatusVideo(videoId, VideoStatus.PROCESSING);

        // Processa o vídeo
        boolean success = processVideo(String.valueOf(videoId), message.path());
        System.out.println("Result ---> " + success);

        // Atualiza status final
        VideoStatus videoStatus = success ? VideoStatus.DONE : VideoStatus.FAILED;
        updatedStatusVideo(videoId, videoStatus);
    }

    private boolean processVideo(String videoId, String inputPath) {
        return videoUtil.compactVideo(videoId, inputPath);
    }

    @Transactional
    public void updatedStatusVideo(Long videoId, VideoStatus videoStatus) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        video.setStatus(videoStatus);
        if (videoStatus.equals(VideoStatus.DONE)) {
            video.setResultZipPath("/tmp/outputs/" + videoId + ".zip");
            System.out.println("Worker: updated status to DONE for video " + videoId);
        }
        if (videoStatus.equals(VideoStatus.FAILED)) {
            video.setErrorMessage("Erro ao processar o vídeo");
            System.out.println("Worker: updated status to FAILED for video " + videoId);
        }

        videoRepository.saveAndFlush(video);
    }
}
