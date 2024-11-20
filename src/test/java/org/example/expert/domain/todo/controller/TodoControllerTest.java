package org.example.expert.domain.todo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import org.example.expert.config.security.CustomUserDetails;
import org.example.expert.config.security.CustomUserDetailsService;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TodoService todoService;

  @Test
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void todo_단건_조회에_성공한다() throws Exception {
    // given
    long todoId = 1L;
    String title = "title";
    UserResponse userResponse = new UserResponse(1L, "aaa@bbb.com");
    TodoResponse response =
        new TodoResponse(
            todoId,
            title,
            "contents",
            "Sunny",
            userResponse,
            LocalDateTime.now(),
            LocalDateTime.now());

    // when
    when(todoService.getTodo(todoId)).thenReturn(response);

    // then
    mockMvc
        .perform(get("/todos/{todoId}", todoId))
        .andExpect(status().isOk()) // 200 OK 상태 코드 기대
        .andExpect(jsonPath("$.id").value(todoId))
        .andExpect(jsonPath("$.title").value(title));
  }

  @Test
  @WithMockUser(
          username = "user",
          roles = {"USER"})
  void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
    // given
    long todoId = 1L;

    // when
    when(todoService.getTodo(todoId)).thenThrow(new InvalidRequestException("Todo not found"));

    // then
    mockMvc
        .perform(get("/todos/{todoId}", todoId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
        .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message").value("Todo not found"));
  }
}
