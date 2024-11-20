package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoSearchCondition {
  @NotBlank private String keyword;
  private String managerNickname;
  private LocalDate startDate;
  private LocalDate endDate;

  @Builder
  public TodoSearchCondition(
      String keyword, String managerNickname, LocalDate startDate, LocalDate endDate) {
    this.keyword = keyword;
    this.managerNickname = managerNickname;
    this.startDate = startDate;
    this.endDate = endDate;
  }
}
