package io.org.reactivestax.controller;

import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.service.OTPService;
import io.org.reactivestax.type.DeliveryMethodEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/otp")
public class OTPController {

    @Autowired
    private OTPService otpService;


    @PostMapping("/sms")
    public String generateOtpForSms(@Valid @RequestBody OtpDTO otpDTO){
        otpDTO.setContactMethod(DeliveryMethodEnum.SMS);
        return  otpService.generateOtp(otpDTO,"sms");
    }

    @PutMapping("/verify")
    public String verifyOtp(@Valid @RequestBody OtpVerificationDTO otpVerificationDTO){
        return otpService.verifyOtp(otpVerificationDTO);

    }
}
