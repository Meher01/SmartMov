package com.SmartMov.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Resource {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String title;

@Enumerated(EnumType.STRING)
private ResourceType type;

    private String url;

    private String content;

    private String fileName;

    private String filePath;

    private String contentType;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private Target target;

    public Resource() {
    }

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

    public Target getTarget() {
        return target;
    }

    public void setContent(String content) {
    this.content = content;
}

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

    public void setTarget(Target target) {
        this.target = target;
    }

}