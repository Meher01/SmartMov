package com.SmartMov.dto;

import com.SmartMov.entity.ResourceType;

public class ResourceResponse {

    private Long id;
    private String title;
    private ResourceType type;
    private String url;
    private String content;
    private Long targetId;
    private String fileName;
    private String filePath;
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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