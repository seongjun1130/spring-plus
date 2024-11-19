package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String password;
  private String nickName;

  @Enumerated(EnumType.STRING)
  private UserRole userRole;

  public User(String email, String password, String nickName, UserRole userRole) {
    this.email = email;
    this.password = password;
    this.userRole = userRole;
    this.nickName = nickName;
  }

  private User(Long id, String email, UserRole userRole) {
    this.id = id;
    this.email = email;
    this.userRole = userRole;
  }

  public static User fromAuthUser(AuthUser authUser) {
    return new User(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void updateRole(UserRole userRole) {
    this.userRole = userRole;
  }
}
