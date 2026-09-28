package com.SmartMov.entity;


import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "target")
public class Target {
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String title;
private String description;
private String category;
private Integer dailyMinutes;
private Integer completedMinutes;
private Boolean completedToday;
private LocalDate progressDate;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;

  public Target() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public User getUser() {
    return user;
}

public void setUser(User user) {
    this.user = user;
}

public LocalDate getProgressDate() {
    return progressDate;
}

public void setProgressDate(LocalDate progressDate) {
    this.progressDate = progressDate;
}
}