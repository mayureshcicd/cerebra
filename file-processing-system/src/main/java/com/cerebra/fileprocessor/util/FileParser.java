package com.cerebra.fileprocessor.util;


import com.cerebra.fileprocessor.exceptions.RestApiException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {

    public static List<String> parseFile(byte[] fileBytes , String file) {//throws Exception {
        List<String> messages = new ArrayList<>();

        if (file.equalsIgnoreCase(".csv")) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream))) {
                String template = br.readLine();  // Read template line
                String[] headers = br.readLine().split(",");  // Read headers
                String line;

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    Map<String, String> dataMap = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        dataMap.put(headers[i], values[i]);
                    }
                    messages.add(replaceTemplate(template, dataMap));
                }
            } catch (Exception e) {
                throw new RestApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else if (file.equalsIgnoreCase(".xls") || file.equalsIgnoreCase(".xlsx")) {
            Workbook workbook = null;
            try {
                if (file.equalsIgnoreCase(".xlsx")) {
                    workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes)); // For .xlsx

                } else if (file.equalsIgnoreCase(".xls")) {
                    workbook = new HSSFWorkbook(new ByteArrayInputStream(fileBytes)); // For .xls
                }
                Sheet sheet = workbook.getSheetAt(0);
                String template = sheet.getRow(0).getCell(0).getStringCellValue();
                Row headerRow = sheet.getRow(1);

                Map<Integer, String> headers = new HashMap<>();
                for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                    headers.put(i, headerRow.getCell(i).getStringCellValue());
                }

                for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Map<String, String> dataMap = new HashMap<>();
                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        dataMap.put(headers.get(j), getCellValue(row.getCell(j)));
                    }
                    messages.add(replaceTemplate(template, dataMap));
                }
            } catch (Exception e) {
                throw new RestApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return messages;
    }

    private static String replaceTemplate(String template, Map<String, String> dataMap) {
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

}


