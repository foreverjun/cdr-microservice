package ru.nexignbootcamp.cdrmicroservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ru.nexignbootcamp.cdrmicroservice.DTO.CDRReportRequest;
import ru.nexignbootcamp.cdrmicroservice.model.CDRRecord;
import ru.nexignbootcamp.cdrmicroservice.model.Subscriber;
import ru.nexignbootcamp.cdrmicroservice.repository.CDRRecordRepository;
import ru.nexignbootcamp.cdrmicroservice.repository.SubscriberRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CDRReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CDRRecordRepository cdrRecordRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        subscriberRepository.deleteAll();
        cdrRecordRepository.deleteAll();
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn("79990000001");
        subscriberRepository.save(subscriber);

        CDRRecord record = new CDRRecord();
        record.setCallType("01");
        record.setInitiatorMsisdn("79990000001");
        record.setReceiverMsisdn("79990000002");
        record.setStartTime(LocalDateTime.of(2024, 2, 1, 10, 0));
        record.setEndTime(LocalDateTime.of(2024, 2, 1, 10, 5));
        cdrRecordRepository.save(record);
    }

    @Test
    void generateCDRReport() throws Exception {

        CDRReportRequest request = new CDRReportRequest();
        request.setMsisdn("79990000001");
        request.setStartDate(LocalDateTime.parse("2024-02-01T00:00:00"));
        request.setEndDate(LocalDateTime.parse("2024-02-02T00:00:00"));

        ResultActions result = mockMvc.perform(post("/api/cdr-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesRegex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")));

        String uuid = result.andReturn().getResponse().getContentAsString();
        String expectedFileName = "79990000001_" + uuid + ".csv";
        Path filePath = Paths.get("reports", expectedFileName);

        assertTrue(Files.exists(filePath), "Report file should exist");

        String fileContent = Files.readString(filePath);
        String expectedContent = "01,79990000001,79990000002,2024-02-01T10:00:00,2024-02-01T10:05:00\n";

        assertEquals(expectedContent, fileContent, "File content should match expected CDR record");
    }

    @Test
    public void testGenerateCDRReport_StartTimeAfterEndTime() throws Exception {
        CDRReportRequest request = new CDRReportRequest();
        request.setMsisdn("79990000001");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);
        request.setStartDate(start);
        request.setEndDate(end);

        mockMvc.perform(post("/api/cdr-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateCDRReport_StartTimeNull() throws Exception {
        CDRReportRequest request = new CDRReportRequest();
        request.setMsisdn("79990000001");
        request.setStartDate(null);
        request.setEndDate(LocalDateTime.now());

        mockMvc.perform(post("/api/cdr-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateCDRReport_EndTimeNull() throws Exception {
        CDRReportRequest request = new CDRReportRequest();
        request.setMsisdn("79990000001");
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(null);

        mockMvc.perform(post("/api/cdr-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}