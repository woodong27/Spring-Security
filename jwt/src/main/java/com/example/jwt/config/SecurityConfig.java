package com.example.jwt.config;

import com.example.jwt.jwt.handler.CustomLogoutSuccessHandler;
import com.example.jwt.jwt.filter.JwtFilter;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.jwt.filter.LoginFilter;
import com.example.jwt.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                // Custom 예외처리를 위해 /error 경로는 ignore
                .requestMatchers("/error");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // api 방식은 session stateless
                .csrf(AbstractHttpConfigurer::disable)

                // jwt을 쓰기 때문에 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // 동일하게 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // Cors 설정
                .cors((cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(List.of("Set-Cookie", JwtUtil.AUTH_HEADER));
                    return configuration;
                })))

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .deleteCookies(JwtUtil.AUTH_HEADER)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(logoutSuccessHandler))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/common", "/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // addFilterAt : 특정 필터를 선택해서 원하는 custom 필터로 대체
                .addFilterAt(new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), redisService), UsernamePasswordAuthenticationFilter.class)

                // addFilterBefore : 특정 필터의 전에 실행
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // LoginFilter의 인자로 사용하기 위해 Bean 등록
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
