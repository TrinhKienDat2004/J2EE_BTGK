package com.trinhkiendat.TrinhKienDat.chat;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.trinhkiendat.TrinhKienDat.config.RabbitConfig;

@Component
public class ChatMessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitConfig.CHAT_QUEUE)
    public void receive(ChatMessage message) {
        // forward to all websocket subscribers
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
