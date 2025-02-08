package com.cerebra.fileprocessor.controller;

import com.cerebra.fileprocessor.dto.ProcessFileDto;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.FileProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileProcessControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileProcessingService fileProcessScheduleService;

    @InjectMocks
    private FileProcessController fileProcessController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileProcessController).build();
    }

    @Test
        void testFileProcessors() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user", null, Collections.singletonList(new SimpleGrantedAuthority("UPLOAD")));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        RestApiResponse<String> mockResponse = RestApiResponse.<String>builder()
                .message("File is uploaded and processing started.")
                .data("File is uploaded and processing started.")
                .timestamp(new Date())
                .build();

        when(fileProcessScheduleService.processFile(any(), any())).thenReturn(mockResponse);

        mockMvc.perform(multipart("/api/files/upload")
                        .file("fileProcess", "test file content".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("File is uploaded and processing started."));
    }

    @Test
    void testGetProcessFileWithError() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user", null, Collections.singletonList(new SimpleGrantedAuthority("VIEW")));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        Page<ProcessFileDto> mockPage = new PageImpl<>(List.of(new ProcessFileDto()));

        RestApiResponse<Page<ProcessFileDto>> mockResponse = RestApiResponse.<Page<ProcessFileDto>>builder()
                .message("Error")
                .data(mockPage)
                .timestamp(new Date())
                .build();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Order.asc("id")));
        when(fileProcessScheduleService.getProcessFile(any(HttpServletRequest.class), eq(pageable)))
                .thenReturn(mockResponse);
        mockMvc = MockMvcBuilders.standaloneSetup(fileProcessController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new SortHandlerMethodArgumentResolver()
                )
                .build();
        mockMvc.perform(get("/api/files")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc"))
                .andExpect(status().is5xxServerError()) ;
    }


    @Test
    void testDeleteProcessFile() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin", null, Collections.singletonList(new SimpleGrantedAuthority("DELETE")));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        RestApiResponse<String> mockResponse = RestApiResponse.<String>builder()
                .message("Process file deleted successfully")
                .data("Process file deleted successfully")
                .timestamp(new Date())
                .build();

        when(fileProcessScheduleService.deleteProcessFile(any())).thenReturn(mockResponse);
        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Process file deleted successfully"));
    }

}