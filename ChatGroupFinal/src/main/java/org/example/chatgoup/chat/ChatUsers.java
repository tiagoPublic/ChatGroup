package org.example.chatgoup.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users") //mongo

public class ChatUsers {
    @Id
    private String id;
    private String username;
    private String password;
}
