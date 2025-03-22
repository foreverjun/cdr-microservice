package ru.nexignbootcamp.cdrmicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexignbootcamp.cdrmicroservice.model.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}
