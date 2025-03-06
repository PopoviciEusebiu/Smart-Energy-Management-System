package chat.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadNotification {

    private String senderId;

    private String receiverId;
}