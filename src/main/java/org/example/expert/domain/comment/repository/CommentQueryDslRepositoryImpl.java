package org.example.expert.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.entity.QComment;
import org.springframework.data.repository.query.Param;

@RequiredArgsConstructor
public class CommentQueryDslRepositoryImpl implements CommentQueryDslRepository {
  private final JPAQueryFactory queryFactory;
  private final QComment comment = QComment.comment;

  @Override
  public List<Comment> findByTodoIdWithUser(@Param("todoId") Long todoId) {
    return queryFactory
        .selectFrom(comment)
        .leftJoin(comment.user)
        .fetchJoin()
        .where(comment.todo.id.eq(todoId))
        .stream()
        .toList();
  }
}
