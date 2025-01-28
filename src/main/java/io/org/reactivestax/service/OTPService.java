package io.org.reactivestax.service;


import io.org.reactivestax.domain.Otp;
import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.OTPRepository;
import io.org.reactivestax.type.enums.CustomerStatusEnum;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.enums.OTPStatus;
import io.org.reactivestax.type.enums.OTPVerificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    private static final Log log = LogFactory.getLog(OTPService.class);
    @Value("${jms.otp.queue}")
    private String otpQueue;

    @Autowired
    private JMSProducer jmsProducer;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Long createRandomOneTimePassword() {
        log.info("Generating OTP");
        Random random = new Random();
        StringBuilder oneTimePassword = new StringBuilder();
        int length = 6;
        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            oneTimePassword.append(randomNumber);
        }
        return Long.parseLong(oneTimePassword.toString().trim());
    }

    public String handleOtpRequest(OtpDTO otpDTO, String contactMethod){
            return otpRepository.findFirstByClientIdOrderByCreatedAtDesc(otpDTO.getClientId())
                    .map(existingOtp -> processExistingOtp(existingOtp, otpDTO, contactMethod))
                    .orElseGet(() -> handleNewClientOtp(otpDTO, contactMethod));
    }

    private String handleNewClientOtp(OtpDTO otpDTO, String contactMethod) {
        return clientRepository.findById(otpDTO.getClientId())
                .map(client -> {
                    generateNewOtp(otpDTO, contactMethod);
                    return "Otp has been generated";
                })
                .orElse("Client does not exist");
    }

    private String processExistingOtp(Otp existingOtp, OtpDTO otpDTO, String contactMethod) {
        if (existingOtp.getOtpStatus() != OTPStatus.EXPIRED) {
            return handleActiveOtp(existingOtp, otpDTO, contactMethod);
        }
        if (existingOtp.getBlockedTimeFrame() == null) {
            generateNewOtp(otpDTO, contactMethod);
            return "Otp sent";
        }
        if (!existingOtp.getBlockedTimeFrame().plusMinutes(1).isAfter(LocalDateTime.now())) {
            generateNewOtp(otpDTO, contactMethod);
            return "New OTP has been generated for you";
        }

        return "OTP cannot be processed";
    }

    private String handleActiveOtp(Otp existingOtp, OtpDTO otpDTO, String contactMethod) {
        if (existingOtp.getCreatedAt().plusMinutes(1).isAfter(LocalDateTime.now()) &&
                existingOtp.getCustomerStatusEnum() == CustomerStatusEnum.BLOCKED) {
            return "You are blocked from generating otp";
        }

        blockAndExpireOtp(existingOtp);

        if (existingOtp.getCountGenerationNumber() <= 3) {
            createAndSaveNewOtp(existingOtp, otpDTO, contactMethod);
            return "OTP has been updated";
        } else {
            blockClient(existingOtp);
            return "You have been blocked for generating too many OTPs";
        }
    }

    private void blockAndExpireOtp(Otp otp) {
        otp.setCustomerStatusEnum(CustomerStatusEnum.BLOCKED);
        otp.setOtpStatus(OTPStatus.EXPIRED);
        otpRepository.save(otp);
    }

    private void createAndSaveNewOtp(Otp existingOtp, OtpDTO otpDTO, String contactMethod) {
        Otp newOtp = generateNewOtp(otpDTO, contactMethod);
        newOtp.setOtpNumber(createRandomOneTimePassword());
        newOtp.setVerificationCount(0);
        newOtp.setCountGenerationNumber(existingOtp.getCountGenerationNumber() + 1);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setCustomerStatusEnum(CustomerStatusEnum.UNBLOCKED);
        otpRepository.save(newOtp);
    }

    private void blockClient(Otp otp) {
        otp.setCustomerStatusEnum(CustomerStatusEnum.BLOCKED);
        otp.setBlockedTimeFrame(LocalDateTime.now());
        otp.setOtpVerificationStatus(OTPVerificationStatus.NOT_VERIFIED);
        otp.setOtpStatus(OTPStatus.EXPIRED);
        otpRepository.save(otp);
    }

    private Otp generateNewOtp(OtpDTO otpDTO, String contactMethod) {
        Otp otp = new Otp();
        otp.setOtpNumber(createRandomOneTimePassword());
        otp.setCountGenerationNumber(1);
        otp.setClientId(otpDTO.getClientId());
        otp.setOtpStatus(OTPStatus.NOT_EXPIRED);
        otp.setOtpVerificationStatus(OTPVerificationStatus.NOT_VERIFIED);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setVerificationCount(0);
        otp.setCustomerStatusEnum(CustomerStatusEnum.UNBLOCKED);
        otp.setMobileNumber(otpDTO.getMobileNumber());
        otp.setEmail(otpDTO.getEmail());
        setDeliveryMethodForOtp(otp,contactMethod);
        otpRepository.save(otp);
        jmsProducer.sendMessage(otp.getOtpId(), otpQueue);
        return otp;
    }
    private void setDeliveryMethodForOtp(Otp otp,String contactMethod){
        if (contactMethod.equals("sms")){
            otp.setDeliveryMethod(DeliveryMethodEnum.SMS);
        }else if(contactMethod.equals("email")){
            otp.setDeliveryMethod(DeliveryMethodEnum.EMAIL);
        }else {
            otp.setDeliveryMethod(DeliveryMethodEnum.CALL);
        }

    }


    public String verifyOtp(OtpVerificationDTO otpVerificationDTO) {
        Optional<Otp> otp = otpRepository.findFirstByClientIdOrderByCreatedAtDesc(otpVerificationDTO.getClientId());
        if (otp.isPresent()) {
            if (otp.get().getVerificationCount() > 3) {
                otp.get().setCustomerStatusEnum(CustomerStatusEnum.BLOCKED);
                otp.get().setBlockedTimeFrame(LocalDateTime.now());
                otpRepository.save(otp.get());
                return "You are currently blocked for 2 minutes";
            }
            if (otp.get().getOtpNumber().equals(otpVerificationDTO.getOtp())) {
                LocalDateTime localDateTime = otp.get().getCreatedAt();
                if (!localDateTime.plusMinutes(2).isBefore(LocalDateTime.now())) {
                    otp.get().setOtpVerificationStatus(OTPVerificationStatus.VERIFIED);
                    otp.get().setCountGenerationNumber(0);
                    otpRepository.save(otp.get());
                    return "OTP has been verified successfully";
                } else {
                    otp.get().setOtpStatus(OTPStatus.EXPIRED);
                    otpRepository.save(otp.get());
                    return "OTP is expired generate new otp";
                }
            } else {
                otp.get().setVerificationCount(otp.get().getVerificationCount() + 1);
                otpRepository.save(otp.get());
                return "Wrong Otp entered please try again";
            }
        }
        return "Client does not exists";
    }


    public String getClientStatus(Long clientId) {
        Optional<Otp> otpStatus = otpRepository.findFirstByClientIdOrderByCreatedAtDesc(clientId);
        boolean otpForClient = otpStatus.isPresent();
        if (otpForClient && otpStatus.get().getOtpVerificationStatus().equals(OTPVerificationStatus.NOT_VERIFIED)) {
            return clientId + " is not verified";
        } else if (!otpForClient) {
            return clientId + " is not present";
        } else {
            return clientId + " is verified";
        }

    }
}

