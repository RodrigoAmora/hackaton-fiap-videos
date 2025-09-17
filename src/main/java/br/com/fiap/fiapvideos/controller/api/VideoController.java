package br.com.fiap.fiapvideos.controller.api;

import br.com.fiap.fiapvideos.controller.api.doc.VideoControllerDoc;
import br.com.fiap.fiapvideos.dto.response.VideoResponse;
import br.com.fiap.fiapvideos.dto.response.VideoStatusResponse;
import br.com.fiap.fiapvideos.service.VideoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController implements VideoControllerDoc {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<VideoResponse> upload(@RequestParam("file") MultipartFile file) {
        VideoResponse videoResponse = videoService.enqueueVideo(file);
        return ResponseEntity.accepted().body(videoResponse);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<VideoStatusResponse> status(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getStatus(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> buscarVideoPeloId(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.buscarVideoPeloId(id));
    }

    @GetMapping("/meusVideos")
    public ResponseEntity<Page<VideoResponse>> buscarVideosDoUsuario(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return ResponseEntity.ok(videoService.buscarVideosDoUsuario(page, size));
    }
}
