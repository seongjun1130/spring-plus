package org.example.expert.domain.todo.repository;

import static io.jsonwebtoken.lang.Strings.hasText;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoQueryDslRepositoryImpl implements TodoQueryDslRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<TodoSearchResponse> findAllByTodo(TodoSearchCondition condition, Pageable pageable) {
    QTodo todo = QTodo.todo;
    QComment comment = QComment.comment;
    QManager manager = QManager.manager;

    BooleanExpression conditionExpression = buildConditionExpression(condition);

    QueryResults<TodoSearchResponse> results =
        queryFactory
            .select(
                new QTodoSearchResponse(
                    todo.title,
                    manager.countDistinct().as("managerCount"),
                    comment.count().as("commentCount")))
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(todo.comments, comment)
            .where(conditionExpression)
            .groupBy(todo.id)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
    List<TodoSearchResponse> contents = results.getResults();
    long total = results.getTotal();
    return new PageImpl<>(contents, pageable, total);
  }

  private BooleanExpression buildConditionExpression(TodoSearchCondition condition) {
    return Objects.requireNonNull(hasKeyWord(condition.getKeyword()))
        .and(hasDateRange(condition.getStartDate(), condition.getEndDate()))
        .and(hasManagerNickName(condition.getManagerNickname()));
  }

  private BooleanExpression hasKeyWord(String keyword) {
    return hasText(keyword) ? QTodo.todo.title.containsIgnoreCase(keyword) : null;
  }

  private BooleanExpression hasDateRange(LocalDate start, LocalDate end) {
    // QueryDSL 에서 LocalDateTime -> LocalDate 로 변환
    DateExpression<LocalDate> createAtDate =
        Expressions.dateTemplate(
            // SQL 변환: LocalDateTime -> LocalDate
            LocalDate.class, "cast({0} as date)", QTodo.todo.createdAt);
    if (start != null && end != null) {
      return createAtDate.between(start, end);
    }
    // 시작일자 이후 일정들 검색
    else if (start != null) {
      return createAtDate.goe(start);
    }
    // 종료일자 이전 일정들 검색
    else if (end != null) {
      return createAtDate.loe(end);
    }
    return null;
  }

  private BooleanExpression hasManagerNickName(String managerNickName) {
    return hasText(managerNickName)
        ? QManager.manager.user.nickname.containsIgnoreCase(managerNickName)
        : null;
  }
}
