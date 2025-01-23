package io.org.reactivestax.dto;


import lombok.Data;

@Data
public class OtpVerificationDTO {
    private Long otp;
    private Long clientId;
}
