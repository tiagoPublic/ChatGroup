package org.example.chatgoup.chat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends MongoRepository<ChatUsers, String>{
    ChatUsers findByUsername(String username);
}
