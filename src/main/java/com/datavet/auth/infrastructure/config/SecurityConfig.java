package com.datavet.auth.infrastructure.config;

import com.datavet.auth.infrastructure.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Endpoints públicos — sin autenticación
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/resend-verification").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/activate-account").permitAll()

                        // Onboarding — requiere JWT temporal (validado en el controller)
                        .requestMatchers(HttpMethod.PATCH, "/clinic/*/complete-setup").authenticated()

                        // Employees — solo CLINIC_OWNER y CLINIC_ADMIN
                        .requestMatchers("/employees/**").hasAnyRole(
                                "CLINIC_OWNER", "CLINIC_ADMIN")

                        // Clinic — solo SUPER_ADMIN puede crear directamente
                        .requestMatchers(HttpMethod.POST, "/clinic").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clinic/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/clinic/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER")
                        .requestMatchers(HttpMethod.GET, "/clinic/**").authenticated()

                        // Resto de endpoints — autenticación requerida
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}