package com.github.tornaia.mybadcast.server.extension.api;

import com.github.tornaia.mybadcast.server.extension.EventType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PushEventRestRequest {

    private String userid;
    private EventType type;
    private String videoid;
    private int currentTime;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder("PushEventRestRequest", ToStringStyle.JSON_STYLE)
                .append("PushEventRestRequest", "")
                .append("userid", userid)
                .append("type", type)
                .append("videoid", videoid)
                .append("currentTime", currentTime)
                .toString();
    }
}
