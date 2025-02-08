package com.cerebra.fileprocessor.common;

import com.cerebra.fileprocessor.response.RestApiResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CacheHelper {
    public static boolean isResultEmpty(RestApiResponse<?> result) {
        if (result == null || result.getData() == null) {
            return true;
        }
        Object data = result.getData();
        if (data instanceof Collection) {
            return ((Collection<?>) data).isEmpty();
        }
        return false;
    }
}
