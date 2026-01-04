package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;


class DownloadServiceTest extends BaseServiceTest {

    @Autowired
    private DownloadService downloadService;

    @Test
    void ShouldDownloadFile() throws IOException {
        String fileName = "test file.txt";
        MockMultipartFile file = createFile(fileName);

        uploadService.upload(ROOT, TEST_ID, new MockMultipartFile[]{file});

        StreamingResponseBody answer = downloadService.download(fileName, TEST_ID).content();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> answer.writeTo(outputStream));

        assertArrayEquals(file.getBytes(), outputStream.toByteArray());
        assertEquals(new String(file.getBytes(), StandardCharsets.UTF_8), outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    void ShouldThrowNotFoundException_When_FileDoesntExist() {
        String fileName = "test file.txt";

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                downloadService.download(fileName, TEST_ID).content());

        assertEquals("Resource doesn't exists: " + fileName, exception.getMessage());
    }

    @Test
    void ShouldDownloadFolderInZip() throws IOException {
        String folderPath = TEST_PARENT_FOLDER;

        String innerFolderName = "inner folder/";
        String innerFolderPath = folderPath + innerFolderName;

        String fileName1 = "file1";
        String contentFile1 = "content file1";
        String filePath1 = folderPath + fileName1;

        String fileName2 = "file2";
        String contentFile2 = "content file2";
        String filePath2 = folderPath + fileName2;

        String fileName3 = "file3";
        String contentFile3 = "content file3";
        String filePath3 = innerFolderPath + fileName3;
        String innerPathForFile3 = innerFolderName + fileName3;

        MultipartFile[] files = new MockMultipartFile[]{
                createFile(filePath1, contentFile1),
                createFile(filePath2, contentFile2),
                createFile(filePath3, contentFile3),
        };
        uploadService.upload(ROOT, TEST_ID, files);

        StreamingResponseBody answer = downloadService.download(folderPath, TEST_ID).content();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        answer.writeTo(outputStream);
        byte[] zipBytes = outputStream.toByteArray();

        Map<String, byte[]> expectedResources = new HashMap<>(
                Map.of(
                        fileName1, contentFile1.getBytes(),
                        fileName2, contentFile2.getBytes(),
                        innerPathForFile3, contentFile3.getBytes(),
                        innerFolderName, new byte[0]
                ));

        Map<String, byte[]> answerResources = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                byte[] content = zis.readAllBytes();
                answerResources.put(entry.getName(), content);
                zis.closeEntry();
            }
        }

        assertEquals(expectedResources.size(), answerResources.size());

        for (Map.Entry<String, byte[]> expectedEntry : expectedResources.entrySet()) {
            String expectedFileName = expectedEntry.getKey();
            byte[] expectedContent = expectedEntry.getValue();

            assertTrue(answerResources.containsKey(expectedFileName),
                    "Отсутствует файл: " + expectedFileName);

            assertArrayEquals(expectedContent, answerResources.get(expectedFileName),
                    "Неверное содержимое файла в байтах: " + expectedFileName);

            assertEquals(
                    new String(expectedContent, StandardCharsets.UTF_8),
                    new String(answerResources.get(expectedFileName), StandardCharsets.UTF_8),
                    "Неверное содержимое файла: " + expectedFileName
            );

        }
    }

}