package com.github.tornaia.mybadcast.server.websocket.api;

import com.github.tornaia.mybadcast.server.extension.EventType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WebSocketMessage {

    private final EventType type;
    private final String videoid;
    private final int currentTime;

    public WebSocketMessage(EventType type, String videoid, int currentTime) {
        this.type = type;
        this.videoid = videoid;
        this.currentTime = currentTime;
    }

    public EventType getType() {
        return type;
    }

    public String getVideoid() {
        return videoid;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder("WebSocketMessage", ToStringStyle.JSON_STYLE)
                .append("WebSocketMessage", "")
                .append("type", type)
                .append("videoid", videoid)
                .append("currentTime", currentTime)
                .toString();
    }
}
