package com.SmartMov.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public class UpdateTargetRequest {

@NotBlank(message = "Title is required")
private String title;

private String description;

@NotBlank(message = "Category is required")
private String category;

@Min(value = 1, message = "Daily minutes must be at least 1")
private Integer dailyMinutes;

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


}
