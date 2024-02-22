package fr.arpicode.socketclash.config;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import fr.arpicode.socketclash.chat.ChatMessage;
import fr.arpicode.socketclash.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null) {
            String username = (String) sessionAttributes.get("username");

            if (username != null) {
                log.info("User Disconnected : {}", username);

                ChatMessage chatMessage = ChatMessage.builder()
                        .type(MessageType.LEAVE)
                        .sender(username)
                        .build();

                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }

}
