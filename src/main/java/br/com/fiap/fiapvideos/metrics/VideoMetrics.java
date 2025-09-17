package br.com.fiap.fiapvideos.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class VideoMetrics {

    private Counter uploadsVideoSuccess, uploadsVideoError, uploadsVideoTotal;
    private Counter videoCompressionsSuccess, videoCompressionsError, videoCompressionsTotal;

    public VideoMetrics(MeterRegistry registry) {
        this.registerUploadMetrics(registry);
        this.registerCompressionMetrics(registry);
    }

    // Uploads video
    private void registerUploadMetrics(MeterRegistry registry) {
        this.uploadsVideoSuccess = Counter.builder("uploads_video_success")
                .description("Uploads video successfully")
                .register(registry);

        this.uploadsVideoError = Counter.builder("uploads_video_error")
                .description("Uploads video with error")
                .register(registry);

        this.uploadsVideoTotal = Counter.builder("uploads_video_total")
                .description("Total of uploads video")
                .register(registry);
    }

    public void incrementUploadsVideoSuccess() {
        this.uploadsVideoSuccess.increment();
    }

    public void incrementUploadsVideoError() {
        this.uploadsVideoError.increment();
    }

    public void incrementUploadsVideoTotal() {
        this.uploadsVideoTotal.increment();
    }

    // Compressions of videos
    private void registerCompressionMetrics(MeterRegistry registry) {
        this.videoCompressionsSuccess = Counter.builder("compressions_video_success")
                .description("Compressions video successfully")
                .register(registry);

        this.videoCompressionsError = Counter.builder("compressions_video_error")
                .description("Compressions video with error")
                .register(registry);

        this.videoCompressionsTotal = Counter.builder("compressions_video_total")
                .description("Total of compressions video")
                .register(registry);
    }

    public void incrementVideoCompressionsSuccess() {
        this.videoCompressionsSuccess.increment();
    }

    public void incrementVideoCompressionsError() {
        this.videoCompressionsError.increment();
    }

    public void incrementVideoCompressionsTotal() {
        this.videoCompressionsTotal.increment();
    }

}
