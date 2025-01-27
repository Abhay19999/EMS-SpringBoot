package io.org.reactivestax.repository;


import io.org.reactivestax.domain.NotificationMessage;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.enums.MessageStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
 class NotificationRepositoryWithEmbeddedDBTest {

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;


    @Test
    void testSaveAndRetrieveNotificationMessage() {
        // Arrange: Create a NotificationMessage entity
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setClientId(1001L);
        notificationMessage.setEmail("test@example.com");
        notificationMessage.setPhoneNumber("1234567890");
        notificationMessage.setRawMessage("This is a test message.");
        notificationMessage.setDeliveryMethod(DeliveryMethodEnum.SMS);
        notificationMessage.setMessageStatus(MessageStatus.NOT_PROCESSED);
        notificationMessage.setProcessedAt(LocalDateTime.now());

        NotificationMessage savedMessage = notificationMessageRepository.save(notificationMessage);
        Optional<NotificationMessage> retrievedMessage = notificationMessageRepository.findById(savedMessage.getId());

        assertTrue(retrievedMessage.isPresent());
        assertEquals(savedMessage.getId(), retrievedMessage.get().getId());
        assertEquals("test@example.com", retrievedMessage.get().getEmail());
        assertEquals("1234567890", retrievedMessage.get().getPhoneNumber());
        assertEquals("This is a test message.", retrievedMessage.get().getRawMessage());
        assertEquals(DeliveryMethodEnum.SMS, retrievedMessage.get().getDeliveryMethod());
        assertEquals(MessageStatus.NOT_PROCESSED, retrievedMessage.get().getMessageStatus());
        assertNotNull(retrievedMessage.get().getMessageId());
        assertNotNull(retrievedMessage.get().getCreatedAt());
    }

}
