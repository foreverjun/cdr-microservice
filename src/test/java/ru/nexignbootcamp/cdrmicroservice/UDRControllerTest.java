package ru.nexignbootcamp.cdrmicroservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.nexignbootcamp.cdrmicroservice.model.CDRRecord;
import ru.nexignbootcamp.cdrmicroservice.model.Subscriber;
import ru.nexignbootcamp.cdrmicroservice.repository.CDRRecordRepository;
import ru.nexignbootcamp.cdrmicroservice.repository.SubscriberRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UDRControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private CDRRecordRepository cdrRecordRepository;

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
    void getUDRForSubscriber() throws Exception {
        mockMvc.perform(get("/api/udr/79990000001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("79990000001"))
                .andExpect(jsonPath("$.outcomingCall").value("00:05:00"))
                .andExpect(jsonPath("$.incomingCall").value("00:00:00"));
    }

    @Test
    void getUDRForSubscriberByMonth() throws Exception {
        mockMvc.perform(get("/api/udr/79990000001")
                        .param("yearMonth", "2024-02")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("79990000001"))
                .andExpect(jsonPath("$.outcomingCall").value("00:05:00"))
                .andExpect(jsonPath("$.incomingCall").value("00:00:00"));
    }

    @Test
    void getAllUDRForMonth() throws Exception {
        mockMvc.perform(get("/api/udr")
                        .param("yearMonth", "2024-02")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].msisdn").value("79990000001"))
                .andExpect(jsonPath("$[0].outcomingCall").value("00:05:00"))
                .andExpect(jsonPath("$[0].incomingCall").value("00:00:00"));
    }
}
