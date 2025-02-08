package com.cerebra.fileprocessor.common;

import com.cerebra.fileprocessor.exceptions.RestApiException;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ResponseUtil {
    public static final String UPLOAD_DIR = "uploads/";
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".csv", ".xls", ".xlsx");

    public static <T> RestApiResponse<T> getResponse(T data, String message) {
        return RestApiResponse.<T>builder()
                .data(data)
                .message(message)
                .timestamp(new Date())
                .build();
    }

    public static <T> RestApiResponse<T> getResponse(Object message) {
        return RestApiResponse.<T>builder()
                .message(message)
                .timestamp(new Date())
                .build();
    }

    public static String getValue(Object o) {
        try {
            return objectMapper.writer().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static String getAuthUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return Objects.equals(authentication.getName(), "anonymousUser") ? "Admin" : authentication.getName();
        } catch (Exception e) {
            return "Admin";
        }

    }

    public static String getCurrentDate() {
        LocalDate today = LocalDate.now();
        return today.format(formatter);
    }

    public static void deleteFile(String fileName, String uploadDir) {
        if (!StringUtils.isBlank(fileName)) {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException ignored) {
            }
        }
    }
    public static String getReadableFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " Bytes";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", (double) sizeInBytes / 1024);
        } else {
            return String.format("%.2f MB", (double) sizeInBytes / (1024 * 1024));
        }
    }
    public static String uploadFile(MultipartFile file, String uploadDir) {
        Path targetLocation = null;
        String newFilename = "";
        if (file != null && !file.isEmpty()) {
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String extension = getFileExtension(originalFileName);
            String baseName = removeFileExtension(originalFileName);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddss.nnnnnnnnn"));
            newFilename = String.format("%s_%s%s", baseName, timestamp, extension);

            targetLocation = Paths.get(uploadDir).resolve(Objects.requireNonNull(newFilename));
            try {

                Files.createDirectories(targetLocation.getParent());
                Files.copy(file.getInputStream(), targetLocation);
            } catch (IOException e) {
                throw new RestApiException("There is an issue to save attached file.", HttpStatus.BAD_REQUEST);
            }
        }
        return newFilename;
    }

    public static byte[] getFileBytes(String fileName, String uploadDir) {
        try {
            File file = new File(uploadDir.concat("/".concat(fileName)));
            Resource resource = new UrlResource(file.toURI());
            return readBytesFromResource(resource);
        } catch (Exception e) {
            throw new RestApiException("Invalid file Name.", HttpStatus.BAD_REQUEST);
        }

    }
    public static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex != -1) ? filename.substring(dotIndex) : "";
    }

    private static String removeFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex != -1) ? filename.substring(0, dotIndex) : filename;
    }
    private static byte[] readBytesFromResource(Resource resource) throws IOException {
        File file = resource.getFile(); // This throws an IOException if the resource is not a file
        return Files.readAllBytes(file.toPath());
    }
    public static String validation(MultipartFile fileProcess)  {
        String fileName = fileProcess.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        List<String> validationMessages = new ArrayList<>();
        if (fileProcess.getSize() > MAX_FILE_SIZE) {
            validationMessages.add("File size exceeds 10MB limit.");
        }
        try {
            if (fileProcess.getInputStream().available() == 0) {
                validationMessages.add("The file is empty and cannot be processed.");
            }
        } catch (IOException e) {
            validationMessages.add("The file has invalid content.");
        }
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            validationMessages.add("Invalid file type. Only CSV and Excel files are allowed.");
        }
        if (!validationMessages.isEmpty()) {
            throw new RestApiException(validationMessages, HttpStatus.BAD_REQUEST);
        }
        return fileName;
    }


}
