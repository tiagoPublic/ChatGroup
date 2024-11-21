package org.example.chatgoup.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;


@Controller
/*@RequiredArgsConstructor*/
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);



    @Autowired
    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    // metodo para  enviar msgs
    // MessageMapping -> diz qual é url que queremos usar para invocar o metodo sendMessages
    // SendoTo -> para qual queue queremos enviar

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        try {
            chatMessage.setTimestamp(LocalDateTime.now());
            logger.info("Mensagem recebida para salvar: {}", chatMessage.getContent());
            chatMessageRepository.save(chatMessage); // Salva a mensagem no MongoDB
        } catch (Exception e) {
            logger.error("Erro ao salvar a mensagem no MongoDB: {}", e.getMessage());
            e.printStackTrace();
        }
        return chatMessage;
    }

    // metodo para adicionar user
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ){
        // adiciona o username à sessão da websocket
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
