package io.org.reactivestax.controller;

import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.service.OTPService;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        return  otpService.handleOtpRequest(otpDTO,"sms");
    }
    @PostMapping("/call")
    public String generateOtpForCall(@Valid @RequestBody OtpDTO otpDTO){
        otpDTO.setContactMethod(DeliveryMethodEnum.CALL);
        return otpService.handleOtpRequest(otpDTO,"call");
    }

    @PostMapping("/email")
    public String generateOtpForEmail(@Valid @RequestBody OtpDTO otpDTO){
        otpDTO.setContactMethod(DeliveryMethodEnum.EMAIL);
        return otpService.handleOtpRequest(otpDTO,"email");
    }

    @PutMapping("/verify")
    public String verifyOtp(@Valid @RequestBody OtpVerificationDTO otpVerificationDTO){
        return otpService.verifyOtp(otpVerificationDTO);
    }
    @GetMapping("/status/{clientId}")
    public String getClientStatus(@PathVariable Long clientId){
        return otpService.getClientStatus(clientId);
    }
}
