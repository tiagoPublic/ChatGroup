package org.example.chatgoup.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document //mongo

// contem os atributos das msgs
public class ChatMessage {
    @Id //mongo
    private String id;
    private String content;
    private String sender;
    private MessageType type;
    private LocalDateTime timestamp;
}
