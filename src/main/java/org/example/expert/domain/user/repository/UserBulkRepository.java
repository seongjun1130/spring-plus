package org.example.expert.domain.user.repository;

import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBulkRepository {
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public void saveAll(List<User> Users) {
    jdbcTemplate.batchUpdate(
        "insert into users(email,password,nickname,user_role,created_at,modified_at) values (?, ?, ?, ?,?,?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            User user = Users.get(i);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNickname());
            ps.setString(4, user.getUserRole().toString());
            ps.setString(5, LocalDateTime.now().toString());
            ps.setString(6, LocalDateTime.now().toString());
          }

          @Override
          public int getBatchSize() {
            return Users.size();
          }
        });
  }
}
