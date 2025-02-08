
package com.cerebra.fileprocessor.service;
import com.cerebra.fileprocessor.common.ResponseUtil;
import com.cerebra.fileprocessor.config.ConfigProperties;
import com.cerebra.fileprocessor.entity.ProcessFile;
import com.cerebra.fileprocessor.entity.User;
import com.cerebra.fileprocessor.exceptions.RestApiException;
import com.cerebra.fileprocessor.repository.ProcessFileRepository;
import com.cerebra.fileprocessor.repository.ProcessedMessageRepository;
import com.cerebra.fileprocessor.repository.UserRepository;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.impl.FileProcessingServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class FileProcessScheduleServiceImplTest {

    private static final Path UPLOAD_DIR = Paths.get("uploads"); // Folder path

    @MockBean
    private ConfigProperties configProperties;

    @Mock
    private ConfigProperties configProperty;

    @Mock
    private ResponseUtil responseUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProcessFileRepository processFileRepository;

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FileProcessingServiceImpl fileProcessScheduleService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterAll
    static void cleanup() {
        try {
            if (Files.exists(UPLOAD_DIR)) {
                Files.walk(UPLOAD_DIR)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) { }
                        });
            }
        } catch (IOException ignored) { }
    }

    @Test
    void testFileProcessScheduler_Success() throws Exception {
        // Prepare CSV content as byte array
        String csvContent = """
                Dear ${CustomerName}, your account ending ${AccNumber} is credited with ${Amount} ${Currency}.
                CustomerName,AccNumber,Amount,Currency
                John Doe,1234,2500,USD
                Alice Smith,5678,1500,EUR
                Bob Johnson,9876,3200,GBP
                """;

        byte[] fileBytes = csvContent.getBytes();

        MultipartFile fileProcess = new MockMultipartFile("file", "testFile.csv", "text/csv", fileBytes);

        ProcessFile processFile = new ProcessFile();
        processFile.setOriginalFileName("testFile.csv");
        processFile.setSystemFileName("testFile_system.csv");
        processFile.setProcessed(false);

        User user = new User();
        user.setEmail("test@example.com");
        processFile.setUser(user);

        when(processFileRepository.saveAndFlush(any(ProcessFile.class))).thenReturn(processFile);
        when(processedMessageRepository.saveAllAndFlush(any())).thenReturn(null);
        doNothing().when(notificationService).notifyFileProcessStatusToMonitor(any(String.class),any(String.class));

        fileProcessScheduleService.fileProcessScheduler(fileBytes, processFile);

        assertTrue(processFile.isProcessed());
        verify(processedMessageRepository, times(1)).saveAllAndFlush(any());
        verify(processFileRepository, times(1)).saveAndFlush(any());
        verify(notificationService, times(1)).notifyFileProcessStatusToMonitor(any(String.class),any(String.class));
    }

    @Test
    void testProcessFile_Success() throws Exception {

        HttpServletRequest requestH = mock(HttpServletRequest.class);

        String csvContent = """
                Dear ${CustomerName}, your account ending ${AccNumber} is credited with ${Amount} ${Currency}.
                CustomerName,AccNumber,Amount,Currency
                John Doe,1234,2500,USD
                Alice Smith,5678,1500,EUR
                Bob Johnson,9876,3200,GBP
                """;

        byte[] fileBytes = csvContent.getBytes(); // Convert CSV string content to byte array
        MultipartFile fileProcess = new MockMultipartFile("file", "testFile.csv", "text/csv", fileBytes);

        when(jwtService.getAttributeValue(any(), eq("email"))).thenReturn("test@example.com");
       // when(configProperty.getUploadDirectory()).thenReturn("/dummy/upload/directory");
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        RestApiResponse<String> response = fileProcessScheduleService.processFile(requestH, fileProcess);

        assertEquals("testFile.csv File is uploaded and processing started.", response.getMessage());
        verify(userRepository, times(1)).findByEmail(any());
        verify(processFileRepository, times(1)).saveAndFlush(any());
    }


    @Test
    void testDeleteProcessFile_Success() {
        Long processFileId = 1L;
        ProcessFile processFile = new ProcessFile();
        processFile.setId(processFileId);
        processFile.setSystemFileName("testFile.csv");

        when(processFileRepository.findById(processFileId)).thenReturn(Optional.of(processFile));
        doNothing().when(processedMessageRepository).deleteByProcessFileId(any(Long.class));
        doNothing().when(processFileRepository).delete(any(ProcessFile.class));

        RestApiResponse<String> response = fileProcessScheduleService.deleteProcessFile(processFileId);

        assertEquals("Process file deleted successfully", response.getMessage());

        verify(processFileRepository, times(1)).findById(processFileId);
        verify(processedMessageRepository, times(1)).deleteByProcessFileId(processFileId);
        verify(processFileRepository, times(1)).delete(processFile);
    }

    @Test
    void testDeleteProcessFile_NotFound() {
        Long processFileId = 1L;
        when(processFileRepository.findById(processFileId)).thenReturn(Optional.empty());

        RestApiException exception = null;
        try {
            fileProcessScheduleService.deleteProcessFile(processFileId);
        } catch (RestApiException e) {
            exception = e;
        }
        assertEquals("Process file not found", exception.getMessage());
    }




}