package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentQueryDslRepository {
    List<Comment> findByTodoIdWithUser(Long id);
}
