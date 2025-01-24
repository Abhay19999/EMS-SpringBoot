package io.org.reactivestax.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.service.OTPService;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OTPController.class)
class OTPControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OTPService mockOtpService;

    private OtpDTO otpDTO;

    @BeforeEach
    void setUp() {
        otpDTO = new OtpDTO();
        otpDTO.setClientId(1L);
        otpDTO.setEmail("abhaynimavat2410@gmail.com");
        otpDTO.setMobileNumber("6478181379");
    }

    @Test
    void testPostRequestForGenerateOtpForSms() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String otpJson = objectMapper.writeValueAsString(otpDTO);
        otpDTO.setContactMethod(DeliveryMethodEnum.SMS);
        when(mockOtpService.handleOtpRequest(otpDTO, "sms")).thenReturn("OTP Generated successfully");
        mockMvc.perform(post("/api/v1/otp/sms")
                        .content(otpJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("OTP Generated successfully"))
                .andExpect(status().isOk());
    }

    @Test
    void testPostMappingForGenerateOtpForCall() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String otpJson = objectMapper.writeValueAsString(otpDTO);
        otpDTO.setContactMethod(DeliveryMethodEnum.CALL);
        when(mockOtpService.handleOtpRequest(otpDTO, "call")).thenReturn("OTP Generated successfully");
        mockMvc.perform(post("/api/v1/otp/call")
                        .content(otpJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testPostMappingForGenerateOtpForEmail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String otpJson = objectMapper.writeValueAsString(otpDTO);
        otpDTO.setContactMethod(DeliveryMethodEnum.EMAIL);
        when(mockOtpService.handleOtpRequest(otpDTO, "email")).thenReturn("OTP Generated successfully");
        mockMvc.perform(post("/api/v1/otp/email")
                        .content(otpJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testPutMappingForVerifyOto() throws Exception {
        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setOtp(1234L);
        otpVerificationDTO.setClientId(1L);
        ObjectMapper objectMapper = new ObjectMapper();
        String otpVerificationJson = objectMapper.writeValueAsString(otpVerificationDTO);
        when(mockOtpService.verifyOtp(otpVerificationDTO)).thenReturn("OTP verified successfully");
        mockMvc.perform(put("/api/v1/otp/verify")
                        .content(otpVerificationJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("OTP verified successfully"))
                .andExpect(status().isOk());
    }
    @Test
    void testGetStatusOfClient() throws Exception {
        when(mockOtpService.getClientStatus(1L)).thenReturn("Client has successfully verified the otp");
        mockMvc.perform(get("/api/v1/otp/status/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Client has successfully verified the otp"));
    }


}