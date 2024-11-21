package org.example.chatgoup.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker

// implementar uma interface
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // queremos adicionar um novo stomp e um novo endpoint à nossa websocket configuraion
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    // adicionar os prefixos de destino da app
    // todas as mensagens enviadas pelos clientes com destino /app serão roteadas para os métodos com @MessageMapping no chatController.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}
