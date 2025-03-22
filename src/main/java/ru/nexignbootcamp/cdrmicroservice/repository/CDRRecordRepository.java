package ru.nexignbootcamp.cdrmicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nexignbootcamp.cdrmicroservice.model.CDRRecord;

import java.time.LocalDateTime;
import java.util.List;




/**
 * Репозиторий для работы с CDR записями.
 */
public interface CDRRecordRepository extends JpaRepository<CDRRecord, Long> {
    interface CallDurationSummary {
        String getMsisdn();

        Long getTotalDurationInSeconds();
    }
    @Query(value = "SELECT initiator_msisdn AS msisdn, SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) AS totalDurationInSeconds FROM cdr_record WHERE call_type = '01' AND initiator_msisdn IN (SELECT msisdn FROM subscriber) AND start_time >= :start AND start_time < :end GROUP BY initiator_msisdn", nativeQuery = true)
    List<CallDurationSummary> getOutgoingTotals(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT receiver_msisdn AS msisdn, SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) AS totalDurationInSeconds FROM cdr_record WHERE call_type = '02' AND receiver_msisdn IN (SELECT msisdn FROM subscriber) AND start_time >= :start AND start_time < :end GROUP BY receiver_msisdn", nativeQuery = true)
    List<CallDurationSummary> getIncomingTotals(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) FROM cdr_record WHERE call_type = '01' AND initiator_msisdn = :msisdn", nativeQuery = true)
    Long getTotalOutgoingSeconds(@Param("msisdn") String msisdn);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) FROM cdr_record WHERE call_type = '02' AND receiver_msisdn = :msisdn", nativeQuery = true)
    Long getTotalIncomingSeconds(@Param("msisdn") String msisdn);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) FROM cdr_record WHERE call_type = '01' AND initiator_msisdn = :msisdn AND start_time >= :start AND start_time < :end", nativeQuery = true)
    Long getTotalOutgoingSeconds(@Param("msisdn") String msisdn, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) FROM cdr_record WHERE call_type = '02' AND receiver_msisdn = :msisdn AND start_time >= :start AND start_time < :end", nativeQuery = true)
    Long getTotalIncomingSeconds(@Param("msisdn") String msisdn, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT c FROM CDRRecord c WHERE (c.initiatorMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) AND c.startTime >= :start AND c.startTime < :end ORDER BY c.startTime")
    List<CDRRecord> findByMsisdnAndPeriod(@Param("msisdn") String msisdn, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}