package org.example.controller;

import org.example.entity.PriceRecord;
import org.example.service.PriceAnalysisService;
import org.example.service.PriceRecordService;
import org.example.util.ExcelReader;
import org.example.util.ExcelWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceRecordController {
    private final PriceRecordService priceRecordService;
    private final PriceAnalysisService priceAnalysisService;
    private final ExcelReader excelReader;
    private final ExcelWriter excelWriter;

    @Autowired
    public PriceRecordController(PriceRecordService priceRecordService, PriceAnalysisService priceAnalysisService, ExcelReader excelReader, ExcelWriter excelWriter) {
        this.priceRecordService = priceRecordService;
        this.priceAnalysisService = priceAnalysisService;
        this.excelReader = excelReader;
        this.excelWriter = excelWriter;
    }

    private List<PriceRecord> priceRecords;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            priceRecords = excelReader.readExcelFile(file.getInputStream());
            System.out.println(priceRecords.get(0) + " " + priceRecords.get(1) + " " + priceRecords.get(2) + " размер: " + priceRecords.size());
            deleteAllPriceRecords();
            addAllPriceRecords(priceRecords);
            return "File uploaded and data saved successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("no transaction is in progress")) {
                return "File uploaded and data saved successfully!";
            }
            return "Failed to upload and process file.";
        }
    }

    @Transactional
    public void deleteAllPriceRecords() {
        priceRecordService.deleteAllPriceRecords();
    }

    @Transactional
    public void addAllPriceRecords(List<PriceRecord> records) {
        priceRecordService.addAllFromXml(records);
    }

    @GetMapping
    public String getWelcomeMessage() {
        return "Welcome to Price Record API!";
    }

    @GetMapping("/download/anomalies")
    public ResponseEntity<byte[]> downloadAnomalies(@RequestParam double anomalyCoefficient) {
        List<PriceRecord> anomalies = priceAnalysisService.detectAnomalies(priceRecords, anomalyCoefficient);

        try {
            byte[] data = excelWriter.writeAnomalies(anomalies);
            return createExcelResponse(data, "anomalies.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/view/anomalies")
    public List<PriceRecord> viewAnomalies(@RequestParam double anomalyCoefficient) {
        return priceAnalysisService.detectAnomalies(priceRecords, anomalyCoefficient);
    }

    @GetMapping("/predict/sma")
    public Double predictNextSMA(@RequestParam int period) {
        return priceAnalysisService.predictNextSMA(priceRecords, period);
    }

    @GetMapping("/predict/ema")
    public Double predictNextEMA(@RequestParam int period) {
        return priceAnalysisService.predictNextEMA(priceRecords, period);

    }

    @GetMapping("/calculateAveragePrice")
    public Double calculateAveragePrice() {
        return priceAnalysisService.calculateAveragePrice(priceRecords);
    }

    @GetMapping("/predict/linear")
    public Double predictNextPriceWithLinearRegression() {
        return priceAnalysisService.predictNextPriceWithLinearRegression(priceRecords);
    }


    private ResponseEntity<byte[]> createExcelResponse(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}