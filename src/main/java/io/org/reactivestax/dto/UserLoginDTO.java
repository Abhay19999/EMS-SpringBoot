package io.org.reactivestax.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDTO {
    private String userId;
    private String familyPin;
    private String message;
    private String phoneNumber;
}
