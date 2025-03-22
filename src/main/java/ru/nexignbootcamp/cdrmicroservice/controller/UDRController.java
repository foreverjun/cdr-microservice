package ru.nexignbootcamp.cdrmicroservice.controller;

import org.springframework.web.bind.annotation.*;
import ru.nexignbootcamp.cdrmicroservice.DTO.UDRDto;
import ru.nexignbootcamp.cdrmicroservice.service.UDRService;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UDRController {
    private final UDRService udrService;

    public UDRController(UDRService udrService) {
        this.udrService = udrService;
    }

    @GetMapping("/udr/{msisdn}")
    public UDRDto getUDR(@PathVariable String msisdn, @RequestParam(required = false) String yearMonth) {
        Optional<YearMonth> ym = yearMonth != null ? Optional.of(YearMonth.parse(yearMonth)) : Optional.empty();
        return udrService.getUDR(msisdn, ym);
    }

    @GetMapping("/udr")
    public List<UDRDto> getAllUDR(@RequestParam String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        return udrService.getAllUDR(ym);
    }
}