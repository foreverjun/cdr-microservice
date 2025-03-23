package ru.nexignbootcamp.cdrmicroservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nexignbootcamp.cdrmicroservice.model.CDRRecord;
import ru.nexignbootcamp.cdrmicroservice.model.Subscriber;
import ru.nexignbootcamp.cdrmicroservice.repository.CDRRecordRepository;
import ru.nexignbootcamp.cdrmicroservice.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Сервис для генерации тестовых данных.
 * Создает абонентов и записи CDR.
 */
@Service
@RequiredArgsConstructor
public class DataGeneratorService {
    private final SubscriberRepository subscriberRepository;
    private final CDRRecordRepository cdrRecordRepository;


    private final Random random = new Random(42);

    public void generateData() {
        for (int i = 1; i <= 10; i++) {
            Subscriber subscriber = new Subscriber();
            subscriber.setMsisdn("799900000" + String.format("%02d", i));
            subscriberRepository.save(subscriber);
        }

        LocalDateTime currentTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime endTime = currentTime.plusYears(1);
        List<Subscriber> subscribers = subscriberRepository.findAll();

        ArrayList<CDRRecord> records = new ArrayList<>();
        while (currentTime.isBefore(endTime)) {
            Subscriber subscriber = subscribers.get(random.nextInt(subscribers.size()));
            String callType = random.nextBoolean() ? "01" : "02";
            String otherMsisdn = generateRandomMsisdn();
            LocalDateTime startTime = currentTime;
            int durationSeconds = random.nextInt(3600) + 1; // 1-3600 секунд
            LocalDateTime callEndTime = startTime.plusSeconds(durationSeconds);
            String initiatorMsisdn = callType.equals("01") ? subscriber.getMsisdn() : otherMsisdn;
            String receiverMsisdn = callType.equals("01") ? otherMsisdn : subscriber.getMsisdn();

            CDRRecord record = new CDRRecord();
            record.setCallType(callType);
            record.setInitiatorMsisdn(initiatorMsisdn);
            record.setReceiverMsisdn(receiverMsisdn);
            record.setStartTime(startTime);
            record.setEndTime(callEndTime);
            records.add(record);

            int minutes = random.nextInt(60) + 1; // Интервал 1-20 минут
            currentTime = currentTime.plusMinutes(minutes);
        }
        cdrRecordRepository.saveAll(records);
    }

    private String generateRandomMsisdn() {
        StringBuilder sb = new StringBuilder("7");
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}