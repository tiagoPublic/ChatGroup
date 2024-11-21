package org.example.chatgoup.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private ChatUserRepository chatUserRepository;

    public boolean isUsernameAvailable(String username) {
        return chatUserRepository.findByUsername(username) == null;
    }

    public void registerUser(ChatUsers user) {
        chatUserRepository.save(user);
    }

    public boolean authenticateUser(String username, String password) {
        ChatUsers user = chatUserRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found: " + username);
            return false;
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Invalid password for user: " + username);
            return false;
        }
        return true;
    }
}

