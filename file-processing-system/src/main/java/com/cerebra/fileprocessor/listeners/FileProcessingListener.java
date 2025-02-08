package com.cerebra.fileprocessor.listeners;

import com.cerebra.fileprocessor.events.FileUploadedEvent;
import com.cerebra.fileprocessor.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessingListener {

    private final FileProcessingService fileProcessingService;

    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer processingTimer;

    @Async("fileProcessingExecutor")
    @EventListener
    public void onFileUploaded(FileUploadedEvent event) {
        log.info("Received FileUploadedEvent for file: {}", event.getFileEntity().getOriginalFileName());
        try {
            processingTimer.record(() -> {
                fileProcessingService.processFileAsync(event.getFileEntity());
            });
            successCounter.increment();
            log.info("File processing started successfully for file: {}", event.getFileEntity().getOriginalFileName());
        } catch (Exception e) {
            failureCounter.increment();
            log.error("File processing failed for file: {}", event.getFileEntity().getOriginalFileName(), e);
        }
    }
}
