package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.service.impl.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceImplTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @Mock
    private MultipartFile multipartFile;

    @Value("${file.upload-dir}")
    private String uploadDir = "uploads";

    @BeforeEach
    public void setUp() {
        fileStorageService.setUploadDir("uploads");
    }

    @Test
    public void testUploadFile_DirectoryExists() throws IOException {
        String originalFilename = "resim.png";
        String fileContent = "This is a test file content.";
        byte[] content = fileContent.getBytes();

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(content));

        String expectedFileName = UUID.randomUUID() + "_" + originalFilename;
        Path expectedPath = Paths.get(uploadDir, expectedFileName);

        Files.copy(multipartFile.getInputStream(), expectedPath);

        String result = fileStorageService.uploadFile(multipartFile);

        assertNotNull(result);
        assertTrue(result.endsWith(originalFilename));

        verify(multipartFile, times(1)).getOriginalFilename();
    }

    @Test
    void shouldCreateDirectoryIfNotExist() throws IOException {
        Path tempDir = Files.createTempDirectory("uploads");

        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        MultipartFile file = new MockMultipartFile("resim", "resim.png", "text/plain", "sample data".getBytes());

        String uploadedFileName = fileStorageService.uploadFile(file);

        assertTrue(Files.exists(tempDir), "Directory should exist");

        Path uploadedFile = tempDir.resolve(uploadedFileName);

        assertTrue(Files.exists(uploadedFile), "File should be uploaded to the directory: " + uploadedFile);

        Files.delete(uploadedFile);
        Files.delete(tempDir);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUploadDirIsNull() {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", null);

        MultipartFile file = new MockMultipartFile("resim", "resim.png", "text/plain", "sample data".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.uploadFile(file);
        }, "Upload directory path is null");
    }
}