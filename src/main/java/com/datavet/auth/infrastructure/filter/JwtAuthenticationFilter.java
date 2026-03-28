package com.datavet.auth.infrastructure.filter;

import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.infrastructure.security.AuthenticatedUser;
import com.datavet.auth.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            try {
                Claims claims = jwtUtil.parseToken(token);

                String   userId     = claims.getSubject();
                String   employeeId = claims.get("employeeId",  String.class);
                String   clinicId   = claims.get("clinicId",    String.class);
                String   scope      = claims.get("scope",       String.class);
                String   email      = claims.get("email",       String.class);
                String   roleStr    = claims.get("role",        String.class);

                if (roleStr == null) {
                    throw new IllegalArgumentException("Token sin rol — petición rechazada");
                }
                UserRole role = UserRole.valueOf(roleStr);
                String   emailFinal = (email   != null) ? email : userId;

                AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                        userId, employeeId, clinicId, emailFinal, role, scope);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                authenticatedUser,
                                null,
                                authenticatedUser.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.warn("No se pudo autenticar el token JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}