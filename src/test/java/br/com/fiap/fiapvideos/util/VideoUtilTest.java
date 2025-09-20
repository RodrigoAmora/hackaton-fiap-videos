package br.com.fiap.fiapvideos.util;

import br.com.fiap.fiapvideos.metrics.VideoMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoUtilTest {

    @Mock
    private VideoMetrics videoMetrics;

    @InjectMocks
    private VideoUtil videoUtil;

    @TempDir
    Path tempDir;

    private Path uploadsDir;
    private Path outputsDir;

    @BeforeEach
    void setUp() {
        uploadsDir = tempDir.resolve("uploads");
        outputsDir = tempDir.resolve("outputs");

        MockitoAnnotations.openMocks(this);
        VideoUtil.VIDEO_FILE_INPUT_DIR = tempDir.resolve("uploads").toString();
        VideoUtil.VIDEO_FILE_OUTPUT_DIR = tempDir.resolve("outputs").toString();
    }

    @Test
    void uploadVideo_QuandoArquivoValido_DeveRetornarTrue() throws IOException {
        // Arrange
        String conteudo = "conteudo do video";
        MultipartFile file = new MockMultipartFile(
                "video.mp4",
                "video.mp4",
                "video/mp4",
                conteudo.getBytes()
        );
        String fileName = "teste.mp4";

        // Act
        boolean resultado = videoUtil.uploadVideo(file, fileName);

        // Assert
        assertTrue(resultado);
        assertTrue(Files.exists(Paths.get(VideoUtil.VIDEO_FILE_INPUT_DIR, fileName)));
        verify(videoMetrics).incrementUploadsVideoSuccess();
        verify(videoMetrics).incrementUploadsVideoTotal();
    }

//    @Test
//    void uploadVideo_QuandoErroAoSalvar_DeveRetornarFalse() throws IOException {
//        // Arrange
//        MultipartFile file = mock(MultipartFile.class);
//        doThrow(new IOException()).when(file).transferTo(any(File.class));
//
//        // Act
//        boolean resultado = videoUtil.uploadVideo(file, "teste.mp4");
//
//        // Assert
//        assertFalse(resultado);
//        verify(videoMetrics).incrementUploadsVideoError();
//        verify(videoMetrics).incrementUploadsVideoTotal();
//    }

    @Test
    void compactVideo_QuandoArquivoValido_DeveRetornarTrue() throws IOException {
        // Arrange
        String videoId = "123";
        String conteudo = "conteudo do video";
        Path inputFile = tempDir.resolve("video.mp4");
        Files.write(inputFile, conteudo.getBytes());
        String prefixFileName = "prefix";

        // Act
        boolean resultado = videoUtil.compactVideo(videoId, inputFile.toString(), prefixFileName);

        // Assert
        assertTrue(resultado);
        Path zipPath = Paths.get(VideoUtil.VIDEO_FILE_OUTPUT_DIR, prefixFileName + "_" + videoId + ".zip");
        assertTrue(Files.exists(zipPath));

        // Verifica conteúdo do ZIP
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            boolean foundVideo = false;
            boolean foundInfo = false;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("video.mp4")) foundVideo = true;
                if (entry.getName().equals("frame-info.txt")) foundInfo = true;
            }

            assertTrue(foundVideo && foundInfo);
        }

        verify(videoMetrics).incrementVideoCompressionsSuccess();
        verify(videoMetrics).incrementVideoCompressionsTotal();
    }

    @Test
    void compactVideo_QuandoErroAoComprimir_DeveRetornarFalse() {
        // Arrange
        String videoId = "123";
        String inputPath = "arquivo_inexistente.mp4";
        String prefixFileName = "prefix";

        // Act
        boolean resultado = videoUtil.compactVideo(videoId, inputPath, prefixFileName);

        // Assert
        assertFalse(resultado);
        verify(videoMetrics).incrementVideoCompressionsError();
        verify(videoMetrics).incrementVideoCompressionsTotal();
    }

    @Test
    void deleteVideo_QuandoArquivosExistem_DeveRemoverAmbos() throws IOException {
        // Arrange
        String fileName = "video.mp4";
        String zipFileName = "video.zip";

        // Cria arquivos de teste
        Files.createDirectories(Paths.get(VideoUtil.VIDEO_FILE_INPUT_DIR));
        Files.createDirectories(Paths.get(VideoUtil.VIDEO_FILE_OUTPUT_DIR));
        Files.createFile(Paths.get(VideoUtil.VIDEO_FILE_INPUT_DIR, fileName));
        Files.createFile(Paths.get(VideoUtil.VIDEO_FILE_OUTPUT_DIR, zipFileName));

        // Act
        assertDoesNotThrow(() -> videoUtil.deleteVideo(fileName, zipFileName));

        // Assert
        assertFalse(Files.exists(Paths.get(VideoUtil.VIDEO_FILE_INPUT_DIR, fileName)));
        assertFalse(Files.exists(Paths.get(VideoUtil.VIDEO_FILE_OUTPUT_DIR, zipFileName)));
    }



    @Test
    void compactVideo_QuandoSucesso_DeveRetornarTrue() throws IOException {
        // Arrange
        String videoId = "123";
        String prefixFileName = "prefix";

        // Criar diretórios necessários
        Files.createDirectories(uploadsDir);
        Files.createDirectories(outputsDir);

        // Criar arquivo de teste
        String inputPath = createTestFile(uploadsDir, "test_video.mp4");

        // Act
        boolean result = videoUtil.compactVideo(videoId, inputPath, prefixFileName);

        // Assert
        assertTrue(result);
        assertTrue(Files.exists(outputsDir.resolve(prefixFileName + "_" + videoId + ".zip")));
        verify(videoMetrics).incrementVideoCompressionsSuccess();
        verify(videoMetrics).incrementVideoCompressionsTotal();
    }

    @Test
    void compactVideo_QuandoArquivoNaoExiste_DeveRetornarFalse() {
        // Arrange
        String videoId = "123";
        String inputPath = "/caminho/inexistente/video.mp4";
        String prefixFileName = "prefix";

        // Act
        boolean result = videoUtil.compactVideo(videoId, inputPath, prefixFileName);

        // Assert
        assertFalse(result);
        verify(videoMetrics).incrementVideoCompressionsError();
        verify(videoMetrics).incrementVideoCompressionsTotal();
    }

    @Test
    void uploadVideo_QuandoSucesso_DeveRetornarTrue() throws IOException {
        // Arrange
        byte[] content = "test content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "video",
                "test.mp4",
                "video/mp4",
                content
        );

        // Act
        boolean result = videoUtil.uploadVideo(file, "test.mp4");

        // Assert
        assertTrue(result);
        assertTrue(Files.exists(uploadsDir.resolve("test.mp4")));
        verify(videoMetrics).incrementUploadsVideoSuccess();
        verify(videoMetrics).incrementUploadsVideoTotal();
    }


    @Test
    void uploadVideo_QuandoErroAoSalvar_DeveRetornarFalse() throws IOException {
        // Arrange
        Files.createDirectories(uploadsDir); // Garante que o diretório existe

        MultipartFile file = new MockMultipartFile(
                "video",
                "test.mp4",
                "video/mp4",
                "test content".getBytes()
        ) {
            @Override
            public void transferTo(File dest) throws IOException {
                throw new IOException("Erro simulado ao salvar arquivo");
            }

            @Override
            public void transferTo(Path dest) throws IOException {
                throw new IOException("Erro simulado ao salvar arquivo");
            }
        };

        // Act
        boolean resultado = videoUtil.uploadVideo(file, "teste.mp4");

        // Assert
        assertFalse(resultado);
        verify(videoMetrics).incrementUploadsVideoError();
        verify(videoMetrics).incrementUploadsVideoTotal();
        assertFalse(Files.exists(uploadsDir.resolve("teste.mp4")));
    }

    @Test
    void deleteVideo_QuandoArquivosExistem_DeveExcluirComSucesso() throws IOException {
        // Arrange
        String fileName = "video.mp4";
        String zipFileName = "video.zip";

        // Criar diretórios necessários
        Files.createDirectories(uploadsDir);
        Files.createDirectories(outputsDir);

        // Criar arquivos de teste
        Files.write(uploadsDir.resolve(fileName), "test".getBytes());
        Files.write(outputsDir.resolve(zipFileName), "test".getBytes());

        // Act
        videoUtil.deleteVideo(fileName, zipFileName);

        // Assert
        assertFalse(Files.exists(uploadsDir.resolve(fileName)));
        assertFalse(Files.exists(outputsDir.resolve(zipFileName)));
    }


    @Test
    void deleteVideo_QuandoArquivosNaoExistem_NaoDeveLancarExcecao() {
        // Arrange
        String fileName = "inexistente.mp4";
        String zipFileName = "inexistente.zip";

        // Act & Assert
        assertDoesNotThrow(() -> videoUtil.deleteVideo(fileName, zipFileName));
        verify(videoMetrics, never()).incrementVideoCompressionsError();
        verify(videoMetrics, never()).incrementVideoCompressionsTotal();
    }

    private String createTestFile(Path dir, String fileName) throws IOException {
        Path file = dir.resolve(fileName);
        Files.write(file, "test content".getBytes());
        return file.toString();
    }

}
