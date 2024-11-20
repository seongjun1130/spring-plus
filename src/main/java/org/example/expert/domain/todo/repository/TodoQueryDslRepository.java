package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoQueryDslRepository {
    Page<TodoSearchResponse> findAllByTodo(TodoSearchCondition condition, Pageable pageable);
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
