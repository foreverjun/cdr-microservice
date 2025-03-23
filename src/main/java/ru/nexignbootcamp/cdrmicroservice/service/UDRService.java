package ru.nexignbootcamp.cdrmicroservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nexignbootcamp.cdrmicroservice.DTO.UDRDto;
import ru.nexignbootcamp.cdrmicroservice.model.Subscriber;
import ru.nexignbootcamp.cdrmicroservice.repository.CDRRecordRepository;
import ru.nexignbootcamp.cdrmicroservice.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для получения отчетов UDR (Usage Data Records).
 * Предоставляет данные об использовании услуг абонентами.
 */
@Service
@RequiredArgsConstructor
public class UDRService {
    private final CDRRecordRepository cdrRecordRepository;
    private final SubscriberRepository subscriberRepository;

    /**
     * Возвращает отчет UDR для указанного абонента.
     *
     * @param msisdn    номер абонента
     * @param yearMonth опциональный фильтр по месяцу
     * @return DTO с данными UDR
     */
    public UDRDto getUDR(String msisdn, Optional<YearMonth> yearMonth) {
        Long outgoingSeconds;
        Long incomingSeconds;
        if (yearMonth.isPresent()) {
            YearMonth ym = yearMonth.get();
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
            outgoingSeconds = cdrRecordRepository.getTotalOutgoingSeconds(msisdn, start, end);
            incomingSeconds = cdrRecordRepository.getTotalIncomingSeconds(msisdn, start, end);
        } else {
            outgoingSeconds = cdrRecordRepository.getTotalOutgoingSeconds(msisdn);
            incomingSeconds = cdrRecordRepository.getTotalIncomingSeconds(msisdn);
        }
        outgoingSeconds = Optional.ofNullable(outgoingSeconds).orElse(0L);
        incomingSeconds = Optional.ofNullable(incomingSeconds).orElse(0L);

        UDRDto udr = new UDRDto();
        udr.setMsisdn(msisdn);
        String outgoing = formatDuration(outgoingSeconds);
        udr.setOutcomingCall(outgoing);
        String incoming = formatDuration(incomingSeconds);
        udr.setIncomingCall(incoming);
        return udr;
    }


    /**
     * Возвращает отчеты UDR для всех абонентов за указанный месяц.
     *
     * @param yearMonth месяц для фильтрации
     * @return список DTO с данными UDR
     */
    public List<UDRDto> getAllUDR(YearMonth yearMonth) {
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        List<CDRRecordRepository.CallDurationSummary> outgoingTotals = cdrRecordRepository.getOutgoingTotals(start, end);
        List<CDRRecordRepository.CallDurationSummary> incomingTotals = cdrRecordRepository.getIncomingTotals(start, end);

        System.out.println(outgoingTotals.get(0).getMsisdn());
        System.out.println(outgoingTotals.get(0).getTotalDurationInSeconds());

        Map<String, Long> outgoingMap = outgoingTotals.stream()
                .collect(Collectors.toMap(
                        CDRRecordRepository.CallDurationSummary::getMsisdn,
                        CDRRecordRepository.CallDurationSummary::getTotalDurationInSeconds
                ));

        Map<String, Long> incomingMap = incomingTotals.stream()
                .collect(Collectors.toMap(
                        CDRRecordRepository.CallDurationSummary::getMsisdn,
                        CDRRecordRepository.CallDurationSummary::getTotalDurationInSeconds
                ));

        List<Subscriber> subscribers = subscriberRepository.findAll();
        List<UDRDto> udrs = new ArrayList<>();
        for (Subscriber subscriber : subscribers) {
            String msisdn = subscriber.getMsisdn();
            Long outgoingSeconds = outgoingMap.getOrDefault(msisdn, 0L);
            Long incomingSeconds = incomingMap.getOrDefault(msisdn, 0L);

            UDRDto udr = new UDRDto();
            udr.setMsisdn(msisdn);
            String outgoing = formatDuration(outgoingSeconds);
            udr.setOutcomingCall(outgoing);
            String incoming = formatDuration(incomingSeconds);
            udr.setIncomingCall(incoming);
            udrs.add(udr);
        }
        return udrs;
    }


    /**
     * Форматирует длительность в секундах в строку формата HH:mm:ss.
     *
     * @param seconds длительность в секундах
     * @return отформатированная строка
     */
    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}