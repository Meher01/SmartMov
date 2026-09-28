package com.SmartMov.dto;

import jakarta.validation.constraints.NotNull;

public class FinishSessionRequest {

    @NotNull
    private Long sessionId;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}