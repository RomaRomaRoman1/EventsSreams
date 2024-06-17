package org.example.controller;

import org.example.entity.PriceRecord;
import org.example.service.PriceRecordService;
import org.example.util.ExcelReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceRecordController {
    private final PriceRecordService priceRecordService;
    private final ExcelReader excelReader;
@Autowired
    public PriceRecordController(PriceRecordService priceRecordService, ExcelReader excelReader) {
        this.priceRecordService = priceRecordService;
    this.excelReader = excelReader;
}
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            List<PriceRecord> records = excelReader.readExcelFile(file.getInputStream());
            priceRecordService.addAllFromXml(records);
            return "File uploaded and data saved successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload and process file.";
        }
    }

}
