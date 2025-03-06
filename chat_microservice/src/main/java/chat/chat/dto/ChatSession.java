package chat.chat.dto;

import chat.chat.model.ChatMessage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSession {
    private String sessionId;

    private String userId;

    private String adminId;

    private List<ChatMessage> messages;

}