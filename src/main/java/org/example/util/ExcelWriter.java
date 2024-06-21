package org.example.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.entity.PriceRecord;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelWriter {

    public byte[] writeAnomalies(List<PriceRecord> anomalies) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Anomalies");

        int rowIndex = 0;
        for (PriceRecord record : anomalies) {
            Row row = sheet.createRow(rowIndex++);
            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(record.getDate().toString()); // записываем дату в ячейку
            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(record.getValue()); // записываем значение аномалии в ячейку
        }

        return writeToByteArray(workbook);
    }

    private byte[] writeToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();
        } finally {
            workbook.close();
        }
    }
}