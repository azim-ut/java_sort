package com.example.server.scoket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.server.SortServiceFactory;
import com.example.server.bean.SortLog;
import com.example.server.bean.SortType;
import com.example.server.scoket.bean.IncomeMessage;
import com.google.gson.Gson;

public class StateSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(StateSocketHandler.class);

    private final Gson gson = new Gson();
    private final SortServiceFactory serviceFactory;
    private final Map<WebSocketSession, SortType> states = new ConcurrentHashMap<>();

    public StateSocketHandler(SortServiceFactory sortServiceFactory) {
        this.serviceFactory = sortServiceFactory;
    }

    public void sendState() {
        states.forEach((session, sortType) -> {
            try {
                SortLog log = serviceFactory.get(sortType).pollLog(sortType.name());
                TextMessage msg = new TextMessage(gson.toJson(log));
                session.sendMessage(msg);
            } catch (IOException | IllegalStateException e) {
                logger.error(e.getMessage());
                states.remove(session);
            } catch (NullPointerException e) {
                logger.info("Sort {}. Queue is empty. " + e.getMessage(), sortType);
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        IncomeMessage income = gson.fromJson(message.getPayload(), IncomeMessage.class);
        SortType sortType = income.getSortType();
        String target = sortType.name();
        if (income.isPlay()) {
            states.put(session, sortType);
            serviceFactory.get(sortType).sort(target);
        }
        if (income.isStop()) {
            states.remove(session);
            serviceFactory.get(sortType).drop(target);
        }
    }
}
