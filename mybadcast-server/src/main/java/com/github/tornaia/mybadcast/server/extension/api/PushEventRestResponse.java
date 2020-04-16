package com.github.tornaia.mybadcast.server.extension.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PushEventRestResponse {

    public enum Status {
        OK,
        TEMPORARY_FAILURE,
        UNKNOWN_FAILURE
    }

    private final Status status;

    private PushEventRestResponse(Status status) {
        this.status = status;
    }

    public static PushEventRestResponse ok() {
        return new PushEventRestResponse(Status.OK);
    }

    public static PushEventRestResponse temporaryFailure() {
        return new PushEventRestResponse(Status.TEMPORARY_FAILURE);
    }

    public static PushEventRestResponse unknownFailure() {
        return new PushEventRestResponse(Status.UNKNOWN_FAILURE);
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder("EditUserRestResponse", ToStringStyle.JSON_STYLE)
                .append("EditUserRestResponse", "")
                .append("status", status)
                .toString();
    }
}
