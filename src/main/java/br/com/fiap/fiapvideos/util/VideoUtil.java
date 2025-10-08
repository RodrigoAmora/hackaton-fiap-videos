package br.com.fiap.fiapvideos.util;

import br.com.fiap.fiapvideos.metrics.VideoMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
public class VideoUtil {

    public static String VIDEO_FILE_INPUT_DIR = "/uploads";
    public static String VIDEO_FILE_OUTPUT_DIR = "/outputs";

    @Autowired
    private VideoMetrics videoMetrics;

    public boolean compactVideo(String videoId, String inputPath, String fileName) {
        log.info("Worker: processing video {} from {}", videoId, inputPath);

        try {
            Path outputDir = Paths.get(VIDEO_FILE_OUTPUT_DIR);
            createDirectories(outputDir);

            Path input = Paths.get(inputPath);
            Path zip = outputDir.resolve(fileName);

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip))) {
                ZipEntry entry = new ZipEntry(input.getFileName().toString());
                zos.putNextEntry(entry);
                Files.copy(input, zos);
                zos.closeEntry();

                ZipEntry txt = new ZipEntry("frame-info.txt");
                zos.putNextEntry(txt);
                String info = "Processed video: " + videoId + "\noriginalPath: " + inputPath;
                zos.write(info.getBytes());
                zos.closeEntry();
            }

            log.info("Worker: created zip at {}", fileName);

            videoMetrics.incrementVideoCompressionsSuccess();

            return true;
        } catch (Exception e) {
            videoMetrics.incrementVideoCompressionsError();
            log.error("Video {} compression failed", fileName);
            return false;
        } finally {
            videoMetrics.incrementVideoCompressionsTotal();
        }
    }

    public boolean uploadVideo(MultipartFile file, String fileName) {
        try {
            Path uploadDir = Paths.get(VIDEO_FILE_INPUT_DIR);
            createDirectories(uploadDir);

            // Salva o arquivo diretamente
            Path out = uploadDir.resolve(fileName);
            file.transferTo(out.toFile());

            videoMetrics.incrementUploadsVideoSuccess();
            log.info("Video {} uploaded successfully", fileName);

            return true;
        } catch (Exception e) {
            videoMetrics.incrementUploadsVideoError();
            log.error("Video {} upload failed", fileName);
            return false;
        } finally {
            videoMetrics.incrementUploadsVideoTotal();
        }
    }

    public void deleteVideo(String fileName, String zipFileName) {
        try {
            Path uploadDir = Paths.get(VIDEO_FILE_INPUT_DIR);
            Path pathVideo = uploadDir.resolve(fileName);
            Files.deleteIfExists(pathVideo);

            Path zipDir = Paths.get(VIDEO_FILE_OUTPUT_DIR);
            Path pathZip = zipDir.resolve(zipFileName);
            Files.deleteIfExists(pathZip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createDirectories(Path path) throws IOException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("Erro ao criar diretório: {}", path);
            throw e;
        }
    }
}
