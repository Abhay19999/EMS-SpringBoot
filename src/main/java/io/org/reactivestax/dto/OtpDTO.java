package io.org.reactivestax.dto;


import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import lombok.Data;

@Data
public class OtpDTO {

    private Long clientId;
    private String mobileNumber;
    private String email;
    private DeliveryMethodEnum contactMethod;


}
