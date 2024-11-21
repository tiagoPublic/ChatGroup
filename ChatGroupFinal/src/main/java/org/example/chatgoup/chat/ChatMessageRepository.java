package org.example.chatgoup.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository  extends MongoRepository<ChatMessage, String> {
}
