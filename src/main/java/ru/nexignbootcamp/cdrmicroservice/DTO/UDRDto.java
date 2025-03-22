package ru.nexignbootcamp.cdrmicroservice.DTO;

import lombok.Data;

@Data
public class UDRDto {
    private String msisdn;
    private String incomingCall;
    private String outcomingCall;
}
