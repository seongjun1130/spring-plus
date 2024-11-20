package org.example.expert.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserReadResponse {
  private final Long id;
  private final String email;
  private final String nickname;

  @Builder
  public UserReadResponse(Long id, String email, String nickname) {
    this.id = id;
    this.email = email;
    this.nickname = nickname;
  }
}
