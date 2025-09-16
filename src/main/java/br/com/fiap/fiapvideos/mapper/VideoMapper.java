package br.com.fiap.fiapvideos.mapper;

import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import br.com.fiap.fiapvideos.model.Video;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    public VideoStatusResponse mapVideoParaVideoStatusResponse(Video video) {
        return new VideoStatusResponse(
                video.getId(),
                video.getOwnerId(),
                video.getStatus().name(),
                video.getFilename(),
                video.getResultZipPath(),
                video.getErrorMessage()
        );
    }

}
