package io.org.reactivestax.domain;


import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String messageId;

    private Long clientId;

    private String email;

    private String phoneNumber;

    private String rawMessage;


    @Enumerated(EnumType.STRING)
    private DeliveryMethodEnum deliveryMethod;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @PrePersist
    public void onPrePersist() {
        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
        }
    }

}
