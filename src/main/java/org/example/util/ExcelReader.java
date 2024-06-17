package org.example.util;

import org.example.entity.PriceRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelReader {

    public List<PriceRecord> readExcelFile(InputStream inputStream) {
        List<PriceRecord> records = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                PriceRecord record = new PriceRecord();

                Cell dateCell = row.getCell(0);
                LocalDate date = LocalDate.parse(dateCell.getStringCellValue(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                record.setDate(date);

                Cell valueCell = row.getCell(1);
                record.setValue(valueCell.getNumericCellValue());

                records.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }
}
