package com.finance.dashboard.config;

import com.finance.dashboard.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF (for APIs)
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session (JWT)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // ✅ Public APIs + Swagger (ADDED 🔥)
                .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // Viewer (read-only)
                .requestMatchers(HttpMethod.GET, "/api/records/**")
                    .hasAnyRole("VIEWER", "ANALYST", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/dashboard/**")
                    .hasAnyRole("VIEWER", "ANALYST", "ADMIN")

                // Analyst (read + create/update)
                .requestMatchers(HttpMethod.POST, "/api/records/**")
                    .hasAnyRole("ANALYST", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/records/**")
                    .hasAnyRole("ANALYST", "ADMIN")

                // Admin only
                .requestMatchers(HttpMethod.DELETE, "/api/records/**")
                    .hasRole("ADMIN")

                .requestMatchers("/api/users/**")
                    .hasRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Add JWT filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}