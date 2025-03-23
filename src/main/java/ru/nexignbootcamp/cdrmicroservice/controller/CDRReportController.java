package ru.nexignbootcamp.cdrmicroservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nexignbootcamp.cdrmicroservice.DTO.CDRReportRequest;
import ru.nexignbootcamp.cdrmicroservice.service.CDRReportService;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class CDRReportController {

    private final CDRReportService cdrReportService;
    public CDRReportController(CDRReportService cdrReportService){
        this.cdrReportService = cdrReportService;
    }

    @PostMapping("/cdr-report")
    public String generateCDRReport(@RequestBody CDRReportRequest request) throws IOException {
        validateRequest(request);
        return cdrReportService.generateCDRReport(request.getMsisdn(), request.getStartDate(), request.getEndDate());
    }

    private void validateRequest(CDRReportRequest request) {
        LocalDateTime start = request.getStartDate();
        LocalDateTime end = request.getEndDate();
        if (start == null || end == null) {
            throw new IllegalArgumentException("startDate and endDate must not be null");
        }
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
