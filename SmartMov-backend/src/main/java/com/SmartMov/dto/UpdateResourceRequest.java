package com.SmartMov.dto;

import jakarta.validation.constraints.NotBlank;
import com.SmartMov.entity.ResourceType;
import jakarta.validation.constraints.NotNull;

public class UpdateResourceRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Type is required")
    private ResourceType type;

    private String url;

    private String content;

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

    public String getContent() {
    return content;
}

public void setContent(String content) {
    this.content = content;
}

    public void setUrl(String url) {
        this.url = url;
    }
}