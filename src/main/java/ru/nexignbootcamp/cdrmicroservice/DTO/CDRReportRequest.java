package ru.nexignbootcamp.cdrmicroservice.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CDRReportRequest {
    private String msisdn;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}