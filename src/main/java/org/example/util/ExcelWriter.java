package org.example.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelWriter {

    public byte[] writeAnomalies(List<Double> anomalies) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Anomalies");

        int rowIndex = 0;
        for (Double value : anomalies) {
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue(value);
        }

        return writeToByteArray(workbook);
    }

    public byte[] writeSMA(List<Double> smaValues) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("SMA");

        int rowIndex = 0;
        for (Double value : smaValues) {
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue(value);
        }

        return writeToByteArray(workbook);
    }

    public byte[] writeEMA(List<Double> emaValues) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("EMA");

        int rowIndex = 0;
        for (Double value : emaValues) {
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue(value);
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