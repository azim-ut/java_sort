package com.example.server.scoket;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.server.SortServiceFactory;

@Configuration
@EnableScheduling
@EnableWebSocket
public class SocketConfiguration implements WebSocketConfigurer {
    private final StateSocketHandler handler;

    public SocketConfiguration(SortServiceFactory serviceFactory) {
        this.handler = new StateSocketHandler(serviceFactory);
    }

    @Scheduled(fixedRate = 10)
    private void sendStates(){
        handler.sendState();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/websocket").setAllowedOrigins("*");
    }
}
