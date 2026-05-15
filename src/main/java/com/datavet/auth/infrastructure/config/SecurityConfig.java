package com.datavet.auth.infrastructure.config;

import com.datavet.auth.infrastructure.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())

                // CSRF desactivado — SameSite=Strict en las cookies ya nos protege
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Preflight OPTIONS — siempre libre
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Endpoints públicos
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/resend-verification").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/activate-account").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()

                        // Onboarding — requiere JWT temporal
                        .requestMatchers(HttpMethod.PATCH, "/clinic/*/complete-setup").authenticated()

                        // Agenda
                        .requestMatchers("/appointments/**").authenticated()

                        // Productos — lectura libre para cualquier rol; escritura restringida
                        .requestMatchers(HttpMethod.GET,    "/product/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/product/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER", "CLINIC_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/product/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER", "CLINIC_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/product/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER", "CLINIC_ADMIN")

                        // Movimientos de inventario — cualquier empleado autenticado puede registrar
                        .requestMatchers("/product-movement/**").authenticated()

                        // Employees — solo CLINIC_OWNER y CLINIC_ADMIN
                        .requestMatchers("/employees/**").hasAnyRole(
                                "CLINIC_OWNER", "CLINIC_ADMIN")

                        // Clinic
                        .requestMatchers(HttpMethod.POST,   "/clinic").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clinic/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER")
                        .requestMatchers(HttpMethod.PUT,    "/clinic/**").hasAnyRole(
                                "SUPER_ADMIN", "CLINIC_OWNER")
                        .requestMatchers(HttpMethod.GET,    "/clinic/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5258"));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));

        // CRÍTICO: debe ser true para que el navegador acepte las cookies
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}