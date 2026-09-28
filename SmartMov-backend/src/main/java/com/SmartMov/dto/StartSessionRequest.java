package com.SmartMov.dto;

import jakarta.validation.constraints.NotNull;

public class StartSessionRequest {

    @NotNull
    private Long targetId;

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}