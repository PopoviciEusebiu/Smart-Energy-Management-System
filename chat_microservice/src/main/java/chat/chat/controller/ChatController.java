package chat.chat.controller;

import chat.chat.dto.MessageReadNotification;
import chat.chat.dto.SendMessageRequest;
import chat.chat.dto.TypingNotificationRequest;
import chat.chat.model.ChatMessage;
import chat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    public void handleSendMessage(@Payload SendMessageRequest request) {
        ChatMessage chatMessage = chatService.sendMessage(request);

        messagingTemplate.convertAndSend(
                "/queue/messages/" + request.getSenderId() + "/" + request.getReceiverId(),
                chatMessage
        );

        System.out.println("Message sent to /queue/messages/" + request.getSenderId() + "/" + request.getReceiverId());

    }

    @MessageMapping("/markMessageAsRead")
    public void markMessageAsRead(@Payload MessageReadNotification notification) {
        messagingTemplate.convertAndSend(
                "/queue/notifications/" + notification.getReceiverId(),
                notification
        );

        System.out.println("Notification sent that message was read: " + notification);
    }



    @MessageMapping("/typingNotification")
    public void handleTypingNotification(@Payload TypingNotificationRequest request){
        System.out.println("Received typing notification for receiverId: " + request.getReceiverId());

        System.out.println("/queue/typing/" + request.getSenderId());
        messagingTemplate.convertAndSend(
                "/queue/typing/" + request.getReceiverId(),
                request
        );

        System.out.println("Notification for typing sent");

    }
}
