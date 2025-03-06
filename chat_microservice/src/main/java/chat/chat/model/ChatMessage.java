package chat.chat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ChatMessage {

    @JsonProperty("senderId")
    private Integer senderId;

    @JsonProperty("reciverId")
    private Integer receiverId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("read")
    private Boolean read = false;

    @JsonProperty("typing")
    private boolean typingNotification;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

}