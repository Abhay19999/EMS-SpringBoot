package io.org.reactivestax.repository;

import io.org.reactivestax.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<NotificationMessage,Long> {
}
