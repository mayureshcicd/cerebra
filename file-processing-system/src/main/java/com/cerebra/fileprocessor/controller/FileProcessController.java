package com.cerebra.fileprocessor.controller;

import com.cerebra.fileprocessor.dto.ProcessFileDto;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.FileProcessingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileProcessController {

    private final FileProcessingService fileProcessScheduleService;

    @PreAuthorize("hasAnyRole('ROLE_USER') and hasAuthority('UPLOAD')")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse<String>> fileProcessor(HttpServletRequest requestH,
                                                                 @Parameter(required = true, schema = @Schema(type = "string", format = "binary"))
                                                                 @RequestPart(name = "fileProcess", required = true) MultipartFile fileProcess) {

            return ResponseEntity.status(HttpStatus.OK).body(
                    fileProcessScheduleService.processFile(requestH, fileProcess));

    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN') and hasAuthority('VIEW')")
    public  ResponseEntity<RestApiResponse<Page<ProcessFileDto>>> getProcessFile(HttpServletRequest request,
                                                                              @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({
                                                                                      @SortDefault(sort = "id", direction = Sort.Direction.ASC) }) Pageable pageable
                                                                              ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                fileProcessScheduleService.getProcessFile(request,pageable)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN') and hasAuthority('DELETE')")
    public ResponseEntity<RestApiResponse<String>> deleteProcessFile(HttpServletRequest request, @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                fileProcessScheduleService.deleteProcessFile(id)
        );
    }
}
