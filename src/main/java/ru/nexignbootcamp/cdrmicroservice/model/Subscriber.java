package ru.nexignbootcamp.cdrmicroservice.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subscriber", indexes = {@Index(name = "idx_msisdn", columnList = "msisdn")})
@Data
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String msisdn;
}
