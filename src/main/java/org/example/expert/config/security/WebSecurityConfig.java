package org.example.expert.config.security;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
  private final JwtFilter jwtFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
        .formLogin(form -> form.disable()) // Form Login 비활성화
        .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 비활성화
        .authorizeRequests(
            (authorizeRequests) ->
                authorizeRequests
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasAuthority("ADMIN")
                    .anyRequest()
                    .authenticated());
    http.sessionManagement(
        sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
