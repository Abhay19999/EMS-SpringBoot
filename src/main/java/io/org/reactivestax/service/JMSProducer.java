package io.org.reactivestax.service;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JMSProducer {
    private final JmsTemplate jmsTemplate;

    public JMSProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    public void sendMessage(String message, String queueName) {
        jmsTemplate.send(queueName, session -> {
            log.info("Message sent successfully");
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);
            return textMessage;
        });
    }
}
