package ru.nexignbootcamp.cdrmicroservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nexignbootcamp.cdrmicroservice.DTO.CDRReportRequest;
import ru.nexignbootcamp.cdrmicroservice.service.CDRReportService;

@RestController
@RequestMapping("/api")
public class CDRReportController {

    private final CDRReportService cdrReportService;
    public CDRReportController(CDRReportService cdrReportService){
        this.cdrReportService = cdrReportService;
    }

    @PostMapping("/cdr-report")
    public String generateCDRReport(@RequestBody CDRReportRequest request) {
        return cdrReportService.generateCDRReport(request.getMsisdn(), request.getStartDate(), request.getEndDate());
    }
}
