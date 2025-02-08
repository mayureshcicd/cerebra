package com.cerebra.fileprocessor.service;

import com.cerebra.fileprocessor.dto.ProcessFileDto;
import com.cerebra.fileprocessor.entity.ProcessFile;
import com.cerebra.fileprocessor.response.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {

    void processFileAsync(ProcessFile processFile);
    void fileProcessScheduler(byte[] fileBytes, ProcessFile processFile);

    RestApiResponse<String> processFile(HttpServletRequest requestH,
                                        MultipartFile fileProcess);

    RestApiResponse<Page<ProcessFileDto>> getProcessFile(HttpServletRequest requestH, Pageable pageable);

    RestApiResponse<String> deleteProcessFile(Long id);
}
