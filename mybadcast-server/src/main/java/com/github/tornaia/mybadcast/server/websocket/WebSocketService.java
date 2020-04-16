package com.github.tornaia.mybadcast.server.websocket;

import com.github.tornaia.mybadcast.server.extension.EventType;
import com.github.tornaia.mybadcast.server.websocket.api.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class WebSocketService {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketService.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConcurrentMap<String, WebSocketMessage> lastMessages;

    @Autowired
    public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.lastMessages = new ConcurrentHashMap<>();
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        MessageHeaders headers = message.getHeaders();
        String destination = headers.get("simpDestination", String.class);
        if (destination == null) {
            LOG.warn("Unexpected destination: null");
            return;
        }
        LOG.info("Client subscribed: {}", destination);

        String[] splittedDestination = destination.split("/topic/events/");
        if (splittedDestination.length != 2) {
            LOG.warn("Unexpected destination: {}", destination);
            return;
        }

        String userid = splittedDestination[1];
        WebSocketMessage lastMessage = lastMessages.get(userid);
        if (lastMessage == null) {
            LOG.warn("No last websocket message for this userid: {}", userid);
            return;
        }

        Principal principal = event.getUser();
        if (principal == null) {
            LOG.warn("Principal not found, event: {}", event);
            return;
        }

        String user = principal.getName();
        LOG.info("Push initial message, user: {}, destination: {}, message: {}", user, destination, lastMessage);
        this.simpMessagingTemplate.convertAndSendToUser(user, destination, lastMessage);
    }

    public void send(@DestinationVariable("userid") String userid, WebSocketMessage webSocketMessage) {
        String destination = "/topic/events/" + userid;
        LOG.info("Push message, destination: {}, message: {}", destination, webSocketMessage);
        lastMessages.put(userid, webSocketMessage);
        if (webSocketMessage.getType() != EventType.PLAYING) {
            this.simpMessagingTemplate.convertAndSend(destination, webSocketMessage);
        }
    }
}