package io.org.reactivestax.service;


import io.org.reactivestax.domain.NotificationMessage;
import io.org.reactivestax.domain.Otp;
import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.OTPRepository;
import io.org.reactivestax.type.CustomerStatus;
import io.org.reactivestax.type.DeliveryMethodEnum;
import io.org.reactivestax.type.OTPStatus;
import io.org.reactivestax.type.OTPVerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {

    @Value("${jms.otp.queue}")
    private String otpQueue;

    @Autowired
    private JMSProducer jmsProducer;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private ClientRepository clientRepository;

    public Long createRandomOneTimePassword() {
        Random random = new Random();
        StringBuilder oneTimePassword = new StringBuilder();
        int length = 6;
        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            oneTimePassword.append(randomNumber);
        }
        return Long.parseLong(oneTimePassword.toString().trim());
    }

    public String generateOtp(OtpDTO otpDTO,String contactMethod) {
        Otp otp = otpRepository.findFirstByClientIdOrderByCreatedAtDesc(otpDTO.getClientId());
        if (otp != null && otp.getOtpStatus() != OTPStatus.EXPIRED) {
            if (otp.getCreatedAt().plusMinutes(1).isAfter(LocalDateTime.now()) && otp.getCustomerStatus() == CustomerStatus.BLOCKED) {
                return "You are blocked from generating otp";
            }
            otp.setCustomerStatus(CustomerStatus.BLOCKED);
            otp.setOtpStatus(OTPStatus.EXPIRED);
            otpRepository.save(otp);
            if (otp.getCountGenerationNumber() <= 3) {
                Otp newOtp = generateNewOtp(otpDTO,contactMethod);
                newOtp.setOtpNumber(createRandomOneTimePassword());
                newOtp.setVerificationCount(0);
                newOtp.setCountGenerationNumber(otp.getCountGenerationNumber() + 1);
                newOtp.setCreatedAt(LocalDateTime.now());
                newOtp.setCustomerStatus(CustomerStatus.UNBLOCKED);
                otpRepository.save(newOtp);
                return "OTP have been updated";
            } else {
                otp.setCustomerStatus(CustomerStatus.BLOCKED);
                otp.setBlockedTimeFrame(LocalDateTime.now());
                otp.setOtpVerificationStatus(OTPVerificationStatus.NOT_VERIFIED);
                otp.setOtpStatus(OTPStatus.EXPIRED);
                otpRepository.save(otp);
                return "You have been blocked for generating too man OTP";
            }
        } else if (otp == null){
            generateNewOtp(otpDTO,contactMethod);
            return "Otp has been generated";
        } else if(otp.getBlockedTimeFrame()==null){
            generateNewOtp(otpDTO,contactMethod);
            return "Otp sent";
        }
        else if (!otp.getBlockedTimeFrame().plusMinutes(1).isAfter(LocalDateTime.now())) {
            generateNewOtp(otpDTO,contactMethod);
            return "New OTP hase been generated for you";
        } else {
            return "Client does not exists";
        }
    }

    private Otp generateNewOtp(OtpDTO otpDTO,String contactMethod) {
        Otp otp = new Otp();
        otp.setOtpNumber(createRandomOneTimePassword());
        otp.setCountGenerationNumber(1);
        otp.setClientId(otpDTO.getClientId());
        otp.setOtpStatus(OTPStatus.NOT_EXPIRED);
        otp.setOtpVerificationStatus(OTPVerificationStatus.NOT_VERIFIED);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setVerificationCount(0);
        otp.setCustomerStatus(CustomerStatus.UNBLOCKED);
        otp.setMobileNumber(otpDTO.getMobileNumber());
        otp.setEmail(otpDTO.getEmail());
        otpRepository.save(otp);
        createNotificationMessage(otpDTO,contactMethod);
        jmsProducer.sendMessage(otp.getOtpId(),otpQueue);
        return otp;
    }

    private void createNotificationMessage(OtpDTO otpDTO,String contactMethod) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setPhoneNumber(otpDTO.getMobileNumber());
        notificationMessage.setEmail(otpDTO.getEmail());
        notificationMessage.setClientId(otpDTO.getClientId());
        if(contactMethod.equals("sms")) {
            notificationMessage.setDeliveryMethod(DeliveryMethodEnum.SMS);
        } else if (contactMethod.equals("call")) {
            notificationMessage.setDeliveryMethod(DeliveryMethodEnum.CALL);
        }else{
            notificationMessage.setDeliveryMethod(DeliveryMethodEnum.EMAIL);
        }
    }

    public String verifyOtp(OtpVerificationDTO otpVerificationDTO) {
        Otp otp = otpRepository.findFirstByClientIdOrderByCreatedAtDesc(otpVerificationDTO.getClientId());
        if (otp != null) {
            if (otp.getVerificationCount() > 3) {
                otp.setCustomerStatus(CustomerStatus.BLOCKED);
                otp.setBlockedTimeFrame(LocalDateTime.now());
                otpRepository.save(otp);
                return "You are currently blocked for 2 minutes";
            }
            if (otp.getOtpNumber().equals(otpVerificationDTO.getOtp())) {
                LocalDateTime localDateTime = otp.getCreatedAt();
                if (!localDateTime.plusMinutes(2).isBefore(LocalDateTime.now())) {
                    otp.setOtpVerificationStatus(OTPVerificationStatus.VERIFIED);
                    otpRepository.save(otp);
                    return "OTP has been verified successfully";
                } else {
                    otp.setOtpStatus(OTPStatus.EXPIRED);
                    otpRepository.save(otp);
                    return "OTP is expired generate new otp";
                }
            } else {
                otp.setVerificationCount(otp.getVerificationCount() + 1);
                otpRepository.save(otp);
                return "Wrong Otp entered please try again";
            }
        }
        return "Client does not exists";
    }


}

