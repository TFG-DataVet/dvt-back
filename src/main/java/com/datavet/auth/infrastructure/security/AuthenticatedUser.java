package com.datavet.auth.infrastructure.security;

import com.datavet.auth.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Representa al usuario autenticado dentro del SecurityContext de Spring.
 * Se construye desde los claims del JWT en el filtro.
 */
@Getter
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {

    private final String   userId;
    private final String   employeeId;
    private final String   clinicId;
    private final String   email;
    private final UserRole role;
    private final String   scope;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String  getPassword()                  { return null; }
    @Override public String  getUsername()                  { return email; }
    @Override public boolean isAccountNonExpired()          { return true; }
    @Override public boolean isAccountNonLocked()           { return true; }
    @Override public boolean isCredentialsNonExpired()      { return true; }
    @Override public boolean isEnabled()                    { return true; }

    public boolean isSuperAdmin() {
        return role == UserRole.SUPER_ADMIN;
    }

    public boolean isClinicOwner() {
        return role == UserRole.CLINIC_OWNER;
    }

    public boolean hasFullAccess() {
        return "FULL_ACCESS".equals(scope);
    }

    public boolean hasOnboardingAccess() {
        return "ONBOARDING_ONLY".equals(scope);
    }
}