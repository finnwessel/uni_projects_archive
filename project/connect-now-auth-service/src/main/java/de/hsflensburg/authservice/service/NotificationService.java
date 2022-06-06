package de.hsflensburg.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hsflensburg.connectnow.messages.PasswordResetEmailAmqp;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final static String EXCHANGE = "communication-service";
    private final static String ROUTING_KEY = "service.communication.mail";

    private final RabbitTemplate rabbitTemplate;

    public void queuePasswordResetEmail(String firstName, String lastName,
                                        String email, String subject, String resetUrl) throws JsonProcessingException {
        PasswordResetEmailAmqp resetEmailAmqp = new PasswordResetEmailAmqp(email, firstName, lastName, subject, resetUrl);

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, resetEmailAmqp);
    }
}
