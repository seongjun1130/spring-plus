package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.config.security.CustomUserDetails;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;

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
  private String nickname;

  @Enumerated(EnumType.STRING)
  private UserRole userRole;

  public User(String email, String password, String nickname, UserRole userRole) {
    this.email = email;
    this.password = password;
    this.userRole = userRole;
    this.nickname = nickname;
  }

  private User(Long id, String email, UserRole userRole) {
    this.id = id;
    this.email = email;
    this.userRole = userRole;
  }

  public static User fromAuthUser(CustomUserDetails customUserDetails) {
    // CustomUserDetails 에서 권한을 추출.
    Collection<? extends GrantedAuthority> authoritiesCollection =
        customUserDetails.getAuthorities();

    // Collection 을 List 로 변환
    List<GrantedAuthority> authorities = new ArrayList<>(authoritiesCollection);
    // 권한을 가져와서 UserRole Enum 으로 변환
    UserRole userRole =
        UserRole.valueOf(
            authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효한 역할이 없습니다.")));

    return new User(customUserDetails.getId(), customUserDetails.getEmail(), userRole);
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void updateRole(UserRole userRole) {
    this.userRole = userRole;
  }
}
