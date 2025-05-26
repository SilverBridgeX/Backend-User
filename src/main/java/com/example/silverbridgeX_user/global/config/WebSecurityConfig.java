package com.example.silverbridgeX_user.global.config;

import com.example.silverbridgeX_user.user.jwt.AuthCreationFilter;
import com.example.silverbridgeX_user.user.jwt.JwtValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final AuthCreationFilter authCreationFilter;
    private final JwtValidationFilter jwtValidationFilter;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authHttp -> authHttp
                        .requestMatchers(
                                "/health", // health check
                                "/check/**",
                                "/swagger-ui/**", // swagger
                                "/v3/api-docs/**",
                                "/auth/verify"
                        )
                        .permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(authCreationFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtValidationFilter, AuthCreationFilter.class);

        return http.build();
    }
}