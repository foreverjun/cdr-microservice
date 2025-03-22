package ru.nexignbootcamp.cdrmicroservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.nexignbootcamp.cdrmicroservice.model.CDRRecord;
import ru.nexignbootcamp.cdrmicroservice.repository.CDRRecordRepository;
import ru.nexignbootcamp.cdrmicroservice.repository.SubscriberRepository;
import ru.nexignbootcamp.cdrmicroservice.service.DataGeneratorService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DataGeneratorServiceTest {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private CDRRecordRepository cdrRecordRepository;

    @Autowired
    private DataGeneratorService dataGeneratorService;

    @BeforeEach
    void setUp() {
        subscriberRepository.deleteAll();
        cdrRecordRepository.deleteAll();
    }

    /**
     * Проверка хронологического порядка записей
     * Данные должны быть отсортированы по времени начала звонка, независимо от абонента.
     */

    @Test
    void testChronologicalOrder() {
        dataGeneratorService.generateData();
        List<CDRRecord> records = cdrRecordRepository.findAll();
        LocalDateTime previous = LocalDateTime.MIN;
        for (CDRRecord record : records) {
            assertTrue(record.getStartTime().isAfter(previous) || record.getStartTime().isEqual(previous), "Записи должны быть в хронологическом порядке");
            previous = record.getStartTime();
        }
    }

    /**
     * Проверка наличия не менее 10 абонентов в базе данных
     */
    @Test
    void testMinimumSubscribers() {
        dataGeneratorService.generateData();
        long subscriberCount = subscriberRepository.count();
        assertTrue(subscriberCount >= 10, "В базе должно быть не менее 10 абонентов");
    }

    /**
     * Проверка генерации данных за один год
     * Проверяется, что все записи находятся в пределах одного года.
     */
    @Test
    void testOneYearDataGeneration() {
        dataGeneratorService.generateData();
        List<CDRRecord> records = cdrRecordRepository.findAll();
        LocalDateTime first = records.get(0).getStartTime();
        LocalDateTime last = records.get(records.size() - 1).getStartTime();
        assertTrue(first.plusYears(1).isAfter(last) || first.plusYears(1).isEqual(last), "Данные должны охватывать ровно один год");
    }
}