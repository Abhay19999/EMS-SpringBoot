package io.org.reactivestax.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.org.reactivestax.dto.MessageDTO;
import io.org.reactivestax.service.MessageService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.mockito.Mockito.when;


@WebMvcTest(OTPController.class)
class MessageControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MessageService messageService;


    private MessageDTO messageDTO;

    @BeforeEach
    void setUp(){
        messageDTO = new MessageDTO();
        messageDTO.setMessage("Testing message");
        messageDTO.setClientId(1L);
        messageDTO.setRecipientContactNumber("6478181379");
        messageDTO.setRecipientEmailAddress("abhaynimavat2410@gmail.com");
    }

    @Test
    void testSendToJmsWithSmsMethod() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = objectMapper.writeValueAsString(messageDTO);
        when(messageService.sendMessageToJMS(messageDTO,"sms")).thenReturn(messageDTO);
        mockMvc.perform(post("/api/v1/ems")
                .content(messageJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(messageJson));

    }




}