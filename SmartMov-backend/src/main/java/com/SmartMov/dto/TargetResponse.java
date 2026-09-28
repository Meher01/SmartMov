package com.SmartMov.dto;
import java.time.LocalDate;

public class TargetResponse {

    private Long id;
    private String title;
    
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
    public String getDescription() {
      return description;
    }
    public void setDescription(String description) {
      this.description = description;
    }
    public String getCategory() {
      return category;
    }
    public void setCategory(String category) {
      this.category = category;
    }
    public Integer getDailyMinutes() {
      return dailyMinutes;
    }
    public void setDailyMinutes(Integer dailyMinutes) {
      this.dailyMinutes = dailyMinutes;
    }
    public Integer getCompletedMinutes() {
      return completedMinutes;
    }
    public void setCompletedMinutes(Integer completedMinutes) {
      this.completedMinutes = completedMinutes;
    }
    public Boolean getCompletedToday() {
      return completedToday;
    }
    public void setCompletedToday(Boolean completedToday) {
      this.completedToday = completedToday;
    }
public LocalDate getProgressDate() {
    return progressDate;
}

public void setProgressDate(LocalDate progressDate) {
    this.progressDate = progressDate;
}
    private String description;
    private String category;
    private Integer dailyMinutes;
    private Integer completedMinutes;
    private Boolean completedToday;
    private LocalDate progressDate;

}