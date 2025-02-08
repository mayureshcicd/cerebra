package com.cerebra.fileprocessor.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cerebra.fileprocessor.exceptions.RestApiException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PageableObject {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public static <T> Map<String, String> getAttributeNamesAndTypes(Class<T> dtoClass) {
        Map<String, String> attributeMap = new HashMap<>();

        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            attributeMap.put(fieldName, fieldType);
        }

        return attributeMap;
    }

    public Pageable isValid(Pageable pageable, Map<String, String> validSort, String defaultSortValue) {
        List<Sort.Order> orderList = new ArrayList<>();
        Iterator<Order> orders = pageable.getSort().iterator();
        int count = 0;

        while (orders.hasNext()) {
            Sort.Order order = orders.next();
            count = count + 1;
            for (Map.Entry<String, String> entry : validSort.entrySet()) {
                if (order.getProperty().equalsIgnoreCase(entry.getKey())) {
                    orderList.add(new Sort.Order(order.getDirection(), entry.getValue()));
                }
            }

        }

        if (orderList.isEmpty() || orderList.size() != count) {
            if (StringUtils.isBlank(defaultSortValue))
            {
                throw new RestApiException("Invalid Sorting criteria.", HttpStatus.BAD_REQUEST);
            }
            orderList.add(new Sort.Order(Sort.Direction.ASC, defaultSortValue));
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orderList));
        return pageable;

    }

    public <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    public <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {

       return sourceList.stream()
                .map(s -> modelMapper.map(s, targetClass)).collect(Collectors.toList());

    }

    public <S, T> Page<T> mapPage(Page<S> source, Class<T> targetClass) {
        List<S> sourceList = source.getContent();

        List<T> list = sourceList.stream()
                .map(s -> modelMapper.map(s, targetClass)).collect(Collectors.toList());

        return new PageImpl<>(list, PageRequest.of(source.getNumber(), source.getSize(), source.getSort()),
                source.getTotalElements());
    }

    public <T> T readValue(String content, Class<T> targetClass) {
        try {
            return objectMapper.readValue(content, targetClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public JsonNode getJsonNode(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            return null;
        }
    }
}
