package io.org.reactivestax.service;


import io.org.reactivestax.domain.NotificationMessage;
import io.org.reactivestax.dto.MessageDTO;
import io.org.reactivestax.dto.UserLoginDTO;
import io.org.reactivestax.repository.ClientRepository;
import io.org.reactivestax.repository.ContactRepository;
import io.org.reactivestax.repository.NotificationMessageRepository;
import io.org.reactivestax.type.enums.DeliveryMethodEnum;
import io.org.reactivestax.type.enums.MessageStatus;
import io.org.reactivestax.type.exception.ClientNotRegisteredException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MessageService {

    @Autowired
    private JMSProducer jmsProducer;

    @Value("${jms.queue}")
    private String jmsQueue;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public MessageDTO sendMessageToJMS(MessageDTO messageDTO,String contactMethod){
//        if(clientRepository.existsById(messageDTO.getClientId())) {
            NotificationMessage notificationMessage = convertToEntity(messageDTO);
            if(contactMethod.equals("sms")) {
                notificationMessage.setDeliveryMethod(DeliveryMethodEnum.SMS);
            } else if (contactMethod.equals("email")) {
                notificationMessage.setDeliveryMethod(DeliveryMethodEnum.EMAIL);
            }else{
                notificationMessage.setDeliveryMethod(DeliveryMethodEnum.CALL);
            }
            NotificationMessage message = notificationMessageRepository.save(notificationMessage);
            jmsProducer.sendMessage(message.getMessageId(),jmsQueue);
            return convertToDTO(message);
//        }else{
//           throw new ClientNotRegisteredException("Client is not registered");
//        }
    }
//    public String sendMessageForExternalAPI()

    private MessageDTO convertToDTO(NotificationMessage notificationMessage) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(notificationMessage.getRawMessage());
        messageDTO.setClientId(notificationMessage.getClientId());
        messageDTO.setRecipientContactNumber(notificationMessage.getPhoneNumber());
        messageDTO.setRecipientEmailAddress(notificationMessage.getEmail());
        return messageDTO;
    }

    private NotificationMessage convertToEntity(MessageDTO messageDTO) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setClientId(messageDTO.getClientId());
        notificationMessage.setRawMessage(messageDTO.getMessage());
        notificationMessage.setCreatedAt(LocalDateTime.now());
        notificationMessage.setPhoneNumber(messageDTO.getRecipientContactNumber());
        notificationMessage.setEmail(messageDTO.getRecipientEmailAddress());
        notificationMessage.setMessageStatus(MessageStatus.NOT_PROCESSED);
        return notificationMessage;
    }

    public UserLoginDTO sendTokenToJMS(UserLoginDTO userLoginDTO, String sms) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setRawMessage(userLoginDTO.getMessage());
        notificationMessage.setDeliveryMethod(DeliveryMethodEnum.SMS);
        notificationMessage.setPhoneNumber(userLoginDTO.getPhoneNumber());
       notificationMessageRepository.save(notificationMessage);
        jmsProducer.sendMessage(notificationMessage.getMessageId(),jmsQueue);
        return null;
    }
}
