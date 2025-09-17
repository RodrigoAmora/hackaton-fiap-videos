package br.com.fiap.fiapvideos.mapper;

import br.com.fiap.fiapvideos.dto.response.VideoResponse;
import br.com.fiap.fiapvideos.model.Video;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    public VideoResponse mapVideoParaVideoResponse(Video video) {
        return new VideoResponse(
                video.getId(),
                video.getOwnerId(),
                video.getStatus().name(),
                video.getFilename(),
                video.getResultZipPath(),
                video.getErrorMessage()
        );
    }

}
