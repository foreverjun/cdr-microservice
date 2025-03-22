package ru.nexignbootcamp.cdrmicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nexignbootcamp.cdrmicroservice.model.CDRRecord;
import ru.nexignbootcamp.cdrmicroservice.repository.CDRRecordRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для генерации отчетов CDR (Call Detail Records).
 * Создает CSV-файлы с данными о звонках абонентов.
 */
@Service
public class CDRReportService {
    private final CDRRecordRepository cdrRecordRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public CDRReportService(CDRRecordRepository cdrRecordRepository) {
        this.cdrRecordRepository = cdrRecordRepository;
    }

    /**
     * Генерирует отчет CDR для указанного абонента и периода.
     *
     * @param msisdn номер абонента
     * @param start  начало периода
     * @param end    конец периода
     * @return UUID сгенерированного отчета
     */
    public String generateCDRReport(String msisdn, LocalDateTime start, LocalDateTime end) {
        String uuid = UUID.randomUUID().toString();
        String fileName = msisdn + "_" + uuid + ".csv";
        java.nio.file.Path path = Paths.get("reports", fileName);

        try (FileWriter writer = new FileWriter(path.toFile())) {
            List<CDRRecord> records = cdrRecordRepository.findByMsisdnAndPeriod(msisdn, start, end);
            for (CDRRecord record : records) {
                writer.write(String.format("%s,%s,%s,%s,%s\n", record.getCallType(), record.getInitiatorMsisdn(), record.getReceiverMsisdn(), record.getStartTime().format(formatter), record.getEndTime().format(formatter)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации CDR отчета", e);
        }
        return uuid;
    }
}
