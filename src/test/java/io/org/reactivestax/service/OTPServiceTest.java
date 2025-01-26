package io.org.reactivestax.service;

import io.org.reactivestax.domain.Client;
import io.org.reactivestax.domain.Otp;
import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.OTPRepository;
import io.org.reactivestax.type.enums.CustomerStatusEnum;
import io.org.reactivestax.type.enums.OTPStatus;
import io.org.reactivestax.type.enums.OTPVerificationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class OTPServiceTest {
    @Autowired
    private  OTPService otpService;

    @MockitoBean
    private OTPRepository otpRepository;


    @MockitoBean
    ClientRepository clientRepository;

    @MockitoBean
    private JMSProducer jmsProducer;


    @Test
    void testHandleOtpRequest_WhenClientIsNotRegistered(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("Client does not exist", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
        verify(clientRepository,times(1)).findById(1L);
    }
    @Test
    void testHandleOtpRequest_WhenClientIsRegistered(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));

        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("Otp has been generated", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
        verify(clientRepository,times(1)).findById(1L);
        verify(otpRepository,times(1)).save(any(Otp.class));
//        verify(jmsProducer,times(1)).sendMessage(null,eq("otpService"));
    }
    @Test
    void testCannotProcessExistingOtp(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        Otp otp = new Otp();
        otp.setOtpStatus(OTPStatus.EXPIRED);
        otp.setBlockedTimeFrame(LocalDateTime.now().plusMinutes(10));
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("OTP cannot be processed", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
    }
    @Test
    void testExistingOTPWhenClientHasNeverBeenBlocked(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        Otp otp = new Otp();
        otp.setOtpStatus(OTPStatus.EXPIRED);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("Otp sent", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
    }
    @Test
    void testWhenUserRequestNewOtpAfterGettingBlocked(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        Otp otp = new Otp();
        otp.setOtpStatus(OTPStatus.EXPIRED);
        otp.setBlockedTimeFrame(LocalDateTime.now().minusMinutes(3));
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("New OTP has been generated for you", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testIfUserGeneratesTheOtpWhileBlocked(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        Otp otp = new Otp();
        otp.setOtpStatus(OTPStatus.NOT_EXPIRED);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setCustomerStatusEnum(CustomerStatusEnum.BLOCKED);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("You are blocked from generating otp", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
    }
    @Test
    void testBlockAndExpirePreviousOtpAndGenerateNew(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        Otp otp = new Otp();
        otp.setOtpStatus(OTPStatus.NOT_EXPIRED);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setCustomerStatusEnum(CustomerStatusEnum.UNBLOCKED);
        otp.setCountGenerationNumber(1);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("OTP has been updated", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
        verify(otpRepository,times(1)).save(otp);
    }
    @Test
    void testBlockClientForGeneratingOtp(){
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        Otp otp = new Otp();
        otp.setOtpStatus(OTPStatus.NOT_EXPIRED);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setCustomerStatusEnum(CustomerStatusEnum.UNBLOCKED);
        otp.setCountGenerationNumber(4);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String response = otpService.handleOtpRequest(otpDTO,"sms");
        assertEquals("You have been blocked for generating too many OTPs", response);
        verify(otpRepository,times(1)).findFirstByClientIdOrderByCreatedAtDesc(1L);
        verify(otpRepository,times(2)).save(otp);
    }

    @Test
    void testOtpVerificationSuccessful(){
        Otp otp = new Otp();
        otp.setOtpNumber(123456L);
        otp.setVerificationCount(1);
        otp.setCreatedAt(LocalDateTime.now());

        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setOtp(123456L);
        otpVerificationDTO.setClientId(1L);

        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String otpStatus = otpService.verifyOtp(otpVerificationDTO);
        assertEquals("OTP has been verified successfully",otpStatus);
        verify(otpRepository,times(1)).save(otp);
    }
    @Test
    void testOtpVerificationAfterTwoMinutes(){
        Otp otp = new Otp();
        otp.setOtpNumber(123456L);
        otp.setVerificationCount(1);
        otp.setCreatedAt(LocalDateTime.now().minusMinutes(3));

        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setOtp(123456L);
        otpVerificationDTO.setClientId(1L);

        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String otpStatus = otpService.verifyOtp(otpVerificationDTO);
        assertEquals("OTP is expired generate new otp",otpStatus);
        verify(otpRepository,times(1)).save(otp);
    }

    @Test
    void testBlockClientFromVerificationForTwoMinutes(){
        Otp otp = new Otp();
        otp.setOtpNumber(123456L);
        otp.setVerificationCount(4);
        otp.setCreatedAt(LocalDateTime.now().minusMinutes(3));

        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setOtp(123456L);
        otpVerificationDTO.setClientId(1L);

        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String otpStatus = otpService.verifyOtp(otpVerificationDTO);
        assertEquals("You are currently blocked for 2 minutes",otpStatus);
        verify(otpRepository,times(1)).save(otp);
    }

    @Test
    void testUserEnterWrongOtp(){
        Otp otp = new Otp();
        otp.setOtpNumber(123456L);
        otp.setVerificationCount(1);
        otp.setCreatedAt(LocalDateTime.now().minusMinutes(3));

        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setOtp(123496L);
        otpVerificationDTO.setClientId(1L);

        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String otpStatus = otpService.verifyOtp(otpVerificationDTO);
        assertEquals("Wrong Otp entered please try again",otpStatus);
        verify(otpRepository,times(1)).save(otp);
    }
    @Test
    void testWhenClientDoesNotExists(){
        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setOtp(123496L);
        otpVerificationDTO.setClientId(1L);

        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        String otpStatus = otpService.verifyOtp(otpVerificationDTO);
        assertEquals("Client does not exists",otpStatus);

    }

    @Test
    void testGetClientStatusWhenClientIsNotVerified(){
        Otp otp = new Otp();
        otp.setOtpVerificationStatus(OTPVerificationStatus.NOT_VERIFIED);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String status = otpService.getClientStatus(1L);
        assertEquals(1L + " is not verified",status);
    }
    @Test
    void testGetClientStatusWhenClientIsVerified(){
        Otp otp = new Otp();
        otp.setOtpVerificationStatus(OTPVerificationStatus.VERIFIED);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(otp));
        String status = otpService.getClientStatus(1L);
        assertEquals(1L + " is verified",status);
    }

    @Test
    void testGetClientStatusWhenClientIdIsNotPresent(){
        Otp otp = new Otp();
        otp.setOtpVerificationStatus(OTPVerificationStatus.VERIFIED);
        when(otpRepository.findFirstByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        String status = otpService.getClientStatus(1L);
        assertEquals(1L + " is not present",status);
    }


}