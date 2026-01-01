package com.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String SECRET =
        "THIS_IS_VMS_SUPER_SECURE_32BYTE_LONG_SECRET_KEY_2025";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
    	String internalCall = request.getHeader("X-Internal-Call");
    	if ("true".equals(internalCall)) return true;


        if (request.getDispatcherType() != DispatcherType.REQUEST)
            return true;

        String path = request.getRequestURI();

        if (path.startsWith("/api/auth"))
            return true;

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        try {
            String token = header.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);

            if ("ADMIN".equals(role)) return true;

            if ("MANAGER".equals(role)) {
                if (path.contains("/assign") ||
                    path.contains("/parts/approve") ||
                    path.startsWith("/api/bays") ||
                    path.contains("/api/service-requests/manager") ||
                    (path.contains("/status") && path.contains("CLOSED")))
                    return true;
            }

            if ("TECHNICIAN".equals(role)) {
                if (path.contains("/status") ||
                	path.contains("/service-requests/technician") ||
                    path.contains("/parts/request"))
                    return true;
            }

            if ("CUSTOMER".equals(role)) {
                if (path.equals("/api/service-requests") ||
                    path.startsWith("/api/service-requests/customer") ||
                    path.startsWith("/api/vehicles") ||
                    path.startsWith("/api/invoices/customer"))
                    return true;
            }

            response.setStatus(403);
            return false;

        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
