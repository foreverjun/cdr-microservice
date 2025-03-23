package ru.nexignbootcamp.cdrmicroservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nexignbootcamp.cdrmicroservice.DTO.CDRReportRequest;
import ru.nexignbootcamp.cdrmicroservice.service.CDRReportService;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CDRReportController {

    private final CDRReportService cdrReportService;
    public CDRReportController(CDRReportService cdrReportService){
        this.cdrReportService = cdrReportService;
    }

    @PostMapping("/cdr-report")
    public String generateCDRReport(@RequestBody CDRReportRequest request) throws IOException {
        return cdrReportService.generateCDRReport(request.getMsisdn(), request.getStartDate(), request.getEndDate());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
