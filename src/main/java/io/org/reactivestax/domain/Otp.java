package io.org.reactivestax.domain;


import io.org.reactivestax.type.CustomerStatusEnum;
import io.org.reactivestax.type.DeliveryMethodEnum;
import io.org.reactivestax.type.OTPStatus;
import io.org.reactivestax.type.OTPVerificationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String otpId;

    private Long otpNumber;

    private Integer countGenerationNumber;

    private Long clientId;


    @Enumerated(EnumType.STRING)
    private OTPVerificationStatus otpVerificationStatus;

    @Enumerated(EnumType.STRING)
    private OTPStatus otpStatus;

    private int verificationCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private CustomerStatusEnum customerStatusEnum;

    private LocalDateTime blockedTimeFrame;
    private String mobileNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private DeliveryMethodEnum deliveryMethod;

    @PrePersist
    public void onPrePersist() {
        if (otpId == null) {
            otpId = UUID.randomUUID().toString();
        }
    }
}
