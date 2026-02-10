package com.trinhkiendat.TrinhKienDat.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.trinhkiendat.TrinhKienDat.config.RabbitConfig;

@Controller
public class ChatController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {
        // forward message to RabbitMQ for distribution
        rabbitTemplate.convertAndSend(RabbitConfig.CHAT_EXCHANGE, RabbitConfig.CHAT_ROUTING, message);
    }
}
