package io.org.reactivestax.controller;


import io.org.reactivestax.dto.MessageDTO;
import io.org.reactivestax.dto.UserLoginDTO;
import io.org.reactivestax.service.MessageService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ems")
public class MessageController {

    public static final String SMS = "sms";
    public static final String CALL = "call";
    public static final String EMAIL = "email";

    @Autowired
    private MessageService messageService;

    @PostMapping("/sms")
    public ResponseEntity<MessageDTO> sendToJMSWithSMSMethod(@Valid @RequestBody MessageDTO messageDTO){
        return ResponseEntity.ok(messageService.sendMessageToJMS(messageDTO, SMS));
    }
    @PostMapping("/activation/sms")
    public ResponseEntity<UserLoginDTO> sendActivationToJMSWithSMSMethod(@RequestBody UserLoginDTO userLoginDTO){
        return ResponseEntity.ok(messageService.sendTokenToJMS(userLoginDTO, SMS));
    }
    @PostMapping("/call")
    public ResponseEntity<MessageDTO> sendToJMSWithCallMethod(@Valid @RequestBody MessageDTO messageDTO){
        return ResponseEntity.ok(messageService.sendMessageToJMS(messageDTO, CALL));
    }
    @PostMapping("/email")
    public ResponseEntity<MessageDTO> sendToJMSWithEmailMethod(@Valid @RequestBody MessageDTO messageDTO){
        return ResponseEntity.ok(messageService.sendMessageToJMS(messageDTO, EMAIL));
    }


}
