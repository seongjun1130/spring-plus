package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoSearchResponse {
  private String title;
  private Long MangerCount;
  private Long commentCount;

  @Builder
  @QueryProjection
  public TodoSearchResponse(String title, Long mangerCount, Long commentCount) {
    this.title = title;
    this.MangerCount = mangerCount;
    this.commentCount = commentCount;
  }
}
