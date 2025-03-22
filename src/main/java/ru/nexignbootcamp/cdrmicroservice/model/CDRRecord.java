package ru.nexignbootcamp.cdrmicroservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "cdr_record", indexes = {@Index(name = "idx_initiator_msisdn", columnList = "initiator_msisdn"), @Index(name = "idx_receiver_msisdn", columnList = "receiver_msisdn")})
@Data
public class CDRRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String callType;
    private String initiatorMsisdn;
    private String receiverMsisdn;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}