package br.com.fiap.fiapvideos.service;

import br.com.fiap.fiapvideos.amqp.config.VideoAMQPConfig;
import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.dto.VideoMessage;
import br.com.fiap.fiapvideos.dto.response.VideoResponse;
import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import br.com.fiap.fiapvideos.exception.VideoNotFoundException;
import br.com.fiap.fiapvideos.exception.VideoNotSavedException;
import br.com.fiap.fiapvideos.mapper.VideoMapper;
import br.com.fiap.fiapvideos.model.Video;
import br.com.fiap.fiapvideos.model.VideoStatus;
import br.com.fiap.fiapvideos.repository.VideoRepository;
import br.com.fiap.fiapvideos.util.VideoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AuthService authService;
    private final VideoUtil videoUtil;
    private final VideoMapper videoMapper;

    public VideoService(VideoRepository videoRepository,
                        RabbitTemplate rabbitTemplate,
                        AuthService authService,
                        VideoUtil videoUtil,
                        VideoMapper videoMapper) {
        this.videoRepository = videoRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.authService = authService;
        this.videoUtil = videoUtil;
        this.videoMapper = videoMapper;
    }

    public VideoResponse enqueueVideo(MultipartFile file) {
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

        video = videoRepository.saveAndFlush(video);
        Long videoId = video.getId();

        boolean videoUploaded = videoUtil.uploadVideo(file, fileName);
        if (videoUploaded) {
            var path = VideoUtil.VIDEO_FILE_INPUT_DIR + "/" + fileName;
            var message = new VideoMessage(videoId, path, prefixFileName, usuarioDTO.email());
            rabbitTemplate.convertAndSend(VideoAMQPConfig.VIDEO_EXCHANGE, VideoAMQPConfig.VIDEO_ROUTING, message);

            return new VideoResponse(videoId, video.getOwnerId(), video.getStatus().name(), video.getFilename(), video.getResultZipPath(), video.getErrorMessage());
        } else {
            throw new VideoNotSavedException("Falha ao salvar arquivo");
        }
    }

    @Cacheable(value = "video_status", key = "#videoId")
    public VideoStatusResponse getStatus(Long videoId) {
        return videoRepository.findById(videoId)
                .map(v -> new VideoStatusResponse(v.getId(), v.getStatus().name()))
                .orElseThrow(() -> new VideoNotFoundException("Video não encontrado"));
    }

    @Cacheable(value = "video_id", key = "'video_id_' + #videoId")
    public VideoResponse buscarVideoPeloId(Long videoId) {
        Video video = buscarVideo(videoId);
        return videoMapper.mapVideoParaVideoResponse(video);
    }

    @Cacheable(value = "videos_user", key = "'videos_user_' + @authService.getUsuarioLogado().id() + '_page_' + #page + '_size_' + #size")
    public Page<VideoResponse> buscarVideosDoUsuario(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new VideoNotFoundException("Parâmetros de paginação inválidos");
        }

        UsuarioDTO usuarioDTO = getUsuarioLogado();
        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        return videoRepository.findByOwnerId(usuarioDTO.id(), pageable).map(videoMapper::mapVideoParaVideoResponse);
    }

    @Caching(evict = {
            @CacheEvict(value = "video_id", key = "#videoId"),
            @CacheEvict(value = "videos_user", key = "#videoId")
    })
    public void removerVideo(Long videoId) {
        Video video = buscarVideo(videoId);
        videoRepository.delete(video);
        videoUtil.deleteVideo(video.getFilename(), video.getResultZipPath());
    }

    private Video buscarVideo(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException("Video não encontrado"));
    }

    private UsuarioDTO getUsuarioLogado() {
        return authService.getUsuarioLogado();
    }
}
