package br.com.fiap.fiapvideos.amqp;

import br.com.fiap.fiapvideos.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@RequiredArgsConstructor
public class VideoListener {

    @RabbitListener(queues = "video.process.queue")
    public void receive(VideoService.VideoMessage message) {
        try {
            processVideo(""+message.videoId(), message.path());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processVideo(String videoId, String inputPath) {
        System.out.println("Worker: processing video " + videoId + " from " + inputPath);
        try {
            Files.createDirectories(Paths.get("/tmp/outputs"));
        } catch (Exception e) { /* ignore */ }
        try {
            Path input = Paths.get(inputPath);
            Path outputDir = Paths.get("/tmp/outputs");
            Files.createDirectories(outputDir);
            Path zip = outputDir.resolve(videoId + ".zip");

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

            System.out.println("Worker: created zip at " + zip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
