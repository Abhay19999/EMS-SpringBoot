package io.org.reactivestax.repository;


import io.org.reactivestax.domain.Otp;
import io.org.reactivestax.type.enums.CustomerStatusEnum;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.enums.OTPStatus;
import io.org.reactivestax.type.enums.OTPVerificationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class OTPRepositoryWithEmbeddedDBTest {

    @Autowired
    private OTPRepository otpRepository;

    @Test
    void testOtpGenerationAndRetrival(){
        Otp otp = new Otp();
        otp.setOtpNumber(123456L);
        otp.setCountGenerationNumber(1);
        otp.setClientId(1001L);
        otp.setOtpVerificationStatus(OTPVerificationStatus.NOT_VERIFIED);
        otp.setOtpStatus(OTPStatus.NOT_EXPIRED);
        otp.setVerificationCount(0);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setCustomerStatusEnum(CustomerStatusEnum.UNBLOCKED);
        otp.setBlockedTimeFrame(null);
        otp.setMobileNumber("1234567890");
        otp.setEmail("test@example.com");
        otp.setDeliveryMethod(DeliveryMethodEnum.SMS);

        // Act: Save the entity and retrieve it
        Otp savedOtp = otpRepository.save(otp);
        Optional<Otp> retrievedOtp = otpRepository.findById(savedOtp.getId());

        // Assert: Verify the saved and retrieved entities
        assertTrue(retrievedOtp.isPresent());
        assertEquals(savedOtp.getId(), retrievedOtp.get().getId());
        assertEquals(123456L, retrievedOtp.get().getOtpNumber());
        assertEquals("1234567890", retrievedOtp.get().getMobileNumber());
        assertEquals("test@example.com", retrievedOtp.get().getEmail());
        assertEquals(DeliveryMethodEnum.SMS, retrievedOtp.get().getDeliveryMethod());
        assertEquals(OTPVerificationStatus.NOT_VERIFIED, retrievedOtp.get().getOtpVerificationStatus());
        assertEquals(OTPStatus.NOT_EXPIRED, retrievedOtp.get().getOtpStatus());

    }

}
