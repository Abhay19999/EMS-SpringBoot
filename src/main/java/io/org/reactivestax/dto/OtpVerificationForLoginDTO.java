package io.org.reactivestax.dto;

import lombok.Data;

@Data
public class OtpVerificationForLoginDTO {
    private Long otp;
    private String userId;
}
