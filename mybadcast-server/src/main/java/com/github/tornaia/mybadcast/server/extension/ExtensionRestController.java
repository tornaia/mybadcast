package com.github.tornaia.mybadcast.server.extension;

import com.github.tornaia.mybadcast.server.extension.api.PushEventRestRequest;
import com.github.tornaia.mybadcast.server.extension.api.PushEventRestResponse;
import com.github.tornaia.mybadcast.server.http.HttpUtils;
import com.github.tornaia.mybadcast.server.websocket.WebSocketService;
import com.github.tornaia.mybadcast.server.websocket.api.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/extension/v1")
public class ExtensionRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionRestController.class);

    private final WebSocketService webSocketService;

    @Autowired
    public ExtensionRestController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    // curl -H "Content-Type: application/json" -X POST http://localhost:8080/api/extension/v1/push -d "{\"userid\":\"123456\",\"type\":\"CHANGE\",\"videoid\":\"bY_PKlLQkL0\",\"currentTime\":0}"
    @PostMapping("/push")
    public ResponseEntity<PushEventRestResponse> push(@RequestBody PushEventRestRequest pushEventRestRequest, @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent, HttpServletRequest httpServletRequest) {
        String userid = pushEventRestRequest.getUserid();
        WebSocketMessage webSocketMessage = new WebSocketMessage(pushEventRestRequest.getType(), pushEventRestRequest.getVideoid(), pushEventRestRequest.getCurrentTime());
        String source = HttpUtils.getIpAddress(httpServletRequest);
        LOG.info("Push message, userid: {}, webSocketMessage: {}, source: {}", userid, webSocketMessage, source);
        webSocketService.send(userid, webSocketMessage);
        return ResponseEntity.ok(PushEventRestResponse.ok());
    }
}
