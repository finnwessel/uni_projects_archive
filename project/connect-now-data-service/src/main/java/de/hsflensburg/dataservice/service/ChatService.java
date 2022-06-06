package de.hsflensburg.dataservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hsflensburg.connectnow.messages.ChatInfoAmqp;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final static String EXCHANGE = "communication-service";
    private final static String ROUTING_KEY = "service.communication.chat-info";

    private final RabbitTemplate rabbitTemplate;

    public void updateChat(String requestId, String owner, Collection<String> participants,
                           Collection<String> receivers, boolean active) throws JsonProcessingException {
        ChatInfoAmqp chatInfoAmqp = new ChatInfoAmqp(requestId, owner, participants, receivers, active);

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, chatInfoAmqp);
    }

}
