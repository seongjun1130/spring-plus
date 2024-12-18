package org.example.expert.config.security;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Not Found " + email));
    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().toString()));
    return new CustomUserDetails(
        user.getNickname(),user.getId(), user.getEmail(), user.getPassword(), authorities);
  }
}
