package io.org.reactivestax.repository;

import io.org.reactivestax.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage,Long> {
}
