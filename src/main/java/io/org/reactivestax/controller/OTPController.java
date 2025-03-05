package io.org.reactivestax.controller;

import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.dto.OtpVerificationForLoginDTO;
import io.org.reactivestax.dto.UserLoginDTO;
import io.org.reactivestax.service.OTPLoginService;
import io.org.reactivestax.service.OTPService;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/v1/otp")
public class OTPController {


    @Autowired
    private OTPService otpService;

    @Autowired
    private OTPLoginService otpLoginService;


    @PostMapping("/sms")
    public ResponseEntity<String> generateOtpForSms(@Valid @RequestBody OtpDTO otpDTO){
        otpDTO.setContactMethod(DeliveryMethodEnum.SMS);
        return ResponseEntity.ok(otpService.handleOtpRequest(otpDTO, "sms"));
    }
    @PostMapping("/login/sms")
    public ResponseEntity<String> generateOtpForLoginSms(@Valid @RequestBody UserLoginDTO userLoginDTO){
        return ResponseEntity.ok(otpLoginService.handleOtpRequestForLogin(userLoginDTO, "sms"));
    }
    @PostMapping("/call")
    public ResponseEntity<String> generateOtpForCall(@Valid @RequestBody OtpDTO otpDTO){
        otpDTO.setContactMethod(DeliveryMethodEnum.CALL);
        return ResponseEntity.ok(otpService.handleOtpRequest(otpDTO,"call"));
    }

    @PostMapping("/email")
    public ResponseEntity<String> generateOtpForEmail(@Valid @RequestBody OtpDTO otpDTO){
        otpDTO.setContactMethod(DeliveryMethodEnum.EMAIL);
        return ResponseEntity.ok(otpService.handleOtpRequest(otpDTO,"email"));
    }

    @PutMapping("/verify")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpVerificationDTO otpVerificationDTO){
        return ResponseEntity.ok(otpService.verifyOtp(otpVerificationDTO));
    }
    @PutMapping("/login/verify")
    public ResponseEntity<String> verifyLoginOtp(@RequestBody OtpVerificationForLoginDTO otpVerificationDTO){
        return ResponseEntity.ok(otpLoginService.verifyOtp(otpVerificationDTO));

    }
    @GetMapping("/status/{clientId}")
    public ResponseEntity<String> getClientStatus(@PathVariable Long clientId){
        return ResponseEntity.ok(otpService.getClientStatus(clientId));
    }
}
