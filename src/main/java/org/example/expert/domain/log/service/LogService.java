package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.enums.ActionStatus;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {
  private final LogRepository logRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveLog(Long requesterId, Long targetId, ActionStatus status, String message) {
    Log log = new Log(requesterId, targetId, status, message);
    logRepository.save(log);
  }
}