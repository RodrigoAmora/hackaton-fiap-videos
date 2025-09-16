package br.com.fiap.fiapvideos.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class VideoUtil {

    public static String VIDEO_FILE_INPUT_DIR = "/uploads";
    public static String VIDEO_FILE_OUTPUT_DIR = "/outputs";

    public boolean compactVideo(String videoId, String inputPath, String prefixFileName) {
        System.out.println("Worker: processing video " + videoId + " from " + inputPath);

        try {
            Path outputDir = Paths.get(VIDEO_FILE_OUTPUT_DIR);
            createDirectories(outputDir);

            Path input = Paths.get(inputPath);
            Path zip = outputDir.resolve(prefixFileName+"_"+videoId + ".zip");

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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean uploadVideo(MultipartFile file, String fileName) {
        try {
            Path uploadDir = Paths.get(VIDEO_FILE_INPUT_DIR);
            createDirectories(uploadDir);

            // Salva o arquivo diretamente
            Path out = uploadDir.resolve(fileName);
            file.transferTo(out.toFile());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (Exception e) { /* ignore */ }
    }
}
