package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.log.enums.ActionStatus;

@Entity
@Getter
@Table(name = "log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long requesterId;
  private Long targetId;
  private String message;

  @Enumerated(EnumType.STRING)
  private ActionStatus status;

  private final LocalDateTime createdAt = LocalDateTime.now();

  @Builder
  public Log(Long requesterId, Long targetId, ActionStatus status, String message) {
    this.requesterId = requesterId;
    this.targetId = targetId;
    this.status = status;
    this.message = message;
  }
}
