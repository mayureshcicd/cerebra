package com.cerebra.fileprocessor.common;

import com.cerebra.fileprocessor.exceptions.RestApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Validator validator;

    public static boolean isBlank(final CharSequence cs) {
        return StringUtils.isBlank(cs);
    }


    public static LocalDate getParseLocalDate(String campaignStartDate) {
        return LocalDate.parse(campaignStartDate, formatter);
    }


    public static String getParseStringDate(LocalDate date)
    {
        return date.format(formatter);
    }



    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (violations.isEmpty()) {
            return;
        }
            throw new ConstraintViolationException(violations);

    }

    public <T> void validate(T object, String message) {
        if (object==null)
            return;
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (violations.isEmpty()) {
            return;
        }

        List<String> modifiedMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        if (!StringUtils.isBlank(message)) {
            modifiedMessages.add(0, message);
        }
        throw new RestApiException(modifiedMessages, HttpStatus.BAD_REQUEST);
    }

    public <T> void validateMessages(T object, List<String> validationMessages) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (violations.isEmpty()) {
            return;
        }

        List<String> modifiedMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        validationMessages.addAll(modifiedMessages);
    }

}

