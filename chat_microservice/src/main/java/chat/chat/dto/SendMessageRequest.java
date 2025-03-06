package chat.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {

    private String senderId;

    private String receiverId;

    private String content;

}