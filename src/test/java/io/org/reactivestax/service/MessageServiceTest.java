package io.org.reactivestax.service;

import io.org.reactivestax.domain.NotificationMessage;
import io.org.reactivestax.dto.MessageDTO;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.ContactRepository;
import io.org.reactivestax.repository.NotificationMessageRepository;
import io.org.reactivestax.type.exception.ClientNotRegisteredException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @MockitoBean
    private ContactRepository contactRepository;

    @MockitoBean
    private ClientRepository clientRepository;

    @MockitoBean
    private JMSProducer jmsProducer;

    @MockitoBean
    private NotificationMessageRepository notificationMessageRepository;



    @Test
    void testSendMessageToJMSBySms(){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setClientId(1L);
        messageDTO.setMessage("test message");
        messageDTO.setRecipientContactNumber("6478181379");
        messageDTO.setRecipientEmailAddress("abhaynimavat2410@gmail.com");
        when(clientRepository.existsById(1L)).thenReturn(true);

        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageId("1234");
        notificationMessage.setClientId(1L);
        notificationMessage.setRawMessage("test message");
        notificationMessage.setPhoneNumber("6478181379");
        notificationMessage.setEmail("abhaynimavat2410@gmail.com");
        when(notificationMessageRepository.save(any(NotificationMessage.class))).thenReturn(notificationMessage);
        MessageDTO result = messageService.sendMessageToJMS(messageDTO,"sms");
        verify(notificationMessageRepository,times(1)).save(any(NotificationMessage.class));
        verify(jmsProducer,times(1)).sendMessage(eq("1234"),eq("emsService"));

        assertNotNull(result);
        assertEquals("test message", result.getMessage());
        assertEquals("6478181379", result.getRecipientContactNumber());
    }
    @Test
    void testSendMessageToJMSByEmail(){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setClientId(1L);
        messageDTO.setMessage("test message");
        messageDTO.setRecipientContactNumber("6478181379");
        messageDTO.setRecipientEmailAddress("abhaynimavat2410@gmail.com");
        when(clientRepository.existsById(1L)).thenReturn(true);

        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageId("1234");
        notificationMessage.setClientId(1L);
        notificationMessage.setRawMessage("test message");
        notificationMessage.setPhoneNumber("6478181379");
        notificationMessage.setEmail("abhaynimavat2410@gmail.com");
        when(notificationMessageRepository.save(any(NotificationMessage.class))).thenReturn(notificationMessage);
        MessageDTO result = messageService.sendMessageToJMS(messageDTO,"email");
        verify(notificationMessageRepository,times(1)).save(any(NotificationMessage.class));
        verify(jmsProducer,times(1)).sendMessage(eq("1234"),eq("emsService"));

        assertNotNull(result);
        assertEquals("test message", result.getMessage());
        assertEquals("6478181379", result.getRecipientContactNumber());
    }
    @Test
    void testSendMessageToJMSByCall(){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setClientId(1L);
        messageDTO.setMessage("test message");
        messageDTO.setRecipientContactNumber("6478181379");
        messageDTO.setRecipientEmailAddress("abhaynimavat2410@gmail.com");
        when(clientRepository.existsById(1L)).thenReturn(true);

        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageId("1234");
        notificationMessage.setClientId(1L);
        notificationMessage.setRawMessage("test message");
        notificationMessage.setPhoneNumber("6478181379");
        notificationMessage.setEmail("abhaynimavat2410@gmail.com");
        when(notificationMessageRepository.save(any(NotificationMessage.class))).thenReturn(notificationMessage);
        MessageDTO result = messageService.sendMessageToJMS(messageDTO,"call");
        verify(notificationMessageRepository,times(1)).save(any(NotificationMessage.class));
        verify(jmsProducer,times(1)).sendMessage(eq("1234"),eq("emsService"));

        assertNotNull(result);
        assertEquals("test message", result.getMessage());
        assertEquals("6478181379", result.getRecipientContactNumber());
    }
    @Test
    void testSendMessageWhenClientNotRegistered(){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setClientId(1L);
        when(clientRepository.existsById(1L)).thenReturn(false);
        assertThrows(ClientNotRegisteredException.class,()->messageService.sendMessageToJMS(messageDTO,anyString()));
    }


}