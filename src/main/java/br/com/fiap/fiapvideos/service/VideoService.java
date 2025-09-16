package br.com.fiap.fiapvideos.service;

import br.com.fiap.fiapvideos.amqp.config.VideoAMQPConfiguration;
import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.dto.VideoMessage;
import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import br.com.fiap.fiapvideos.exception.VideoException;
import br.com.fiap.fiapvideos.mapper.VideoMapper;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import br.com.fiap.fiapvideos.repository.VideoRepository;
import br.com.fiap.fiapvideos.util.VideoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class VideoService {

    private final VideoRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final AuthService authService;
    private final VideoUtil videoUtil;
    private final VideoMapper videoMapper;
    private final NotificationService notificationService;

    public VideoService(VideoRepository repository,
                        RabbitTemplate rabbitTemplate,
                        AuthService authService,
                        VideoUtil videoUtil,
                        VideoMapper videoMapper,
                        NotificationService notificationService) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.authService = authService;
        this.videoUtil = videoUtil;
        this.videoMapper = videoMapper;
        this.notificationService = notificationService;
    }

    @CachePut(value = "video_id", key = "#result")
    public VideoStatusResponse enqueueVideo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        UsuarioDTO usuarioDTO = getUsuarioLogado();

        // Gera um nome único para o arquivo
        String prefixFileName = UUID.randomUUID() + "__" + usuarioDTO.id();
        String fileName = prefixFileName+"-"+file.getOriginalFilename();

        Video video = Video.builder()
                .filename(fileName)
                .ownerId(usuarioDTO.id())
                .status(VideoStatus.PENDING)
                .errorMessage(null)
                .resultZipPath("")
                .build();

        video = repository.saveAndFlush(video);
        Long videoId = video.getId();

        boolean videoUploaded = videoUtil.uploadVideo(file, fileName);
        if (videoUploaded) {
            var path = VideoUtil.VIDEO_FILE_INPUT_DIR + "/" + fileName;
            var message = new VideoMessage(videoId, path, prefixFileName);
            rabbitTemplate.convertAndSend(VideoAMQPConfiguration.VIDEO_EXCHANGE, VideoAMQPConfiguration.VIDEO_ROUTING, message);

            return new VideoStatusResponse(videoId, video.getOwnerId(), video.getStatus().name(), video.getFilename(), video.getResultZipPath(), video.getErrorMessage());
        } else {
            String errorMessage = "Falha ao salvar arquivo";

            video.setStatus(VideoStatus.FAILED);
            video.setErrorMessage(errorMessage);
            repository.save(video);

            notificationService.notifyError(usuarioDTO.email(), videoId, errorMessage);
            throw new VideoException("Falha ao salvar arquivo");
        }
    }

    @Cacheable(value = "video_status", key = "#videoId")
    public VideoStatusResponse getStatus(Long videoId) {
        return repository.findById(videoId)
                .map(v -> new VideoStatusResponse(v.getId(), v.getOwnerId(), v.getStatus().name(), v.getFilename(), v.getResultZipPath(), v.getErrorMessage()))
                .orElseThrow(() -> new VideoException("Video não encontrado"));
    }

    public VideoStatusResponse buscarVideoPeloId(Long videoId) {
        Video video = repository.findById(videoId).orElseThrow(() -> new VideoException("Video não encontrado"));
        return videoMapper.mapVideoParaVideoStatusResponse(video);
    }

    public Page<VideoStatusResponse> buscarVideosDoUsuario(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new VideoException("Parâmetros de paginação inválidos");
        }

        UsuarioDTO usuarioDTO = getUsuarioLogado();
        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        return repository.findByOwnerId(usuarioDTO.id(), pageable).map(videoMapper::mapVideoParaVideoStatusResponse);
    }

    private UsuarioDTO getUsuarioLogado() {
        return authService.getUsuarioLogado();
    }
}
