package chat.chat.service;

import chat.chat.dto.MessageReadNotification;
import chat.chat.dto.SendMessageRequest;
import chat.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessage sendMessage(SendMessageRequest request){
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setSenderId(Integer.parseInt(request.getSenderId()));
        chatMessage.setReceiverId(Integer.parseInt(request.getReceiverId()));
        chatMessage.setContent(request.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setRead(Boolean.FALSE);

        log.info("Message sent to user {}: {}", request.getReceiverId(), chatMessage);
        return chatMessage;
    }

}
