package com.SmartMov.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.SmartMov.entity.ResourceType;

public class CreateResourceRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Type is required")
    private ResourceType type;

    private String url;

    private String content;

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @NotNull(message = "Target ID is required")
    private Long targetId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ResourceType getType() {
    return type;
}

public void setType(ResourceType type) {
    this.type = type;
}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
    return content;
}

    public Long getTargetId() {
        return targetId;
    }

    public void setContent(String content) {
    this.content = content;
}

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}