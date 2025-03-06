package chat.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkAsReadRequest {

    private String senderId;

    private String receiverId;

}