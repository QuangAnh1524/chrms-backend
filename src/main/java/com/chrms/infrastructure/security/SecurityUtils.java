package com.chrms.infrastructure.security;

import com.chrms.domain.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        if (userId instanceof Integer integerId) {
            return integerId.longValue();
        }
        return null;
    }

    public static Role getUserRole(HttpServletRequest request) {
        Object roleAttr = request.getAttribute("userRole");
        if (roleAttr instanceof Role role) {
            return role;
        }
        if (roleAttr instanceof String roleName) {
            try {
                return Role.valueOf(roleName);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }
}

