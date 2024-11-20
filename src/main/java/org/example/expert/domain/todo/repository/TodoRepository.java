package org.example.expert.domain.todo.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoQueryDslRepository {

//  @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
//  Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

  @Query("SELECT t FROM Todo t " + "LEFT JOIN t.user " + "WHERE t.id = :todoId")
  Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

  @Query(
      "SELECT t FROM Todo t "
          + "WHERE (:weather IS NULL OR t.weather = :weather) "
          + "AND (:startDate IS NULL OR :endDate IS NULL OR DATE(t.modifiedAt) BETWEEN :startDate AND :endDate) "
          + "ORDER BY t.modifiedAt DESC")
  Page<Todo> findAllByOrderByModifiedAtWeatherORDate(
      @Param("weather") String weather,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);
}
