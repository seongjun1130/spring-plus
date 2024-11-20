package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.CustomUserDetails;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;

  @PostMapping("/todos")
  public ResponseEntity<TodoSaveResponse> saveTodo(
          @AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody TodoSaveRequest todoSaveRequest) {
    return ResponseEntity.ok(todoService.saveTodo(customUserDetails, todoSaveRequest));
  }

  @GetMapping("/todos")
  public ResponseEntity<Page<TodoResponse>> getTodos(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String weather,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {
    return ResponseEntity.ok(todoService.getTodos(page, size, weather,startDate,endDate));
  }

  @GetMapping("/todos/search")
  public ResponseEntity<Page<TodoSearchResponse>> getTodosAll(TodoSearchCondition condition, Pageable pageable){
    return ResponseEntity.ok(todoService.getTodosAll(condition,pageable));
  }

  @GetMapping("/todos/{todoId}")
  public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
    return ResponseEntity.ok(todoService.getTodo(todoId));
  }
}
